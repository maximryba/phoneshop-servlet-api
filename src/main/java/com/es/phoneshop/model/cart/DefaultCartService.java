package com.es.phoneshop.model.cart;

import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DefaultCartService implements CartService {

    private static final String CART_SESSION_ATTRIBUTE = DefaultCartService.class.getName() + ".cart";
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private ProductDao productDao;

    private static volatile CartService instance;

    public static CartService getInstance() {
        if (instance == null) {
            synchronized (CartService.class) {
                if (instance == null) {
                    instance = new DefaultCartService();
                }
            }
        }
        return instance;
    }

    private DefaultCartService() {
        productDao = ArrayListProductDao.getInstance();
    }

    @Override
    public Cart getCart(HttpServletRequest request) {
        lock.readLock().lock();
        try {
            HttpSession session = request.getSession();
            Cart cart = (Cart) session.getAttribute(CART_SESSION_ATTRIBUTE);
            if (cart == null) {
                cart = new Cart();
                session.setAttribute(CART_SESSION_ATTRIBUTE, cart);
            }
            return cart;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void add(Cart cart, Long productId, int quantity){
        lock.writeLock().lock();
        try {
            Product product = productDao.getProduct(productId);
            if (product.getStock() < quantity) {
                throw new OutOfStockException(product, quantity, product.getStock());
            }
            CartItem cartItem = cart.getItems().stream()
                    .filter(item -> product.equals(item.getProduct()))
                    .findFirst()
                    .orElse(null);

            int index = cart.getItems().indexOf(cartItem);
            if (index != -1) {
                cartItem.setQuantity(cartItem.getQuantity() + quantity);
            } else {
                cart.getItems().add(new CartItem(product, quantity));
            }
            product.setStock(product.getStock() - quantity);
            productDao.save(product);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
