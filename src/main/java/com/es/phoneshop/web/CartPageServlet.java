package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;
import com.es.phoneshop.model.cart.OutOfStockException;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.utils.UriUtil;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class CartPageServlet extends HttpServlet {
    private static final String PRODUCT = "product";
    private static final String WEB_INF_CART_JSP = "/WEB-INF/pages/cart.jsp";
    public static final String QUANTITY = "quantity";
    public static final String CART = "cart";
    public static final String PRODUCTS = "lastProducts";
    private ProductDao productDao;
    private CartService cartService;
    private List<Long> lastViewedProducts;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        productDao = ArrayListProductDao.getInstance();
        cartService = DefaultCartService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Cart cart = cartService.getCart(request);
        request.setAttribute("cart", cart);
        request.getRequestDispatcher(WEB_INF_CART_JSP).forward(request, response);
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
        Long productId = UriUtil.getProductId(request.getRequestURI());
        Cart cart = cartService.getCart(request);
        try {
            cartService.add(cart, productId, quantity);
        } catch (OutOfStockException e) {
            request.setAttribute("error", "Out of stock, available "
                    + e.getStockAvailable() + ", requested " + e.getStockRequested());
            doGet(request, response);
            return;
        }
        response.sendRedirect(request.getContextPath() + "/products/" + productId + "?message=Product added to cart");
    }
}
