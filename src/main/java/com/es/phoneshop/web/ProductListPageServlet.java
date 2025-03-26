package com.es.phoneshop.web;

import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.product.SortField;
import com.es.phoneshop.model.product.SortOrder;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class ProductListPageServlet extends HttpServlet {

    private static final String WEB_INF_PAGES_PRODUCT_LIST_JSP = "/WEB-INF/pages/productList.jsp";
    private static final String PRODUCTS = "products";
    private ProductDao productDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productDao = ArrayListProductDao.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sortField = request.getParameter("sort");
        String sortOrder = request.getParameter("order");
        request.setAttribute(PRODUCTS, productDao.findProducts(request.getParameter("query"),
                Optional.ofNullable(sortField).map(SortField::fromValue).orElse(null),
                Optional.ofNullable(sortOrder).map(SortOrder::fromValue)
                        .orElse(SortOrder.fromValue(SortOrder.ASC.getValue()))
        ));
        request.getRequestDispatcher(WEB_INF_PAGES_PRODUCT_LIST_JSP).forward(request, response);
    }
}
