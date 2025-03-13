package com.es.phoneshop.model.product;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.locks.Lock;
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
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();


    @Override
    public Product getProduct(Long id) throws ProductNotFoundException {
        readLock.lock();
        try {
            return products.stream()
                    .filter(product -> product.getId().equals(id))
                    .findAny()
                    .orElseThrow(() -> new ProductNotFoundException(id));

        } finally {
            readLock.unlock();
        }

    }

    @Override
    public List<Product> findProducts(String query, SortField sortField, SortOrder sortOrder) {
        readLock.lock();
        try {
            Comparator<Product> comparator = Comparator.comparing(product -> {
                if (SortField.description == sortField) {
                    return (Comparable) product.getDescription();
                } else {
                    return (Comparable) product.getPrice();
                }
            });
            boolean asc = sortOrder.name().equals("asc");
            return this.products.stream()
                    .filter(product -> query == null || query.isEmpty() || productHasDescription(product, query))
                    .filter(product -> product.getPrice() != null)
                    .filter(this::productIsInStock)
                    .sorted((a, b) -> Integer.compare(countMatchesInQuery(b, query),
                            countMatchesInQuery(a, query)))
                    .sorted(asc ? comparator : comparator.reversed())
                    .collect(Collectors.toList());
        } finally {
            readLock.unlock();
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

    private boolean productIsInStock(Product product) {
        return product.getStock() > 0;
    }

    @Override
    public void save(Product product) {
        writeLock.lock();
        try {
            if (product.getId() != null) {
                int index = -1;
                for (Product pr : products) {
                    if (pr.getId().equals(product.getId())) {
                       index = pr.getId().intValue();
                    }
                }
                if (index != -1) {
                    products.set(index, product);
                } else {
                    throw new ProductNotFoundException(product.getId());
                }
            } else {
                product.setId(maxId++);
                products.add(product);
            }
        } finally {
            writeLock.unlock();
        }

    }

    @Override
    public void delete(Long id) {
        writeLock.lock();
        try {
            Product foundProduct = getProduct(id);
            products.remove(foundProduct);
        } finally {
            writeLock.unlock();
        }
    }
}
