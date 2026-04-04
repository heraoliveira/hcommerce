package br.com.heraoliveira.hcommerce.repository;

import br.com.heraoliveira.hcommerce.models.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderRepository {
    private final List<Order> orders = new ArrayList<>();

    public long nextId() {
        return orders.stream()
                .mapToLong(Order::getId)
                .max()
                .orElse(0L) + 1;
    }

    public void save(Order order) {
        orders.add(order);
    }

    public List<Order> findAll() {
        return List.copyOf(orders);
    }
}