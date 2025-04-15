package com.es.phoneshop.web;

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
import java.util.Locale;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CartPageServletTest {
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

    private final CartPageServlet servlet = new CartPageServlet();

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
    public void testDoGet() throws ServletException, IOException {
        servlet.doGet(request, response);

        verify(request).setAttribute(eq("cart"), any());
        verify(requestDispatcher).forward(request, response);

    }

    @Test
    public void testDoPostIfNoErrors() throws ServletException, IOException {
        Locale locale = Locale.getDefault();
        when(request.getLocale()).thenReturn(locale);
        when(request.getParameterValues("productId")).thenReturn(new String[]{"1", "2"});
        when(request.getParameterValues("quantity")).thenReturn(new String[]{"10", "20"});

        servlet.doPost(request, response);

        verify(request).setAttribute("success", true);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoPostIfErrors() throws ServletException, IOException {
        Locale locale = Locale.getDefault();
        when(request.getLocale()).thenReturn(locale);
        Map<Long, String> errors = new HashMap<>();
        errors.put(1L, "Not a number");
        when(request.getParameterValues("productId")).thenReturn(new String[]{"1", "2"});
        when(request.getParameterValues("quantity")).thenReturn(new String[]{"eee", "20"});

        servlet.doPost(request, response);

        verify(request).setAttribute("errors", errors);
    }
}
