package br.com.heraoliveira.hcommerce.models;

import br.com.heraoliveira.hcommerce.exception.InvalidCartException;
import br.com.heraoliveira.hcommerce.exception.InvalidDataException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Order {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final long id;
    private final LocalDateTime orderDate;
    private final Customer customer;
    private final List<OrderItem> items;
    private final BigDecimal subtotal;
    private final BigDecimal discountAmount;
    private final BigDecimal total;
    private OrderStatus orderStatus;

    public Order(long id, Customer customer, List<OrderItem> items, BigDecimal subtotal, BigDecimal discountAmount
            , BigDecimal total) {
        validateId(id);
        validateCustomer(customer);
        validateItems(items);
        validateSubtotal(subtotal);
        validateDiscountAmount(discountAmount);
        validateTotal(total);

        this.id = id;
        this.customer = customer;
        this.items = List.copyOf(items);
        this.orderDate = LocalDateTime.now();
        this.subtotal = subtotal;
        this.discountAmount = discountAmount;
        this.total = total;
        this.orderStatus = OrderStatus.CREATED;
    }

    private static void validateId(long id) {
        if (id <= 0)
            throw new InvalidDataException("Validation Error: Order ID must be strictly greater than zero.");
    }

    private static void validateCustomer (Customer customer) {
        if (customer == null)
            throw new InvalidDataException("Validation Error: A valid customer is required to create an order.");
    }

    private static void validateItems(List<OrderItem> items) {
        if (items == null || items.isEmpty())
            throw new InvalidDataException("Business Error: Cannot create an order with an empty item list.");
        if (items.stream().anyMatch(Objects::isNull))
            throw new InvalidDataException("Validation Error: All order items must be valid and cannot be null.");
    }

    private static void validateTotal(BigDecimal total) {
        if (total == null) {
            throw new InvalidDataException("Validation Error: Order total cannot be null.");
        }

        if (total.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidDataException("Business Error: Order total must be strictly " +
                    "greater than zero.");
        }
    }

    private static void validateSubtotal(BigDecimal subtotal) {
        if (subtotal == null)
            throw new InvalidDataException("Validation Error: Order subtotal cannot be null.");

        if (subtotal.compareTo(BigDecimal.ZERO) <= 0)
            throw new InvalidDataException("Business Error: Order subtotal must be strictly " +
                    "greater than zero.");
    }

    private static void validateDiscountAmount(BigDecimal discountAmount) {
        if (discountAmount == null)
            throw new InvalidDataException("Validation Error: Order discount amount cannot be null.");

        if (discountAmount.compareTo(BigDecimal.ZERO) < 0)
            throw new InvalidDataException("Business Error: Order discount amount cannot be negative.");
    }

    private static OrderItem toOrderItem(CartItem cartItem) {
        if  (cartItem == null)
            throw new InvalidDataException("Validation Error: A valid Cart Item is required for order operations.");
        Product product = cartItem.getProduct();
        return new OrderItem(product.getId(), product.getName(), product.getPrice(), cartItem.getQuantity());
    }

    public static Order fromCart(long orderId, Customer customer, Cart cart) {
        validateCustomer(customer);
        if (cart == null) throw new InvalidCartException("Validation Error: Cart cannot be null.");
        if (cart.isEmpty()) throw new InvalidCartException("Business Error: Cart cannot be empty.");

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cart.getItems()) {
            orderItems.add(toOrderItem(cartItem));
        }

        return new Order(orderId, customer, orderItems, cart.calculateSubtotal(), cart.calculateDiscountAmount()
                , cart.calculateTotal());
    }

    public void finalizeOrder() {
        this.orderStatus = this.orderStatus.finalizeOrder();
    }

    public void cancelOrder() {
        this.orderStatus = this.orderStatus.cancelOrder();
    }

    public String getSummary() {
        var products = items.stream()
                .map(i -> String.format("- %d qty. of %s", i.getQuantity(), i.getProductName()))
                .collect(Collectors.joining("\n"));

        return String.format("""
            Order ID: %s
            Customer: %s
            Status: %s
            Date: %s
            Subtotal: R$ %s
            Discount: R$ %s
            Total: R$ %s
            Order items:
            %s
            """,
                id, customer.getName(), orderStatus, orderDate.format(formatter), subtotal, discountAmount, total,
                products);
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

    public List<OrderItem> getItems() {
        return items;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }
}