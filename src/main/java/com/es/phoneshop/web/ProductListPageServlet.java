package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.product.SortField;
import com.es.phoneshop.model.product.SortOrder;
import com.es.phoneshop.utils.UriUtil;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ProductListPageServlet extends HttpServlet {

    private static final String WEB_INF_PAGES_PRODUCT_LIST_JSP = "/WEB-INF/pages/productList.jsp";
    private static final String PRODUCTS = "products";
    private static final String QUERY = "query";
    private static final String QUANTITY = "quantity_";
    public static final String PRODUCT_ID = "productId";
    private CartService cartService;
    private ProductDao productDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productDao = ArrayListProductDao.getInstance();
        cartService = DefaultCartService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String sortField = request.getParameter("sort");
        String sortOrder = request.getParameter("order");
        request.setAttribute(PRODUCTS, productDao.findProducts(request.getParameter(QUERY),
                Optional.ofNullable(sortField).map(SortField::fromValue).orElse(null),
                Optional.ofNullable(sortOrder).map(SortOrder::fromValue)
                        .orElse(SortOrder.fromValue(SortOrder.ASC.getValue()))
        ));
        request.getRequestDispatcher(WEB_INF_PAGES_PRODUCT_LIST_JSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long productId = Long.parseLong(request.getParameter(PRODUCT_ID));
        String quantityString = request.getParameter(QUANTITY + productId);

        Map<Long, String> errors = new HashMap<>();

        int quantity;
        try {
            NumberFormat format = NumberFormat.getInstance(request.getLocale());
            quantity = format.parse(quantityString).intValue();
            Cart cart = cartService.getCart(request);
            cartService.add(cart, productId, quantity);
        } catch (ParseException | NumberFormatException exception) {
            errors.put(productId, "Not a number");
        }
        if (errors.isEmpty()) {
            String productDescription = productDao.get(productId).getDescription();
            request.setAttribute("success", true);
            request.setAttribute("description", productDescription);
        } else {
            request.setAttribute("errors", errors);
        }
        doGet(request, response);
    }
}
