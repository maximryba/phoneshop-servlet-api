package com.es.phoneshop.model.general;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(Long id) {
        super("Entity with id " + id + " not found");
    }
}
