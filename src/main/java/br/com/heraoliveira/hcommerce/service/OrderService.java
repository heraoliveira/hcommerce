package br.com.heraoliveira.hcommerce.service;

import br.com.heraoliveira.hcommerce.exception.InvalidCartException;
import br.com.heraoliveira.hcommerce.model.Cart;
import br.com.heraoliveira.hcommerce.model.Customer;
import br.com.heraoliveira.hcommerce.model.Order;
import br.com.heraoliveira.hcommerce.repository.OrderRepository;

import java.util.List;
import java.util.Objects;

public class OrderService {
    private static final String MISSING_CUSTOMER_MESSAGE = "Register a customer before finalizing the order.";
    private static final String EMPTY_CART_MESSAGE = "Cart is empty. Add products before finalizing the order.";

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = Objects.requireNonNull(orderRepository, "Order repository cannot be null.");
    }

    public FinalizeOrderResult finalizeOrder(Customer customer, Cart cart) {
        if (customer == null) {
            return FinalizeOrderResult.failure(MISSING_CUSTOMER_MESSAGE);
        }

        if (cart.isEmpty()) {
            return FinalizeOrderResult.failure(EMPTY_CART_MESSAGE);
        }

        Order order = Order.fromCart(
                orderRepository.nextId(),
                customer,
                cart
        );

        orderRepository.save(order);
        return FinalizeOrderResult.success(order);
    }

    public List<Order> findSavedOrders() {
        return orderRepository.findAll();
    }

    public record FinalizeOrderResult(boolean completed, Order order, String message) {
        public static FinalizeOrderResult success(Order order) {
            return new FinalizeOrderResult(true, order, null);
        }

        public static FinalizeOrderResult failure(String message) {
            return new FinalizeOrderResult(false, null, message);
        }
    }
}
