package com.es.phoneshop.model.product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class ArrayListProductDao implements ProductDao {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private long maxId;
    private final List<Product> products = new ArrayList<>();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();

    public ArrayListProductDao() {
        saveSampleProducts();
    }

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
    public List<Product> findProducts(String query) {
        readLock.lock();
        try {
            return this.products.stream()
                    .filter(product -> query == null || query.isEmpty() || productHasDescription(product, query))
                    .filter(product -> product.getPrice() != null)
                    .filter(this::productIsInStock)
                    .sorted((a, b) -> Integer.compare(countMatchesInQuery(b, query),
                            countMatchesInQuery(a, query)))
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

    private void saveSampleProducts() {
        Currency usd = Currency.getInstance("USD");
        save(new Product("sgs", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg"));
        save(new Product("sgs2", "Samsung Galaxy S II", new BigDecimal(200), usd, 0, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20II.jpg"));
        save(new Product("sgs3", "Samsung Galaxy S III", new BigDecimal(300), usd, 5, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20III.jpg"));
        save(new Product("iphone", "Apple iPhone", new BigDecimal(200), usd, 10, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone.jpg"));
        save(new Product( "iphone6", "Apple iPhone 6", new BigDecimal(1000), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone%206.jpg"));
        save(new Product("htces4g", "HTC EVO Shift 4G", new BigDecimal(320), usd, 3, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/HTC/HTC%20EVO%20Shift%204G.jpg"));
        save(new Product("sec901", "Sony Ericsson C901", new BigDecimal(420), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Ericsson%20C901.jpg"));
        save(new Product("xperiaxz", "Sony Xperia XZ", new BigDecimal(120), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Xperia%20XZ.jpg"));
        save(new Product("nokia3310", "Nokia 3310", new BigDecimal(70), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Nokia/Nokia%203310.jpg"));
        save(new Product("palmp", "Palm Pixi", new BigDecimal(170), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Palm/Palm%20Pixi.jpg"));
        save(new Product("simc56", "Siemens C56", new BigDecimal(70), usd, 20, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C56.jpg"));
        save(new Product("simc61", "Siemens C61", new BigDecimal(80), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C61.jpg"));
        save(new Product("simsxg75", "Siemens SXG75", new BigDecimal(150), usd, 40, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg"));
    }
}
