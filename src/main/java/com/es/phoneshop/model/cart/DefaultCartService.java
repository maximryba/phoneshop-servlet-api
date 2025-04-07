package com.es.phoneshop.model.cart;

import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.product.ProductNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class DefaultCartService implements CartService {

    private static final String CART_SESSION_ATTRIBUTE = DefaultCartService.class.getName() + ".cart";
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private final ProductDao productDao;

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
            CartItem cartItem = findCartItem(cart, product, quantity);
            int index = cart.getItems().indexOf(cartItem);
            if (index != -1) {
                if (product.getStock() < quantity) {
                    throw new OutOfStockException(product, quantity, product.getStock() - cartItem.getQuantity());
                }
                cartItem.setQuantity(cartItem.getQuantity() + quantity);
            } else {
                if (product.getStock() < quantity) {
                    throw new OutOfStockException(product, quantity, product.getStock());
                }
                cart.getItems().add(new CartItem(product, quantity));
            }
            product.setStock(product.getStock() - quantity);
            productDao.save(product);
            recalculateCart(cart);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void update(Cart cart, Long productId, int quantity){
        lock.writeLock().lock();
        try {
            Product product = productDao.getProduct(productId);

            CartItem cartItem = findCartItem(cart, product, quantity);
            if (cartItem != null) {
                int currentQuantity = cartItem.getQuantity();
                if (quantity > currentQuantity) {

                    int diff = quantity - currentQuantity;
                    if (product.getStock() < diff) {
                        throw new OutOfStockException(product, diff, product.getStock());
                    }
                    cart.getItems().set(cart.getItems().indexOf(cartItem), new CartItem(product, quantity));
                    product.setStock(product.getStock() - diff);
                } else {
                    int diff = currentQuantity - quantity;
                    cart.getItems().set(cart.getItems().indexOf(cartItem), new CartItem(product, quantity));
                    product.setStock(product.getStock() + diff);
                }
                cartItem.setQuantity(quantity);
            } else {
                if (product.getStock() < quantity) {
                    throw new OutOfStockException(product, quantity, product.getStock());
                }
                cart.getItems().add(new CartItem(product, quantity));
            }

            productDao.save(product);
            recalculateCart(cart);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void delete(Cart cart, Long productId) {
        lock.writeLock().lock();
        try {
            Product product = productDao.getProduct(productId);
            CartItem cartItem = cart.getItems().stream()
                            .filter(item -> item.getProduct().getId().equals(productId))
                                    .findFirst()
                                            .orElseThrow(() -> new ProductNotFoundException(productId));
            product.setStock(product.getStock() + cartItem.getQuantity());
            cart.getItems().removeIf(item ->
                    productId.equals(item.getProduct().getId()));
            recalculateCart(cart);

        } finally {
            lock.writeLock().unlock();
        }
    }

    private CartItem findCartItem(Cart cart, Product product, int quantity) {
        return cart.getItems().stream()
                .filter(item -> product.getId().equals(item.getProduct().getId()))
                .findFirst()
                .orElse(null);
    }

    private void recalculateCart(Cart cart) {
        cart.setTotalQuantity(cart.getItems().stream()
                .map(CartItem::getQuantity)
                .mapToInt(q -> q)
                .sum());

        cart.setTotalCost(cart.getItems().stream()
                .map(cartItem -> {
                    BigDecimal price = cartItem.getProduct().getPrice();
                    int quantity = cartItem.getQuantity();
                    return price.multiply(BigDecimal.valueOf(quantity));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add));
    }
}
