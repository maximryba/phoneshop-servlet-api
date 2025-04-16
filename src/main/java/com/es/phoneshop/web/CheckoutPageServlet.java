package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;
import com.es.phoneshop.model.order.DefaultOrderService;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.order.OrderService;
import com.es.phoneshop.model.order.PaymentMethod;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CheckoutPageServlet extends HttpServlet {
    private static final String WEB_INF_CHECKOUT_JSP = "/WEB-INF/pages/checkout.jsp";
    public static final String ORDER = "order";
    private CartService cartService;
    private OrderService orderService;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        cartService = DefaultCartService.getInstance();
        orderService = DefaultOrderService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Cart cart = cartService.getCart(request);
        request.setAttribute(ORDER, orderService.getOrder(cart));
        request.setAttribute("paymentMethods", orderService.getPaymentMethods());
        request.getRequestDispatcher(WEB_INF_CHECKOUT_JSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Map<String, String> errors = new HashMap<>();
        Cart cart = cartService.getCart(request);
        Order order = orderService.getOrder(cart);
        setRequiredParameter(request, "firstName", errors, order::setFirstName, null);
        setRequiredParameter(request, "lastName", errors, order::setLastName, null);
        setRequiredParameter(request, "phone", errors, order::setPhone, phonePredicate);
        setDeliveryDate(request, errors, order::setDeliveryDate);
        setRequiredParameter(request, "deliveryAddress", errors, order::setDeliveryAddress, null);
        setPaymentMethod(request, errors, order);
        handleErrors(request, response, errors, order);
    }

    private void setPaymentMethod(HttpServletRequest request,  Map<String, String> errors, Order order) {
        String value = request.getParameter("paymentMethod");
        if (value == null || value.isEmpty()) {
            errors.put("paymentMethod", "Value is required");
        } else {
            order.setPaymentMethod(PaymentMethod.valueOf(value));
        }
    }

    private void handleErrors(HttpServletRequest request, HttpServletResponse response, Map<String, String> errorsOccurred,
                              Order order) throws IOException, ServletException {
        if (errorsOccurred.isEmpty()) {
            orderService.placeOrder(order);
            cartService.deleteCart(request.getSession());
            response.sendRedirect(request.getContextPath() + "/order/overview/" + order.getSecureId());
        } else {
            request.setAttribute("errors", errorsOccurred);
            request.setAttribute("order", order);
            request.setAttribute("paymentMethods", orderService.getPaymentMethods());
            request.getRequestDispatcher(WEB_INF_CHECKOUT_JSP).forward(request, response);
        }
    }

    Predicate<String> phonePredicate = phone -> phone.matches("\\d{12}");

    private void setRequiredParameter(HttpServletRequest request, String parameter, Map<String, String> errors,
                                      Consumer<String> consumer, Predicate<String> predicate) {
        String value = request.getParameter(parameter);
        if (value == null || value.isEmpty()) {
            errors.put(parameter, "Value is required");
        } else if (predicate != null && !predicate.test(value)) {
            errors.put(parameter, "Phone number should consists of 12 digits");
        } else {
            consumer.accept(value);
        }
    }

    private void setDeliveryDate(HttpServletRequest request, Map<String, String> errors,
                                 Consumer<LocalDate> consumer) {
        String parameter = "deliveryDate";
        String dateString = request.getParameter(parameter);
        if (dateString == null || dateString.isEmpty()) {
            errors.put(parameter, "Value is required");
        } else {
            try {
                LocalDate value = LocalDate.parse(dateString);
                if (value.isBefore(LocalDate.now())) {
                    errors.put(parameter, "Date cannot be in the past");
                } else {
                    consumer.accept(value);
                }
            } catch (DateTimeParseException e) {
                errors.put(parameter, "Invalid date format");
            }
        }
    }

}
