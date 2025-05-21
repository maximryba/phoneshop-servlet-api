package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;
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

public class CartPageServlet extends HttpServlet {
    private static final String WEB_INF_CART_JSP = "/WEB-INF/pages/cart.jsp";
    private static final String QUANTITY = "quantity";
    private static final String PRODUCT_ID = "productId";

    private CartService cartService;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
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
        String[] productIds = request.getParameterValues(PRODUCT_ID);
        String[] quantities = request.getParameterValues(QUANTITY);
        Map<Long, String> errors = new HashMap<>();
        Cart cart = cartService.getCart(request);
        for (int i = 0; i < productIds.length; i++) {
            Long productId = Long.valueOf(productIds[i]);
            int quantity;
            try {
                NumberFormat format = NumberFormat.getInstance(request.getLocale());
                quantity = format.parse(quantities[i]).intValue();

                cartService.update(cart, productId, quantity);
            } catch (ParseException e) {
                errors.put(productId, "Not a number");
            }
        }
        if (errors.isEmpty()) {
            request.setAttribute("success", true);
            request.setAttribute("cart", cartService.getCart(request));
            request.getRequestDispatcher(WEB_INF_CART_JSP).forward(request, response);
        } else {
            request.setAttribute("errors", errors);
            doGet(request, response);
        }
    }
}
