package br.com.heraoliveira.hcommerce.model;

import br.com.heraoliveira.hcommerce.exception.InvalidCartException;
import br.com.heraoliveira.hcommerce.exception.InvalidDataException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderTest {

    @Test
    void shouldCreateACompletedOrderFromTheCart() {
        Cart cart = new Cart();
        cart.addItem(new Product(1L, "Notebook", "Portable computer", new BigDecimal("250.00")), 1);
        Customer customer = new Customer("Maria", "maria@email.com", validAddress());

        Order order = Order.fromCart(1L, customer, cart);
        CartPricing.PricingSummary pricingSummary = CartPricing.calculate(cart);

        assertEquals(1L, order.getId());
        assertEquals(customer, order.getCustomer());
        assertEquals(OrderStatus.COMPLETED, order.getOrderStatus());
        assertEquals(pricingSummary.subtotal(), order.getSubtotal());
        assertEquals(pricingSummary.discountAmount(), order.getDiscountAmount());
        assertEquals(pricingSummary.total(), order.getTotal());
        assertEquals(1, order.getItems().size());
        assertEquals("Notebook", order.getItems().get(0).getProductName());
    }

    @Test
    void shouldCreateACompletedOrderFromACartWithMultipleItems() {
        Cart cart = new Cart();
        cart.addItem(new Product(1L, "Keyboard", "Mechanical keyboard", new BigDecimal("120.00")), 1);
        cart.addItem(new Product(2L, "Mouse", "Wireless mouse", new BigDecimal("130.00")), 1);
        Customer customer = new Customer("Maria", "maria@email.com", validAddress());

        Order order = Order.fromCart(2L, customer, cart);
        CartPricing.PricingSummary pricingSummary = CartPricing.calculate(cart);

        assertEquals(OrderStatus.COMPLETED, order.getOrderStatus());
        assertEquals(2, order.getItems().size());
        assertEquals(pricingSummary.subtotal(), order.getSubtotal());
        assertEquals(pricingSummary.discountAmount(), order.getDiscountAmount());
        assertEquals(pricingSummary.total(), order.getTotal());
    }

    @Test
    void shouldRejectNullCustomerWhenCreatingOrderFromCart() {
        Cart cart = new Cart();
        cart.addItem(new Product(1L, "Notebook", "Portable computer", new BigDecimal("250.00")), 1);

        assertThrows(InvalidDataException.class, () -> Order.fromCart(1L, null, cart));
    }

    @Test
    void shouldRejectNullCartWhenCreatingOrderFromCart() {
        Customer customer = new Customer("Maria", "maria@email.com", validAddress());

        assertThrows(InvalidCartException.class, () -> Order.fromCart(1L, customer, null));
    }

    @Test
    void shouldRejectEmptyCartWhenCreatingOrderFromCart() {
        Customer customer = new Customer("Maria", "maria@email.com", validAddress());

        assertThrows(InvalidCartException.class, () -> Order.fromCart(1L, customer, new Cart()));
    }

    @Test
    void shouldRejectOrderWhenSubtotalDiffersFromTheSumOfItems() {
        assertThrows(InvalidDataException.class, () -> new Order(
                1L,
                LocalDateTime.now(),
                validCustomer(),
                validItems(),
                new BigDecimal("90.00"),
                BigDecimal.ZERO,
                new BigDecimal("90.00"),
                OrderStatus.COMPLETED
        ));
    }

    @Test
    void shouldRejectOrderWhenDiscountAmountExceedsSubtotal() {
        assertThrows(InvalidDataException.class, () -> new Order(
                1L,
                LocalDateTime.now(),
                validCustomer(),
                validItems(),
                new BigDecimal("100.00"),
                new BigDecimal("150.00"),
                new BigDecimal("1.00"),
                OrderStatus.COMPLETED
        ));
    }

    @Test
    void shouldRejectOrderWhenTotalDoesNotMatchSubtotalMinusDiscount() {
        assertThrows(InvalidDataException.class, () -> new Order(
                1L,
                LocalDateTime.now(),
                validCustomer(),
                validItems(),
                new BigDecimal("100.00"),
                new BigDecimal("10.00"),
                new BigDecimal("95.00"),
                OrderStatus.COMPLETED
        ));
    }

    private Address validAddress() {
        return new Address("01001-000", "Praca da Se", "Se", "Sao Paulo", "SP");
    }

    private Customer validCustomer() {
        return new Customer("Maria", "maria@email.com", validAddress());
    }

    private List<OrderItem> validItems() {
        return List.of(new OrderItem(1L, "Notebook", new BigDecimal("100.00"), 1));
    }
}
