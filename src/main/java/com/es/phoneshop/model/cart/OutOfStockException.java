package com.es.phoneshop.model.cart;

import com.es.phoneshop.model.product.Product;

public class OutOfStockException extends RuntimeException {

    public OutOfStockException(Product product, int stockRequested, int stockAvailable) {
        super("Product " + product.getDescription() + " is not available, stock available " +
                stockAvailable + ", and stock requested " + stockRequested);
    }
}
