package com.es.phoneshop.model.order;

public interface OrderDao {
    Order get(Long id);
    Order getOrderBySecureId(String secureId);
    void save(Order order);
}
