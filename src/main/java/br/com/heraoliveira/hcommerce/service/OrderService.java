package br.com.heraoliveira.hcommerce.service;

import br.com.heraoliveira.hcommerce.exception.InvalidCartException;
import br.com.heraoliveira.hcommerce.model.Cart;
import br.com.heraoliveira.hcommerce.model.Customer;
import br.com.heraoliveira.hcommerce.model.Order;
import br.com.heraoliveira.hcommerce.repository.OrderRepository;

import java.util.List;
import java.util.Objects;

public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = Objects.requireNonNull(orderRepository, "Order repository cannot be null.");
    }

    public FinalizeOrderResult finalizeOrder(Customer customer, Cart cart) {
        if (customer == null) {
            return FinalizeOrderResult.failure(FinalizeOrderFailure.MISSING_CUSTOMER);
        }

        if (cart == null) {
            throw new InvalidCartException("Cart cannot be null.");
        }

        if (cart.isEmpty()) {
            return FinalizeOrderResult.failure(FinalizeOrderFailure.EMPTY_CART);
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

    public enum FinalizeOrderFailure {
        MISSING_CUSTOMER,
        EMPTY_CART
    }

    public record FinalizeOrderResult(boolean completed, Order order, FinalizeOrderFailure failureReason) {
        public static FinalizeOrderResult success(Order order) {
            return new FinalizeOrderResult(true, order, null);
        }

        public static FinalizeOrderResult failure(FinalizeOrderFailure failureReason) {
            return new FinalizeOrderResult(false, null, failureReason);
        }
    }
}
