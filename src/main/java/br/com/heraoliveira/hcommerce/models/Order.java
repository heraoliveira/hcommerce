package br.com.heraoliveira.hcommerce.models;

import br.com.heraoliveira.hcommerce.exception.InvalidCartException;
import br.com.heraoliveira.hcommerce.exception.InvalidDataException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class Order {
    private static long nextId = 1;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

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
        if (customer == null) throw new InvalidDataException("Validation Error: A valid customer is required " +
                "to create an order.");
    }

    private static void validateItems(List<CartItem> items) {
        if (items == null || items.isEmpty()) throw new InvalidCartException("Business Error: Cannot create an " +
                "order with an empty item list.");
        if (items.stream().anyMatch(i -> i == null || i.getProduct() == null))
            throw new InvalidCartException("Validation Error: All order items must contain valid products.");
    }

    private BigDecimal calculateTotal() {
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
        var products = items.stream().map(i -> String.format("- %d qty. of %s"
                , i.getQuantity(), i.getProduct().getName())).collect(Collectors.joining("\n"));
        return String.format("""
                Order ID: %s
                Customer: %s
                Status: %s
                Date: %s
                Total: R$ %s
                Order items:
                %s
                """, id, customer.getName(), orderStatus, orderDate.format(formatter), total, products);
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
        return items;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }
}