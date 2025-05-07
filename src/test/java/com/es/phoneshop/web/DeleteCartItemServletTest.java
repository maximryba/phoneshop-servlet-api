package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.cart.DefaultCartService;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.PriceHistory;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DeleteCartItemServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;

    @Mock
    private ServletConfig servletConfig;

    private ProductDao productDao;


    @Mock
    private HttpSession session;

    private final DeleteCartItemServlet servlet = new DeleteCartItemServlet();

    @Before
    public void setup() throws ServletException {
        servlet.init(servletConfig);
        productDao = ArrayListProductDao.getInstance();
        Currency usd = Currency.getInstance("USD");
        List<PriceHistory> priceHistories = new ArrayList<>();
        priceHistories.add(new PriceHistory
                (1L, BigDecimal.valueOf(50), LocalDate.of(2024, 12, 5), usd));
        priceHistories.add(new PriceHistory
                (2L, BigDecimal.valueOf(100), LocalDate.of(2025, 1, 10), usd));
        productDao.save(new Product( "test-phone", "Test", BigDecimal.valueOf(100), usd, 100, "test", priceHistories));
        productDao.save(new Product( "test-phone", "Test", BigDecimal.valueOf(100), usd, 100, "test", priceHistories));
        productDao.save(new Product( "test-phone", "Test", BigDecimal.valueOf(100), usd, 100, "test", priceHistories));
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(request.getSession()).thenReturn(session);
    }

    @Test
    public void testDoPost() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/phoneshop/cart/deleteCartItem/0");
        Cart cart = new Cart();
        cart.getItems().add(new CartItem(productDao.get(0L), 3));

        when(session.getAttribute(DefaultCartService.class.getName() + ".cart")).thenReturn(cart);
        servlet.doPost(request, response);

        verify(request).setAttribute("successDelete", true);
        verify(requestDispatcher).forward(request, response);
    }
}
