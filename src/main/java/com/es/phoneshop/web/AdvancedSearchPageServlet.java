package com.es.phoneshop.web;

import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.ProductDao;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdvancedSearchPageServlet extends HttpServlet {

    private static final String WEB_INF_PAGES_ADVANCED_SEARCH_JSP = "/WEB-INF/pages/advancedSearch.jsp";
    private static final String PRODUCTS = "products";
    private static final String QUERY = "description";
    private static final String MIN_PRICE = "min-price";
    private static final String MAX_PRICE = "max-price";
    private static final String SEARCH_CRITERIA = "search-criteria";

    private ProductDao productDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productDao = ArrayListProductDao.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String query = request.getParameter(QUERY);
        String maxPriceString = request.getParameter(MAX_PRICE);
        String minPriceString = request.getParameter(MIN_PRICE);
        String criteria = request.getParameter(SEARCH_CRITERIA);
        Integer maxPrice = null, minPrice = null;
        Map<String, String> errors = new HashMap<>();
        NumberFormat format = NumberFormat.getInstance(request.getLocale());

        if (maxPriceString != null && !maxPriceString.isEmpty()) {
            try {
                maxPrice = format.parse(maxPriceString).intValue();
                if (maxPrice < 0) {
                    errors.put("max-price", "Price cannot be negative");
                }
            } catch (ParseException | NumberFormatException exception) {
                errors.put("max-price", "Not a number");
            }
        }

        if (minPriceString != null && !minPriceString.isEmpty()) {
            try {
                minPrice = format.parse(minPriceString).intValue();
                if (minPrice < 0) {
                    errors.put("min-price", "Price cannot be negative");
                }
            } catch (ParseException | NumberFormatException exception) {
                errors.put("min-price", "Not a number");
            }
        }
        if (maxPrice != null && minPrice != null && maxPrice < minPrice) {
            errors.put("price-range", "Max price must be greater than min price");
        }

        if (errors.isEmpty()) {
            request.setAttribute("products", productDao.advancedSearchProducts(query, minPrice, maxPrice, criteria));
        } else {
            request.setAttribute(PRODUCTS, new ArrayList<>());
            request.setAttribute("errors", errors);
        }
        request.getRequestDispatcher(WEB_INF_PAGES_ADVANCED_SEARCH_JSP).forward(request, response);
    }

}
