package br.com.heraoliveira.hcommerce.models;

import br.com.heraoliveira.hcommerce.exception.InvalidCartException;
import br.com.heraoliveira.hcommerce.exception.InvalidDataException;

import java.math.BigDecimal;
import java.util.Objects;

public class CartItem {
    private final Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        validateProduct(product);
        validateQuantity(quantity);

        this.product = product;
        this.quantity = quantity;
    }

    private static void validateProduct(Product product) {
        if (product == null)
            throw new InvalidDataException("A valid product is required to create a cart item.");
    }

    private static void validateQuantity(int quantity) {
        if (quantity <= 0)
            throw new InvalidCartException("Quantity must be greater than zero.");
    }

    public BigDecimal calculateSubtotal() {
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

    public void addQuantity(int newQuantity) {
        validateQuantity(newQuantity);
        this.quantity += newQuantity;
    }

    public void updateQuantity(int newQuantity) {
        validateQuantity(newQuantity);
        this.quantity = newQuantity;
    }

    @Override
    public String toString() {
        return String.format("CartItem[product=%s, price=%s, quantity=%s, subtotal=%s]"
                , product.getName(), product.getPrice() , quantity, calculateSubtotal());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CartItem cartItem)) return false;
        return Objects.equals(product, cartItem.product);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(product);
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }
}