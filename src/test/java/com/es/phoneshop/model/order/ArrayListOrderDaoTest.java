package com.es.phoneshop.model.order;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ArrayListOrderDaoTest {

    private OrderDao orderDao;


    @Before
    public void setup() {
        orderDao = ArrayListOrderDao.getInstance();
    }

    @Test
    public void testGetOrderBySecureId() {
        Order order = new Order();
        order.setSecureId("test");
        order.setDeliveryAddress("a");
        orderDao.save(order);
        Order foundOrder = orderDao.getOrderBySecureId("test");
        assertEquals("a", foundOrder.getDeliveryAddress());
        assertEquals("test", foundOrder.getSecureId());
    }

    @Test(expected = OrderNotFoundException.class)
    public void testGetOrderBySecureIdIfNotFound() {
        Order foundOrder = orderDao.getOrderBySecureId("aaa");
    }
}
