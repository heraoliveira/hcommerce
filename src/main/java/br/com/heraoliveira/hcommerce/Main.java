package br.com.heraoliveira.hcommerce;

import br.com.heraoliveira.hcommerce.models.Cart;
import br.com.heraoliveira.hcommerce.models.Customer;
import br.com.heraoliveira.hcommerce.models.Order;
import br.com.heraoliveira.hcommerce.models.Product;

import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {

        //INSTANTIATING CUSTOMER
        Customer customer = new Customer("Customer 1", "customer@cust.com", null);

        // INSTANTIATING PRODUCT
        Product product = new Product("Product 1", "Description 1", BigDecimal.valueOf(1));
        Product product2 = new Product("Product 2", "Description 2", BigDecimal.valueOf(2));
        Product product3 = new Product("Product 3", "Description 3", BigDecimal.valueOf(3));

        // INSTANTIATING CART
        Cart cart = new Cart();
        cart.addItem(product, 5);
        cart.addItem(product2, 10);
        cart.addItem(product3, 15);
        System.out.println(cart.getItems());

        // TESTING ADDING DUPLICITY

        cart.addItem(product, 5);
        System.out.println(cart.getItems());

        // TESTING REMOVING

        if (cart.removeItem(product)) System.out.println(cart.getItems());

        // TESTING CHANGES QUANTITY

        cart.updateQuantity(product3, 7);
        System.out.println(cart.getItems());

        System.out.println(cart.calculateTotal());

        System.out.println(cart.applyDiscount(BigDecimal.TEN));

        Order order = new Order(customer, cart.getItems());
        System.out.println(order.getSummary());
    }
}