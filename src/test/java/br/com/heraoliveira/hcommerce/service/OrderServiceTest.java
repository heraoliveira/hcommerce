package br.com.heraoliveira.hcommerce.service;

import br.com.heraoliveira.hcommerce.exception.InvalidCartException;
import br.com.heraoliveira.hcommerce.model.Address;
import br.com.heraoliveira.hcommerce.model.Cart;
import br.com.heraoliveira.hcommerce.model.Customer;
import br.com.heraoliveira.hcommerce.model.OrderStatus;
import br.com.heraoliveira.hcommerce.model.Product;
import br.com.heraoliveira.hcommerce.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderServiceTest {

    @TempDir
    Path tempDirectory;

    @Test
    void shouldReturnFailureReasonWhenCustomerIsMissing() {
        OrderService orderService = new OrderService(new OrderRepository(tempDirectory));
        Cart cart = cartWithProduct();

        OrderService.FinalizeOrderResult result = orderService.finalizeOrder(null, cart);

        assertFalse(result.completed());
        assertNull(result.order());
        assertEquals(OrderService.FinalizeOrderFailure.MISSING_CUSTOMER, result.failureReason());
    }

    @Test
    void shouldReturnFailureReasonWhenCartIsEmpty() {
        OrderService orderService = new OrderService(new OrderRepository(tempDirectory));

        OrderService.FinalizeOrderResult result = orderService.finalizeOrder(customer(), new Cart());

        assertFalse(result.completed());
        assertNull(result.order());
        assertEquals(OrderService.FinalizeOrderFailure.EMPTY_CART, result.failureReason());
    }

    @Test
    void shouldThrowWhenCartIsNull() {
        OrderService orderService = new OrderService(new OrderRepository(tempDirectory));

        assertThrows(InvalidCartException.class, () -> orderService.finalizeOrder(customer(), null));
    }

    @Test
    void shouldFinalizeAndPersistOrder() {
        OrderRepository orderRepository = new OrderRepository(tempDirectory);
        OrderService orderService = new OrderService(orderRepository);

        OrderService.FinalizeOrderResult result = orderService.finalizeOrder(customer(), cartWithProduct());

        assertTrue(result.completed());
        assertEquals(OrderStatus.COMPLETED, result.order().getOrderStatus());
        assertEquals(1, orderRepository.findAll().size());
    }

    private static Customer customer() {
        return new Customer(
                "Ana",
                "ana@example.com",
                new Address("01001-000", "Praca da Se", "Se", "Sao Paulo", "SP")
        );
    }

    private static Cart cartWithProduct() {
        Cart cart = new Cart();
        cart.addItem(new Product(1L, "Notebook", "Portable computer", new BigDecimal("2500.00")), 1);
        return cart;
    }
}
