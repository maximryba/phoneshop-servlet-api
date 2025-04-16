package com.es.phoneshop.web;

import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LastViewedProductsServlet extends HttpServlet {
    public static final String PRODUCTS = "lastProducts";
    private ProductDao productDao;
    private static final String WEB_INF_PAGES_LAST_PRODUCTS_JSP = "/WEB-INF/pages/lastThreeViewedProducts.jsp";

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        productDao = ArrayListProductDao.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Long> productsIds = (List<Long>) request.getSession().getAttribute(PRODUCTS);
        List<Product> products = new ArrayList<>();
        if (productsIds != null) {
            productsIds.forEach(id -> products.add(productDao.get(id)));
        }
        request.setAttribute(PRODUCTS, products);
        request.getRequestDispatcher(WEB_INF_PAGES_LAST_PRODUCTS_JSP).forward(request, response);
    }
}
