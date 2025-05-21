package com.es.phoneshop.web;

import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.PriceHistory;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import jakarta.servlet.ServletConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AdvancedSearchPageServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private ServletConfig servletConfig;

    private ProductDao productDao;
    private AdvancedSearchPageServlet servlet;

    @Before
    public void setup() throws ServletException {
        servlet = new AdvancedSearchPageServlet();
        servlet.init(servletConfig);
        productDao = ArrayListProductDao.getInstance();

        Currency usd = Currency.getInstance("USD");
        List<PriceHistory> priceHistories = new ArrayList<>();
        priceHistories.add(new PriceHistory(1L, BigDecimal.valueOf(100), LocalDate.now(), usd));

        productDao.save(new Product( "iphone", "iPhone 6", BigDecimal.valueOf(300), usd, 10, "image.jpg", priceHistories));
        productDao.save(new Product( "samsung", "Samsung Galaxy S", BigDecimal.valueOf(200), usd, 5, "image.jpg", priceHistories));

        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
    }

    @Test
    public void testDoGetWithValidParameters() throws ServletException, IOException {
        when(request.getParameter("description")).thenReturn("iphone");
        when(request.getParameter("min-price")).thenReturn("100");
        when(request.getParameter("max-price")).thenReturn("400");
        when(request.getParameter("search-criteria")).thenReturn("all");
        when(request.getLocale()).thenReturn(Locale.getDefault());

        servlet.doGet(request, response);

        verify(request).setAttribute(eq("products"), any());
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGetWithInvalidMinPrice() throws ServletException, IOException {
        when(request.getParameter("description")).thenReturn("iphone");
        when(request.getParameter("min-price")).thenReturn("abc");
        when(request.getParameter("max-price")).thenReturn("400");
        when(request.getLocale()).thenReturn(Locale.getDefault());

        servlet.doGet(request, response);

        verify(request).setAttribute(eq("errors"), any());
        verify(request).setAttribute(eq("products"), any());
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGetWithNegativePrice() throws ServletException, IOException {
        when(request.getParameter("description")).thenReturn("iphone");
        when(request.getParameter("min-price")).thenReturn("-100");
        when(request.getParameter("max-price")).thenReturn("400");
        when(request.getLocale()).thenReturn(Locale.getDefault());

        servlet.doGet(request, response);

        verify(request).setAttribute(eq("errors"), any());
        verify(request).setAttribute(eq("products"), any());
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGetWithInvalidPriceRange() throws ServletException, IOException {
        when(request.getParameter("description")).thenReturn("iphone");
        when(request.getParameter("min-price")).thenReturn("500");
        when(request.getParameter("max-price")).thenReturn("400");
        when(request.getLocale()).thenReturn(Locale.getDefault());

        servlet.doGet(request, response);

        verify(request).setAttribute(eq("errors"), any());
        verify(request).setAttribute(eq("products"), any());
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGetWithEmptyParameters() throws ServletException, IOException {
        when(request.getLocale()).thenReturn(Locale.getDefault());
        when(request.getParameter("search-criteria")).thenReturn("all");
        servlet.doGet(request, response);

        verify(request).setAttribute(eq("products"), any());
        verify(requestDispatcher).forward(request, response);
    }

}