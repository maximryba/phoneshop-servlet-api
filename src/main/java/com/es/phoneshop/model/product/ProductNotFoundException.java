package com.es.phoneshop.model.product;

public class ProductNotFoundException extends RuntimeException {

    private final Long id;

    public Long getId() {
        return this.id;
    }

    public ProductNotFoundException(Long id) {
        super("Product with id " + id + " not found");
        this.id = id;
    }
}
