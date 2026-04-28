package br.com.heraoliveira.hcommerce.model;

import br.com.heraoliveira.hcommerce.exception.InvalidCartException;
import br.com.heraoliveira.hcommerce.exception.InvalidDataException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Order {
    private final long id;
    private final LocalDateTime orderDate;
    private final Customer customer;
    private final List<OrderItem> items;
    private final BigDecimal subtotal;
    private final BigDecimal discountAmount;
    private final BigDecimal total;
    private final OrderStatus orderStatus;

    @JsonCreator
    public Order(
            @JsonProperty("id") long id,
            @JsonProperty("orderDate") LocalDateTime orderDate,
            @JsonProperty("customer") Customer customer,
            @JsonProperty("items") List<OrderItem> items,
            @JsonProperty("subtotal") BigDecimal subtotal,
            @JsonProperty("discountAmount") BigDecimal discountAmount,
            @JsonProperty("total") BigDecimal total,
            @JsonProperty("orderStatus") OrderStatus orderStatus) {
        validateId(id);
        validateOrderDate(orderDate);
        validateCustomer(customer);
        validateItems(items);
        validateSubtotal(subtotal);
        validateDiscountAmount(discountAmount);
        validateTotal(total);
        validateOrderStatus(orderStatus);
        validateFinancialConsistency(items, subtotal, discountAmount, total);

        this.id = id;
        this.orderDate = orderDate;
        this.customer = customer;
        this.items = List.copyOf(items);
        this.subtotal = subtotal;
        this.discountAmount = discountAmount;
        this.total = total;
        this.orderStatus = orderStatus;
    }

    private static void validateId(long id) {
        if (id <= 0)
            throw new InvalidDataException("Order ID must be greater than zero.");
    }

    private static void validateOrderDate(LocalDateTime orderDate) {
        if (orderDate == null) {
            throw new InvalidDataException("Order date cannot be null.");
        }
    }

    private static void validateCustomer (Customer customer) {
        if (customer == null)
            throw new InvalidDataException("A valid customer is required to create an order.");
    }

    private static void validateItems(List<OrderItem> items) {
        if (items == null || items.isEmpty())
            throw new InvalidDataException("An order cannot be created with an empty item list.");
        if (items.stream().anyMatch(Objects::isNull))
            throw new InvalidDataException("All order items must be valid and cannot be null.");
    }

    private static void validateSubtotal(BigDecimal subtotal) {
        if (subtotal == null)
            throw new InvalidDataException("Order subtotal cannot be null.");

        if (subtotal.compareTo(BigDecimal.ZERO) <= 0)
            throw new InvalidDataException("Order subtotal must be " +
                    "greater than zero.");
    }

    private static void validateDiscountAmount(BigDecimal discountAmount) {
        if (discountAmount == null)
            throw new InvalidDataException("Order discount amount cannot be null.");

        if (discountAmount.compareTo(BigDecimal.ZERO) < 0)
            throw new InvalidDataException("Order discount amount cannot be negative.");
    }

    private static void validateTotal(BigDecimal total) {
        if (total == null)
            throw new InvalidDataException("Order total cannot be null.");

        if (total.compareTo(BigDecimal.ZERO) <= 0)
            throw new InvalidDataException("Order total must be greater than zero.");
    }

    private static void validateOrderStatus(OrderStatus orderStatus) {
        if (orderStatus == null) {
            throw new InvalidDataException("Order status cannot be null.");
        }
    }

    private static void validateFinancialConsistency(
            List<OrderItem> items,
            BigDecimal subtotal,
            BigDecimal discountAmount,
            BigDecimal total
    ) {
        BigDecimal expectedSubtotal = items.stream()
                .map(OrderItem::calculateSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (expectedSubtotal.compareTo(subtotal) != 0) {
            throw new InvalidDataException("Order subtotal must match the sum of its items.");
        }

        if (discountAmount.compareTo(subtotal) > 0) {
            throw new InvalidDataException("Order discount amount cannot exceed the subtotal.");
        }

        BigDecimal expectedTotal = subtotal.subtract(discountAmount);
        if (expectedTotal.compareTo(total) != 0) {
            throw new InvalidDataException("Order total must equal subtotal minus the discount amount.");
        }
    }

    private static OrderItem toOrderItem(CartItem cartItem) {
        if  (cartItem == null)
            throw new InvalidDataException("A valid cart item is required for order operations.");
        Product product = cartItem.getProduct();
        return new OrderItem(
                product.getId(),
                product.getName(),
                product.getPrice(),
                cartItem.getQuantity());
    }

    public static Order fromCart(long orderId, Customer customer, Cart cart) {
        validateCustomer(customer);
        if (cart == null) throw new InvalidCartException("Cart cannot be null.");
        if (cart.isEmpty()) throw new InvalidCartException("Cart cannot be empty.");

        CartPricing.PricingSummary pricingSummary = CartPricing.calculate(cart);
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cart.getItems()) {
            orderItems.add(toOrderItem(cartItem));
        }

        return new Order(
                orderId,
                LocalDateTime.now(),
                customer,
                orderItems,
                pricingSummary.subtotal(),
                pricingSummary.discountAmount(),
                pricingSummary.total(),
                OrderStatus.COMPLETED);
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
