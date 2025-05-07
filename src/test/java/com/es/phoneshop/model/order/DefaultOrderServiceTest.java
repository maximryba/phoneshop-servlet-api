package com.es.phoneshop.model.order;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.product.PriceHistory;
import com.es.phoneshop.model.product.Product;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class DefaultOrderServiceTest {

    private OrderService orderService;
    private Cart cart;
    private List<CartItem> cartItems;


    @Before
    public void setup() {
        orderService = DefaultOrderService.getInstance();
        cart = new Cart();
        cartItems = new ArrayList<>();
        cartItems.add(new CartItem(new Product(0L, "test", "TEST", new BigDecimal(100), Currency.getInstance("USD"), 100, "test", new ArrayList<PriceHistory>()), 3));
        cart.setItems(cartItems);
        cart.setTotalCost(new BigDecimal(100));
        cart.setTotalQuantity(3);
    }

    @Test
    public void testGetOrder() {
        Order order = orderService.getOrder(cart);
        assertEquals("TEST", order.getItems().get(0).getProduct().getDescription());
        assertEquals(new BigDecimal(5), order.getDeliveryCost());
        assertEquals(new BigDecimal(105), order.getTotalCost());
    }

    @Test
    public void testPlaceOrder() {
        Order order = orderService.getOrder(cart);
        assertNull(order.getSecureId());
        orderService.placeOrder(order);
        assertNotNull(order.getSecureId());
    }
}
