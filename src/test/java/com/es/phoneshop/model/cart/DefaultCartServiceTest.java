package com.es.phoneshop.model.cart;

import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class DefaultCartServiceTest {

    private CartService cartService;

    private ProductDao productDao;

    private final static String CART = DefaultCartService.class.getName() + ".cart";

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpSession session;

    @Before
    public void setUp() {
        cartService = DefaultCartService.getInstance();
        productDao = ArrayListProductDao.getInstance();
        request = mock(HttpServletRequest.class);
        session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(request.getSession().getAttribute("cart")).thenReturn(new Cart());
    }

    @Test
    public void testGetCartIfCartExists() {
        Cart existingCart = new Cart();
        when(session.getAttribute(CART)).thenReturn(existingCart);

        Cart cart = cartService.getCart(request);

        assertNotNull(cart);
        verify(session, never()).setAttribute(CART, new Cart());
    }

    @Test
    public void testGetCartIfCartDoesNotExist() {
        when(session.getAttribute(CART)).thenReturn(null);

        Cart cart = cartService.getCart(request);

        assertNotNull(cart);
        verify(session).setAttribute(CART, cart);
    }

    @Test
    public void testAddProductToCartWhenSuccess() throws OutOfStockException {
        Product product = new Product("Test Product", "Description", BigDecimal.valueOf(100), Currency.getInstance("USD"), 10, "imageUrl", new ArrayList<>());
        productDao.save(product);

        Cart cart = new Cart();

        cartService.add(cart, product.getId(), 2);

        assertEquals(1, cart.getItems().size());
        CartItem cartItem = cart.getItems().get(0);
        assertEquals(product, cartItem.getProduct());
        assertEquals(2, cartItem.getQuantity());
        assertEquals(8, product.getStock());
    }

    @Test(expected = OutOfStockException.class)
    public void testAddProductToCartWhenOutOfStock() throws OutOfStockException {
        Product product = new Product("Test Product", "Description", BigDecimal.valueOf(100), Currency.getInstance("USD"), 1, "imageUrl", new ArrayList<>());
        productDao.save(product);

        Cart cart = new Cart();

        cartService.add(cart, product.getId(), 2);
    }
}
