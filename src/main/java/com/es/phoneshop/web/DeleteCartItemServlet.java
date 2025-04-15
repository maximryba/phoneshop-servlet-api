package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;
import com.es.phoneshop.utils.UriUtil;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class DeleteCartItemServlet extends HttpServlet {
    private static final String SUCCESS_DELETE = "successDelete";
    private static final String CART = "cart";
    private CartService cartService;
    private static final String WEB_INF_CART_JSP = "/WEB-INF/pages/cart.jsp";

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        cartService = DefaultCartService.getInstance();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        Long productId = UriUtil.getEntityId(request.getRequestURI());
        Cart cart = cartService.getCart(request);
        cartService.delete(cart, productId);
        request.setAttribute(SUCCESS_DELETE, true);
        request.setAttribute(CART, cartService.getCart(request));
        request.getRequestDispatcher(WEB_INF_CART_JSP).forward(request, response);
    }
}
