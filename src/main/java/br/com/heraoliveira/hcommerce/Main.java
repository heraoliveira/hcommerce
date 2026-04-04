package br.com.heraoliveira.hcommerce;

import br.com.heraoliveira.hcommerce.models.*;
import br.com.heraoliveira.hcommerce.repository.OrderRepository;
import br.com.heraoliveira.hcommerce.repository.ProductRepository;
import br.com.heraoliveira.hcommerce.service.CartService;
import br.com.heraoliveira.hcommerce.service.ViaCepService;

import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {

        try {
            // Instantiating

            ProductRepository productRepository = new ProductRepository();
            OrderRepository orderRepository = new OrderRepository();

            Address address1 = ViaCepService.fetchAddress("49010040");

            Customer customer1 = new Customer("Customer 1", "customer@customer.com", address1);

            Product product1 = new Product(productRepository.nextId(), "Product 1"
                    , "Description 1", BigDecimal.valueOf(10));
            productRepository.save(product1);

            Product product2 = new Product(productRepository.nextId(), "Product 2"
                    , "Description 2", BigDecimal.valueOf(20));
            productRepository.save(product2);

            Product product3 = new Product(productRepository.nextId(), "Product 3"
                    , "Description 3", BigDecimal.valueOf(30));
            productRepository.save(product3);

            Cart cart = new Cart();

            // Cart

            cart.addItem(product1, 100);
            cart.addItem(product2, 200);
            cart.addItem(product3, 300);

            System.out.println("Items:");
            cart.getItems().forEach(System.out::println);

            cart.removeItem(product2.getId());

            System.out.println("\nItems after removal:");
            cart.getItems().forEach(System.out::println);

            cart.updateQuantity(product3.getId(), 200);
            cart.updateQuantity(product1.getId(), 150);

            System.out.println("\nItems after quantity change:");
            cart.getItems().forEach(System.out::println);

            System.out.println("\nTotal after calculation: " + cart.calculateSubtotal());

            // Order

            CartService cartService = new CartService();
            cartService.recalculateDiscount(cart);

            Order order = Order.fromCart(orderRepository.nextId(), customer1, cart);
            orderRepository.save(order);

            product1.setPrice(BigDecimal.valueOf(500));
            cart.updateQuantity(product1.getId(), 175);

            System.out.println("\nOrder");
            System.out.println(order.getSummary());

            order.finalizeOrder();
            order.cancelOrder();
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }
}