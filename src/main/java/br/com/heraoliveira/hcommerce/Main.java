package br.com.heraoliveira.hcommerce;

import br.com.heraoliveira.hcommerce.models.Cart;
import br.com.heraoliveira.hcommerce.models.CartItem;
import br.com.heraoliveira.hcommerce.models.Product;

import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {

        Product product = new Product("Product 1", "Description 1", BigDecimal.valueOf(1));
        Product product2 = new Product("Product 2", "Description 2", BigDecimal.valueOf(2));
        Product product3 = new Product("Product 3", "Description 3", BigDecimal.valueOf(3));

        CartItem cartItem = new CartItem(product, 5);
        CartItem cartItem2 = new CartItem(product2, 10);
        CartItem cartItem3 = new CartItem(product3, 15);

        Cart cart = new Cart();
        cart.addItem(cartItem);
        cart.addItem(cartItem2);
        cart.addItem(cartItem3);
        System.out.println(cart.getItems());

        // TESTING ADDING DUPLICITY

        cart.addItem(cartItem);
        System.out.println(cart.getItems());

        // TESTING REMOVING

        if (cart.removeItem(cartItem)) System.out.println(cart.getItems());

        // TESTING CHANGES QUANTITY

        cart.updateQuantity(cartItem3, 7);
        System.out.println(cart.getItems());

        System.out.println(cart.calculateTotal());

        System.out.println(cart.applyDiscount(BigDecimal.TEN));



    }
}