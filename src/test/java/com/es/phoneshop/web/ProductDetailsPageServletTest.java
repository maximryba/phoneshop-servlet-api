package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.OutOfStockException;
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
import org.junit.Assert;
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
import java.util.Locale;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductDetailsPageServletTest {
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

    private final ProductDetailsPageServlet productDetailsPageServlet = new ProductDetailsPageServlet();

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
        productDetailsPageServlet.init(servletConfig);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(request.getSession()).thenReturn(session);
    }

    @Test
    public void testDoGet() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/phoneshop/products/1");
        productDetailsPageServlet.doGet(request, response);
        verify(request).setAttribute(eq("product"), any());
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoPostIfValidQuantity() throws Exception {
        Locale locale = Locale.getDefault();
        when(request.getLocale()).thenReturn(locale);
        when(request.getParameter(ProductDetailsPageServlet.QUANTITY)).thenReturn("2");
        when(request.getRequestURI()).thenReturn("/phoneshop/products/1");
        when(request.getContextPath()).thenReturn("/phoneshop");

        productDetailsPageServlet.doPost(request, response);

        verify(response).sendRedirect( "/phoneshop/products/1?message=Product added to cart");

    }

    @Test
    public void testDoPostIfInvalidQuantity() throws Exception {
        when(request.getParameter(ProductDetailsPageServlet.QUANTITY)).thenReturn("invalid");
        when(request.getLocale()).thenReturn(Locale.getDefault());
        when(request.getRequestURI()).thenReturn("/phoneshop/products/1");
        when(session.getAttribute(ProductDetailsPageServlet.PRODUCTS)).thenReturn(new ArrayList<Long>());

        productDetailsPageServlet.doPost(request, response);

        verify(request).setAttribute("error", "Not a number");
        verify(requestDispatcher).forward(request, response);
    }

    @Test(expected = OutOfStockException.class)
    public void testDoPostIfOutOfStock() throws Exception {
        when(request.getLocale()).thenReturn(Locale.getDefault());
        when(request.getParameter(ProductDetailsPageServlet.QUANTITY)).thenReturn("102");
        when(request.getRequestURI()).thenReturn("/phoneshop/products/2");

        productDetailsPageServlet.doPost(request, response);

        verify(request).setAttribute("error", "Out of stock, available 100, requested 102");
    }

    @Test
    public void testDoGetIfLastProductsAreFull() throws ServletException, IOException {
        List<PriceHistory> priceHistories = new ArrayList<>();
        priceHistories.add(new PriceHistory
                (1L, BigDecimal.valueOf(50), LocalDate.of(2024, 12, 5), Currency.getInstance("USD")));
        productDao.save(new Product( "test-phone", "Test", BigDecimal.valueOf(100), Currency.getInstance("USD"), 100, "test", priceHistories));
        when(request.getRequestURI()).thenReturn("/phoneshop/products/3");
        List<Long> ids = new ArrayList<>();
        ids.add(0L);
        ids.add(1L);
        ids.add(2L);
        when(session.getAttribute(ProductDetailsPageServlet.PRODUCTS)).thenReturn(ids);
        productDetailsPageServlet.doGet(request, response);
        List<Long> newIds = (List<Long>) session.getAttribute(ProductDetailsPageServlet.PRODUCTS);
        Assert.assertEquals(Long.valueOf(3), newIds.get(0));
        Assert.assertEquals(Long.valueOf(0), newIds.get(1));
        Assert.assertEquals(Long.valueOf(1), newIds.get(2));

        Assert.assertEquals(3, newIds.size());
        verify(request).setAttribute(eq("product"), any());
        verify(requestDispatcher).forward(request, response);
    }
}
