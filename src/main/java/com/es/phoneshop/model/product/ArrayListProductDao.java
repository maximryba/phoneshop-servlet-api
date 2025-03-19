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

    private static ProductDao instance;

    public static synchronized ProductDao getInstance() {
        if (instance == null) {
            instance = new ArrayListProductDao();
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
                    .findAny()
                    .orElseThrow(() -> new ProductNotFoundException(id));

        } finally {
            lock.readLock().unlock();
        }

    }

    @Override
    public List<Product> findProducts(String query, SortField sortField, SortOrder sortOrder) {
        lock.readLock().lock();
        try {
            Comparator<Product> comparator = getProductComparator(sortField);
            boolean asc = sortOrder.name().equals("asc");
            return this.products.stream()
                    .filter(product -> query == null || query.isEmpty() || productHasDescription(product, query))
                    .filter(product -> product.getPrice() != null)
                    .filter(product -> product.getStock() > 0)
                    .sorted((a, b) -> Integer.compare(countMatchesInQuery(b, query),
                            countMatchesInQuery(a, query)))
                    .sorted(asc ? comparator : comparator.reversed())
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }

    private boolean productHasDescription(Product product, String query) {
        if (query != null) {
            String[] words = query.split(" ");
            return Arrays.stream(words)
                    .allMatch(word -> product.getDescription().toLowerCase().contains(word.toLowerCase()));
        } else {
            return true;
        }

    }

    private Comparator<Product> getProductComparator(SortField sortField) {
        Comparator<Product> comparator = Comparator.comparing(product -> {
            if (SortField.description == sortField) {
                return (Comparable) product.getDescription();
            } else {
                return (Comparable) product.getPrice();
            }
        });
        return comparator;
    }

    private int countMatchesInQuery(Product product, String query) {
        if (query != null) {
            String[] words = query.split(" ");
            return (int) Arrays.stream(words)
                    .filter(word -> product.getDescription().toLowerCase().contains(word.toLowerCase()))
                    .count();
        } else {
            return 0;
        }

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
