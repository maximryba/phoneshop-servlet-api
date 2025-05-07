package com.es.phoneshop.model.cart;

import com.es.phoneshop.model.product.Product;

public class OutOfStockException extends RuntimeException {

    public OutOfStockException(Product product, int stockRequested, int stockAvailable) {
        super(String.format("Product ID %d (%s) is not available: requested stock %d, available stock %d",
                product.getId(), product.getDescription(), stockRequested, stockAvailable));
    }
}
