package com.es.phoneshop.model.order;

import com.es.phoneshop.model.general.AbstractDao;

public class ArrayListOrderDao extends AbstractDao<Order> implements OrderDao {

    private static volatile OrderDao instance;

    public static OrderDao getInstance() {
        if (instance == null) {
            synchronized (OrderDao.class) {
                if (instance == null) {
                    instance = new ArrayListOrderDao();
                }
            }
        }
        return instance;
    }

    private ArrayListOrderDao() {
        super();
    }

    @Override
    protected Long getId(Order order) {
        return order.getId();
    }

    @Override
    protected void setId(Order order, long id) {
        order.setId(id);
    }

    @Override
    public Order getOrderBySecureId(String secureId) {
        lock.readLock().lock();
        try {
            return items.stream()
                    .filter(order -> secureId.equals(order.getSecureId()))
                    .findFirst()
                    .orElseThrow(() -> new OrderNotFoundException(secureId));
        } finally {
            lock.readLock().unlock();
        }
    }
}
