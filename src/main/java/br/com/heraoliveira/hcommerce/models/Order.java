package br.com.heraoliveira.hcommerce.models;

import br.com.heraoliveira.hcommerce.exception.InvalidCartException;
import br.com.heraoliveira.hcommerce.exception.InvalidDataException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class Order {
    private static long nextId = 1;

    private final long id;
    private final LocalDateTime orderDate;
    private final Customer customer;
    private final List<CartItem> items;
    private final BigDecimal total;
    private OrderStatus orderStatus;

    public Order(Customer customer, List<CartItem> items) {
        validateCustomer(customer);
        validateItems(items);

        this.id = nextId++;
        this.customer = customer;
        this.items = items.stream().map(i -> new CartItem(i.getProduct(), i.getQuantity())).toList();
        this.orderDate = LocalDateTime.now();
        this.total = calculateTotal();
        this.orderStatus = OrderStatus.CREATED;
    }

    private static void validateCustomer (Customer customer) {
        if (customer == null) throw new InvalidDataException("Customer cannot be null.");
    }

    private static void validateItems(List<CartItem> items) {
        if (items == null || items.isEmpty()) throw new InvalidCartException("Order cannot be empty or null.");
        if (items.stream().anyMatch(i -> i == null || i.getProduct() == null))
            throw new InvalidCartException("Order item cannot be null.");
    }

    private BigDecimal calculateTotal() {
        validateItems(items);
        return items.stream().map(i-> i.calculateSubtotal())
                .reduce(BigDecimal.ZERO, (a, b) -> a.add(b));
    }

    public void finalizeOrder() {
        this.orderStatus = this.orderStatus.finalizeOrder();
    }

    public void cancelOrder() {
        this.orderStatus = this.orderStatus.cancelOrder();
    }

    public String getSummary() {
        var products = items.stream().map(i -> String.format("%d qty. of %s"
                , i.getQuantity(), i.getProduct().getName())).toList();
        return String.format("""
                Order ID: %s
                Customer: %s
                Items: %s
                Status: %s
                Date: %s
                Total: %s
                """, id, customer.getName(), products, orderStatus, orderDate, total);
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public BigDecimal getTotal() {
        return total;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }
}
