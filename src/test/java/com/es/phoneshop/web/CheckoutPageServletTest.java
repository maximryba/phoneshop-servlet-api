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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CheckoutPageServletTest {
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

    private final CheckoutPageServlet servlet = new CheckoutPageServlet();

    @Before
    public void setup() throws ServletException {
        servlet.init(servletConfig);
        productDao = ArrayListProductDao.getInstance();
        Currency usd = Currency.getInstance("USD");
        List<PriceHistory> priceHistories = new ArrayList<>();
        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(new CartItem(new Product( "test-phone", "Test", BigDecimal.valueOf(100), usd, 100, "test", priceHistories), 1));
        cartItems.add(new CartItem(new Product( "test-phone", "Test", BigDecimal.valueOf(100), usd, 100, "test", priceHistories), 1));
        cartItems.add(new CartItem(new Product( "test-phone", "Test", BigDecimal.valueOf(100), usd, 100, "test", priceHistories), 1));
        priceHistories.add(new PriceHistory
                (1L, BigDecimal.valueOf(50), LocalDate.of(2024, 12, 5), usd));
        priceHistories.add(new PriceHistory
                (2L, BigDecimal.valueOf(100), LocalDate.of(2025, 1, 10), usd));
        productDao.save(new Product( "test-phone", "Test", BigDecimal.valueOf(100), usd, 100, "test", priceHistories));
        productDao.save(new Product( "test-phone", "Test", BigDecimal.valueOf(100), usd, 100, "test", priceHistories));
        productDao.save(new Product( "test-phone", "Test", BigDecimal.valueOf(100), usd, 100, "test", priceHistories));
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(request.getSession()).thenReturn(session);
        Cart cart = new Cart();
        cart.setItems(cartItems);
        cart.setTotalQuantity(3);
        cart.setTotalCost(new BigDecimal(300));
        when(request.getSession().getAttribute(DefaultCartService.class.getName() + ".cart")).thenReturn(cart);

    }

    @Test
    public void testDoGet() throws ServletException, IOException {

        servlet.doGet(request, response);

        verify(request).setAttribute(eq("order"), any());
        verify(request).setAttribute(eq("paymentMethods"), any());
        verify(requestDispatcher).forward(request, response);

    }

    @Test
    public void testDoPostIfNoErrors() throws ServletException, IOException {
        when(request.getContextPath()).thenReturn("/phoneshop");
        when(request.getParameter("firstName")).thenReturn("A");
        when(request.getParameter("lastName")).thenReturn("A");
        when(request.getParameter("deliveryDate")).thenReturn("2025-05-01");
        when(request.getParameter("deliveryAddress")).thenReturn("A");
        when(request.getParameter("phone")).thenReturn("375291111111");
        when(request.getParameter("paymentMethod")).thenReturn("CACHE");

        servlet.doPost(request, response);
        verify(response).sendRedirect(any());
    }

    @Test
    public void testDoPostIfValueIsEmpty() throws ServletException, IOException {
        Map<String, String> errors = new HashMap<>();
        errors.put("firstName", "Value is required");
        when(request.getParameter("lastName")).thenReturn("A");
        when(request.getParameter("deliveryDate")).thenReturn("2025-05-01");
        when(request.getParameter("deliveryAddress")).thenReturn("A");
        when(request.getParameter("phone")).thenReturn("375291111111");
        when(request.getParameter("paymentMethod")).thenReturn("CACHE");

        servlet.doPost(request, response);

        verify(request).setAttribute("errors", errors);
        verify(request).getRequestDispatcher(any());
    }

    @Test
    public void testDoPostIfDateIsIncorrect() throws ServletException, IOException {
        Map<String, String> errors = new HashMap<>();
        errors.put("deliveryDate", "Date cannot be in the past");
        when(request.getParameter("firstName")).thenReturn("A");
        when(request.getParameter("lastName")).thenReturn("A");
        when(request.getParameter("deliveryDate")).thenReturn("2020-05-01");
        when(request.getParameter("deliveryAddress")).thenReturn("A");
        when(request.getParameter("phone")).thenReturn("375291111111");
        when(request.getParameter("paymentMethod")).thenReturn("CACHE");

        servlet.doPost(request, response);

        verify(request).setAttribute("errors", errors);
        verify(request).getRequestDispatcher(any());
    }

    @Test
    public void testDoPostIfPhoneIsIncorrect() throws ServletException, IOException {
        Map<String, String> errors = new HashMap<>();
        errors.put("phone", "Phone number should consists of 12 digits");
        when(request.getParameter("firstName")).thenReturn("A");
        when(request.getParameter("lastName")).thenReturn("A");
        when(request.getParameter("deliveryDate")).thenReturn("2025-05-01");
        when(request.getParameter("deliveryAddress")).thenReturn("A");
        when(request.getParameter("phone")).thenReturn("37529111111");
        when(request.getParameter("paymentMethod")).thenReturn("CACHE");

        servlet.doPost(request, response);

        verify(request).setAttribute("errors", errors);
        verify(request).getRequestDispatcher(any());
    }

    @Test
    public void testDoPostIfInvalidDateFormat() throws ServletException, IOException {
        Map<String, String> errors = new HashMap<>();
        errors.put("deliveryDate", "Invalid date format");
        when(request.getParameter("firstName")).thenReturn("A");
        when(request.getParameter("lastName")).thenReturn("A");
        when(request.getParameter("deliveryDate")).thenReturn("2025-5-01");
        when(request.getParameter("deliveryAddress")).thenReturn("A");
        when(request.getParameter("phone")).thenReturn("375291111111");
        when(request.getParameter("paymentMethod")).thenReturn("CACHE");

        servlet.doPost(request, response);

        verify(request).setAttribute("errors", errors);
        verify(request).getRequestDispatcher(any());
    }
}
