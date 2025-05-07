package com.es.phoneshop.model.cart;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public interface CartService {
    Cart getCart(HttpServletRequest request);
    void add(Cart cart, Long productId, int quantity);
    void update(Cart cart, Long productId, int quantity);

    void delete(Cart cart, Long productId);

    void deleteCart(HttpSession session);
}
