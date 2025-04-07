package com.es.phoneshop.model.product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class ArrayListProductDao implements ProductDao {

    private static volatile ProductDao instance;

    public static ProductDao getInstance() {
        if (instance == null) {
            synchronized (ProductDao.class) {
                if (instance == null) {
                    instance = new ArrayListProductDao();
                }
            }
        }
        return instance;
    }

    private ArrayListProductDao() {
        products = new ArrayList<>();
    }

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private long maxId;
    private final List<Product> products;

    @Override
    public Product getProduct(Long id){
        lock.readLock().lock();
        try {
            return products.stream()
                    .filter(product -> product.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new ProductNotFoundException(id));

        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<Product> findProducts(String query, SortField sortField, SortOrder sortOrder) {
        lock.readLock().lock();
        try {
            if ((query == null || query.isEmpty()) && sortField != null) {
                return findProductsWithoutQuery(sortField, sortOrder);
            } else if (query == null || query.isEmpty()) {
                return findProductsWithoutQueryAndSorting();
            } else if (sortField == null) {
                return findProductsWithQueryWithoutSorting(query);
            } else {
                return findProductsWithQueryAndSorting(query, sortField, sortOrder);
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    private List<Product> findProductsWithoutQueryAndSorting() {
        return this.products.stream()
                .filter(product -> product.getPrice() != null && product.getStock() > 0)
                .collect(Collectors.toList());
    }

    private List<Product> findProductsWithoutQuery(SortField sortField, SortOrder sortOrder) {
        Comparator<Product> comparator = getProductComparator(sortField);
        boolean asc = sortOrder.getValue().equals("asc");
        return this.products.stream()
                .filter(product -> product.getPrice() != null && product.getStock() > 0)
                .sorted(asc ? comparator : comparator.reversed())
                .collect(Collectors.toList());
    }

    private List<Product> findProductsWithQueryWithoutSorting(String query) {
        return this.products.stream()
                .filter(product -> product.getPrice() != null && product.getStock() > 0)
                .filter(product -> matchDescription(product, query, true) == 1)
                .sorted((a, b) -> Integer.compare(matchDescription(b, query, false),
                        matchDescription(a, query, false)))
                .collect(Collectors.toList());
    }

    private List<Product> findProductsWithQueryAndSorting(String query, SortField sortField, SortOrder sortOrder) {
        Comparator<Product> comparator = getProductComparator(sortField);
        boolean asc = sortOrder.getValue().equals("asc");
        return this.products.stream()
                .filter(product -> product.getPrice() != null && product.getStock() > 0)
                .filter(product -> matchDescription(product, query, true) == 1)
                .sorted(asc ? comparator : comparator.reversed())
                .collect(Collectors.toList());
    }

    private int matchDescription(Product product, String query, boolean checkAll) {
        if (query == null || query.isEmpty()) {
            return 1;
        }

        String[] words = query.split(" ");
        long matchCount = Arrays.stream(words)
                .filter(word -> product.getDescription().toLowerCase().contains(word.toLowerCase()))
                .count();

        if (checkAll) {
            return matchCount == words.length ? 1 : 0;
        } else {
            return (int) matchCount;
        }
    }

    private Comparator<Product> getProductComparator(SortField sortField) {

        if (sortField == null) {
            return Comparator.comparing(Product::getId);
        }

        Comparator<Product> comparator = Comparator.comparing(product -> {
            if (SortField.DESCRIPTION.getValue().equals(sortField.getValue())) {
                return (Comparable) product.getDescription();
            } else {
                return (Comparable) product.getPrice();
            }
        });
        return comparator;
    }

    @Override
    public void save(Product product) {
        lock.writeLock().lock();
        try {
            Optional.ofNullable(product.getId())
                    .map(id -> products.stream()
                            .filter(pr -> id.equals(pr.getId()))
                            .findFirst()
                            .map(existingProduct -> {
                                int index = products.indexOf(existingProduct);
                                products.set(index, product);
                                return existingProduct;
                            })
                            .orElseThrow(() -> new ProductNotFoundException(id)))
                    .orElseGet(() -> {
                        product.setId(maxId++);
                        products.add(product);
                        return product;
                    });
        } finally {
            lock.writeLock().unlock();
        }

    }

    @Override
    public void delete(Long id) {
        lock.writeLock().lock();
        try {
            products.removeIf(product -> product.getId().equals(id));
        } finally {
            lock.writeLock().unlock();
        }
    }
}
