package com.es.phoneshop.web;

import com.es.phoneshop.model.order.ArrayListOrderDao;
import com.es.phoneshop.model.order.OrderDao;
import com.es.phoneshop.utils.UriUtil;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class OrderOverviewPageServlet extends HttpServlet {
    private static final String WEB_INF_ORDER_OVERVIEW_JSP = "/WEB-INF/pages/orderOverview.jsp";
    public static final String ORDER = "order";
    private OrderDao orderDao;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        orderDao = ArrayListOrderDao.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String secureId = UriUtil.getSecureId(request.getRequestURI());
        request.setAttribute(ORDER, orderDao.getOrderBySecureId(secureId));
        request.getRequestDispatcher(WEB_INF_ORDER_OVERVIEW_JSP).forward(request, response);
    }

}
