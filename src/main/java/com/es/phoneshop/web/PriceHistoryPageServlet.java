package com.es.phoneshop.web;

import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.PriceHistory;
import com.es.phoneshop.model.product.ProductDao;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public class PriceHistoryPageServlet extends HttpServlet {

    private ProductDao productDao;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        productDao = ArrayListProductDao.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String productId = request.getPathInfo().substring(1);
        List<PriceHistory> priceHistories = productDao.getProduct(Long.valueOf(productId)).getPriceHistories();
        request.setAttribute("product", productDao.getProduct(Long.valueOf(productId)));
        request.setAttribute("priceHistories", priceHistories);
        request.getRequestDispatcher("/WEB-INF/pages/priceHistories.jsp").forward(request, response);
    }

}
