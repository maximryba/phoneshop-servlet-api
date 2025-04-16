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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LastViewedProductsServletTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private ServletConfig servletConfig;
    @Mock
    private HttpSession session;

    private ProductDao productDao;

    private final LastViewedProductsServlet lastViewedProductsServlet = new LastViewedProductsServlet();

    @Before
    public void setup() throws ServletException {
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
        lastViewedProductsServlet.init(servletConfig);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(request.getSession()).thenReturn(session);
    }

    @Test
    public void testDoGet() throws ServletException, IOException {
        lastViewedProductsServlet.doGet(request, response);
        verify(request).setAttribute(eq(LastViewedProductsServlet.PRODUCTS), any());
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGetIfProductsAreNotNull() throws ServletException, IOException {
        when(request.getSession()).thenReturn(session);
        List<Long> ids = List.of(0L, 1L, 2L);
        when(session.getAttribute(LastViewedProductsServlet.PRODUCTS)).thenReturn(ids);
        List<Product> products = new ArrayList<>();
        products.add(productDao.get(ids.get(0)));
        products.add(productDao.get(ids.get(1)));
        products.add(productDao.get(ids.get(2)));
        lastViewedProductsServlet.doGet(request, response);
        verify(request).setAttribute(LastViewedProductsServlet.PRODUCTS, products);
        verify(requestDispatcher).forward(request, response);
    }
}
