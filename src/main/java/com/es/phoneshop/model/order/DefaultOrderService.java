package com.es.phoneshop.model.order;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.cart.CartService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DefaultOrderService implements OrderService {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private OrderDao orderDao = ArrayListOrderDao.getInstance();

    private static volatile OrderService instance;

    public static OrderService getInstance() {
        if (instance == null) {
            synchronized (CartService.class) {
                if (instance == null) {
                    instance = new DefaultOrderService();
                }
            }
        }
        return instance;
    }

    @Override
    public Order getOrder(Cart cart) {
       lock.readLock().lock();
       try {
        Order order = new Order();
        order.setItems(cart.getItems().stream().map(CartItem::clone)
                .toList());
        order.setSubtotal(cart.getTotalCost());
        order.setDeliveryCost(calculateDeliveryCost());
        order.setTotalCost(order.getSubtotal().add(order.getDeliveryCost()));
        return order;
       } finally {
           lock.readLock().unlock();
       }
    }

    @Override
    public List<PaymentMethod> getPaymentMethods() {
        return Arrays.asList(PaymentMethod.values());
    }

    @Override
    public void placeOrder(Order order) {
        lock.writeLock().lock();
        try {
            order.setSecureId(UUID.randomUUID().toString());
            orderDao.save(order);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private BigDecimal calculateDeliveryCost() {
        return new BigDecimal(5);
    }
}
