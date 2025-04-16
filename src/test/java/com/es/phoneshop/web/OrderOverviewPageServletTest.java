package com.es.phoneshop.web;

import com.es.phoneshop.model.order.ArrayListOrderDao;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.order.OrderDao;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrderOverviewPageServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private ServletConfig servletConfig;

    private OrderDao orderDao;

    private final OrderOverviewPageServlet servlet = new OrderOverviewPageServlet();

    @Before
    public void setup() throws ServletException {
        servlet.init(servletConfig);
        orderDao = ArrayListOrderDao.getInstance();
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
    }

    @Test
    public void testDoGet() throws ServletException, IOException {
        String uuid = UUID.randomUUID().toString();
        Order order = new Order();
        order.setSecureId(uuid);
        String uri = request.getContextPath() + "/order/overview/" + order.getSecureId();
        when(request.getRequestURI()).thenReturn(uri);
        orderDao.save(order);
        servlet.doGet(request, response);

        verify(request).setAttribute(eq("order"), any());
        verify(requestDispatcher).forward(request, response);

    }
}
