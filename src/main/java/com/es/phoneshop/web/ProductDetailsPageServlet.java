package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.utils.UriUtil;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class ProductDetailsPageServlet extends HttpServlet {
    private static final String PRODUCT = "product";
    private static final String WEB_INF_PAGES_PRODUCT_JSP = "/WEB-INF/pages/product.jsp";
    public static final String QUANTITY = "quantity";
    public static final String CART = "cart";
    public static final String PRODUCTS = "lastProducts";
    private ProductDao productDao;
    private CartService cartService;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        productDao = ArrayListProductDao.getInstance();
        cartService = DefaultCartService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long productId = UriUtil.getEntityId(request.getRequestURI());
        List<Long> lastViewedProducts = getLastViewedProducts(request, productId);

        request.getSession().setAttribute(PRODUCTS, lastViewedProducts);
        request.setAttribute(PRODUCT, productDao.get(productId));
        request.setAttribute(CART, cartService.getCart(request));
        request.getRequestDispatcher(WEB_INF_PAGES_PRODUCT_JSP).forward(request, response);
    }

    private static List<Long> getLastViewedProducts(HttpServletRequest request, Long productId) {
        HttpSession session = request.getSession();
        List<Long> lastViewedProducts;
        if (session.getAttribute(PRODUCTS) != null) {
            lastViewedProducts = (List<Long>) session.getAttribute(PRODUCTS);
        } else {
            lastViewedProducts = new ArrayList<>();
        }
        lastViewedProducts.add(0, productId);
        if (lastViewedProducts.size() > 2) {
            lastViewedProducts.subList(3, lastViewedProducts.size()).clear();
        }
        return lastViewedProducts;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String quantityString = request.getParameter(QUANTITY);
        int quantity;
        try {
            NumberFormat format = NumberFormat.getInstance(request.getLocale());
            quantity = format.parse(quantityString).intValue();
        } catch (ParseException e) {
            request.setAttribute("error", "Not a number");
            doGet(request, response);
            return;
        }
        Long productId = UriUtil.getEntityId(request.getRequestURI());
        Cart cart = cartService.getCart(request);
        cartService.add(cart, productId, quantity);
        request.setAttribute("success", true);
        doGet(request, response);
    }
}
