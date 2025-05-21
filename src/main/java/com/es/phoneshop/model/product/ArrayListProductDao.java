package com.es.phoneshop.model.product;

import com.es.phoneshop.model.general.AbstractDao;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ArrayListProductDao extends AbstractDao<Product> implements ProductDao {

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
        super();
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

    @Override
    public List<Product> advancedSearchProducts(String query, Integer minPrice, Integer maxPrice, String criteria) {
        lock.readLock().lock();
        try {
            boolean checkAll = criteria.equals("all");

            return this.items.stream()
                    .filter(product -> product.getPrice() != null && product.getStock() > 0)
                    .filter(product -> filterPrices(product, minPrice, maxPrice))
                    .filter(product -> matchDescription(product, query, checkAll) == 1)
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }

    private boolean filterPrices(Product product, Integer minPrice, Integer maxPrice) {
        if (maxPrice != null && minPrice != null) {
            return (product.getPrice().compareTo(BigDecimal.valueOf(minPrice)) >= 0 &&
                    product.getPrice().compareTo(BigDecimal.valueOf(maxPrice)) <= 0);
        } else if (maxPrice != null) {
            return product.getPrice().compareTo(BigDecimal.valueOf(maxPrice)) <= 0;
        } else if (minPrice != null) {
            return product.getPrice().compareTo(BigDecimal.valueOf(minPrice)) >= 0;
        } else {
            return true;
        }
    }

    private List<Product> findProductsWithoutQueryAndSorting() {
        return this.items.stream()
                .filter(product -> product.getPrice() != null && product.getStock() > 0)
                .collect(Collectors.toList());
    }

    private List<Product> findProductsWithoutQuery(SortField sortField, SortOrder sortOrder) {
        Comparator<Product> comparator = getProductComparator(sortField);
        boolean asc = sortOrder.getValue().equals("asc");
        return this.items.stream()
                .filter(product -> product.getPrice() != null && product.getStock() > 0)
                .sorted(asc ? comparator : comparator.reversed())
                .collect(Collectors.toList());
    }

    private List<Product> findProductsWithQueryWithoutSorting(String query) {
        return this.items.stream()
                .filter(product -> product.getPrice() != null && product.getStock() > 0)
                .filter(product -> matchDescription(product, query, true) == 1)
                .sorted((a, b) -> Integer.compare(matchDescription(b, query, false),
                        matchDescription(a, query, false)))
                .collect(Collectors.toList());
    }

    private List<Product> findProductsWithQueryAndSorting(String query, SortField sortField, SortOrder sortOrder) {
        Comparator<Product> comparator = getProductComparator(sortField);
        boolean asc = sortOrder.getValue().equals("asc");
        return this.items.stream()
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
    protected Long getId(Product item) {
        return item.getId();
    }

    @Override
    protected void setId(Product item, long id) {
        item.setId(id);
    }
}
