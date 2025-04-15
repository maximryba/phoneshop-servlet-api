package com.es.phoneshop.web;

import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.PriceHistory;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.utils.UriUtil;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public class PriceHistoryPageServlet extends HttpServlet {

    private static final String PRODUCT = "product";
    private static final String PRICE_HISTORIES = "priceHistories";
    private static final String WEB_INF_PAGES_PRICE_HISTORIES_JSP = "/WEB-INF/pages/priceHistories.jsp";
    private ProductDao productDao;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        productDao = ArrayListProductDao.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long productId = UriUtil.getEntityId(request.getRequestURI());
        List<PriceHistory> priceHistories = productDao.get(productId).getPriceHistories();
        request.setAttribute(PRODUCT, productDao.get(productId));
        request.setAttribute(PRICE_HISTORIES, priceHistories);
        request.getRequestDispatcher(WEB_INF_PAGES_PRICE_HISTORIES_JSP).forward(request, response);
    }

}
