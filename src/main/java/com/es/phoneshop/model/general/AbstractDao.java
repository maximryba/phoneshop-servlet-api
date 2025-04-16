package com.es.phoneshop.model.general;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class AbstractDao<T> {
    protected final ReadWriteLock lock = new ReentrantReadWriteLock();
    protected long maxId;
    protected final List<T> items;

    protected AbstractDao() {
        this.items = new ArrayList<>();
    }

    public T get(Long id) {
        lock.readLock().lock();
        try {
            return items.stream()
                    .filter(item -> getId(item).equals(id))
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException(id));
        } finally {
            lock.readLock().unlock();
        }
    }

    public void save(T item) {
        lock.writeLock().lock();
        try {
            Optional.ofNullable(getId(item))
                    .map(id -> items.stream()
                            .filter(existingItem -> id.equals(getId(existingItem)))
                            .findFirst()
                            .map(existingItem -> {
                                int index = items.indexOf(existingItem);
                                items.set(index, item);
                                return existingItem;
                            })
                            .orElseThrow(() -> new EntityNotFoundException(id)))
                    .orElseGet(() -> {
                        setId(item, maxId++);
                        items.add(item);
                        return item;
                    });
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void delete(Long id) {
        lock.writeLock().lock();
        try {
            items.removeIf(item -> getId(item).equals(id));
        } finally {
            lock.writeLock().unlock();
        }
    }

    protected abstract Long getId(T item);
    protected abstract void setId(T item, long id);
}