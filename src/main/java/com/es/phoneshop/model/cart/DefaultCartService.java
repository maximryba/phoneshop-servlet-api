package com.es.phoneshop.model.cart;

import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import jakarta.servlet.http.HttpServletRequest;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DefaultCartService implements CartService {

    private Cart cart;

    private static final String CART_SESSION_ATTRIBUTE = DefaultCartService.class.getName() + ".cart";
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private ProductDao productDao;

    private static CartService instance;

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
            cart = (Cart) request.getSession().getAttribute(CART_SESSION_ATTRIBUTE);
            if (cart == null) {
                request.getSession().setAttribute(CART_SESSION_ATTRIBUTE, cart = new Cart());
            }
            return cart;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void add(Cart cart, Long productId, int quantity) throws OutOfStockException{
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

            int index = cartItem == null ? -1 : cart.getItems().indexOf(cartItem);
            if (index != -1) {
                cart.getItems().set(index, new CartItem(product, cartItem.getQuantity() + quantity));
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
