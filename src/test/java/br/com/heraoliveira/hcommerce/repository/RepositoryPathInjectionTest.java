package br.com.heraoliveira.hcommerce.repository;

import br.com.heraoliveira.hcommerce.exception.ProductNotFoundException;
import br.com.heraoliveira.hcommerce.model.Address;
import br.com.heraoliveira.hcommerce.model.Cart;
import br.com.heraoliveira.hcommerce.model.Customer;
import br.com.heraoliveira.hcommerce.model.Order;
import br.com.heraoliveira.hcommerce.model.OrderStatus;
import br.com.heraoliveira.hcommerce.model.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RepositoryPathInjectionTest {

    @TempDir
    Path tempDirectory;

    @Test
    void shouldPersistProductsUsingTheInjectedDataDirectory() {
        ProductRepository productRepository = new ProductRepository(tempDirectory);
        Product product = new Product(1L, "Notebook", "Portable computer", new BigDecimal("2500.00"));

        productRepository.save(product);

        ProductRepository reloadedRepository = new ProductRepository(tempDirectory);

        assertTrue(Files.exists(tempDirectory.resolve("products.json")));
        assertEquals(1, reloadedRepository.findAll().size());
        assertEquals("Notebook", reloadedRepository.findById(1L).getName());
    }

    @Test
    void shouldPersistOrdersUsingTheInjectedDataDirectory() {
        OrderRepository orderRepository = new OrderRepository(tempDirectory);
        Order order = Order.fromCart(1L, createCustomer(), createCart());

        orderRepository.save(order);

        OrderRepository reloadedRepository = new OrderRepository(tempDirectory);

        assertTrue(Files.exists(tempDirectory.resolve("orders.json")));
        assertEquals(1, reloadedRepository.findAll().size());
        assertEquals(OrderStatus.COMPLETED, reloadedRepository.findAll().get(0).getOrderStatus());
        assertEquals(2, reloadedRepository.findAll().get(0).getItems().size());
    }

    @Test
    void shouldPersistAndLoadTheCurrentCustomerUsingTheInjectedDataDirectory() {
        CurrentCustomerRepository currentCustomerRepository = new CurrentCustomerRepository(tempDirectory);
        Customer customer = createCustomer();

        currentCustomerRepository.saveCurrent(customer);

        CurrentCustomerRepository reloadedRepository = new CurrentCustomerRepository(tempDirectory);

        assertTrue(Files.exists(tempDirectory.resolve("customer.json")));
        assertTrue(reloadedRepository.findCurrent().isPresent());
        assertEquals("Ana", reloadedRepository.findCurrent().orElseThrow().getName());
        assertEquals("ana@example.com", reloadedRepository.findCurrent().orElseThrow().getEmail());
    }

    @Test
    void shouldReturnEmptyWhenCustomerFileIsAbsent() {
        CurrentCustomerRepository currentCustomerRepository = new CurrentCustomerRepository(tempDirectory);

        assertTrue(currentCustomerRepository.findCurrent().isEmpty());
        assertFalse(Files.exists(tempDirectory.resolve("customer.json")));
    }

    @Test
    void shouldReturnEmptyWhenCustomerFileIsEmpty() throws Exception {
        Files.createDirectories(tempDirectory);
        Files.writeString(tempDirectory.resolve("customer.json"), "");

        CurrentCustomerRepository currentCustomerRepository = new CurrentCustomerRepository(tempDirectory);

        assertTrue(currentCustomerRepository.findCurrent().isEmpty());
    }

    @Test
    void shouldTreatAnEmptyProductsJsonFileAsAnEmptyRepository() throws Exception {
        Files.writeString(tempDirectory.resolve("products.json"), "");

        ProductRepository productRepository = new ProductRepository(tempDirectory);

        assertTrue(productRepository.findAll().isEmpty());
        assertEquals("[]", Files.readString(tempDirectory.resolve("products.json")).strip());
    }

    @Test
    void shouldThrowWhenOrdersJsonFileContainsInvalidJson() throws Exception {
        Files.writeString(tempDirectory.resolve("orders.json"), "{invalid-json}");

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> new OrderRepository(tempDirectory)
        );

        assertEquals("Failed to load orders from the JSON file.", exception.getMessage());
    }

    @Test
    void shouldThrowWhenProductsJsonFileContainsInvalidJson() throws Exception {
        Files.writeString(tempDirectory.resolve("products.json"), "{invalid-json}");

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> new ProductRepository(tempDirectory)
        );

        assertEquals("Failed to load products from the JSON file.", exception.getMessage());
    }

    @Test
    void shouldRejectRemovingANonExistentProduct() {
        ProductRepository productRepository = new ProductRepository(tempDirectory);

        assertThrows(ProductNotFoundException.class, () -> productRepository.removeById(999L));
    }

    private static Customer createCustomer() {
        return new Customer(
                "Ana",
                "ana@example.com",
                new Address("01001-000", "Praca da Se", "Se", "Sao Paulo", "SP")
        );
    }

    private static Cart createCart() {
        Cart cart = new Cart();
        cart.addItem(new Product(1L, "Notebook", "Portable computer", new BigDecimal("1200.00")), 1);
        cart.addItem(new Product(2L, "Mouse", "Wireless mouse", new BigDecimal("400.00")), 1);
        return cart;
    }
}
