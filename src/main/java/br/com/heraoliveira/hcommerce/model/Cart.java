package br.com.heraoliveira.hcommerce.model;

import br.com.heraoliveira.hcommerce.exception.InvalidDataException;
import br.com.heraoliveira.hcommerce.exception.ProductNotFoundException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cart {
    private final List<CartItem> items = new ArrayList<>();

    private static void validateProduct(Product product) {
        if (product == null)
            throw new InvalidDataException("A valid product is required for cart operations.");
    }

    private CartItem findItemByProductId(long productId) {
        return items.stream()
                .filter(item -> item.getProduct().getId() == productId)
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException(
                        "Product with ID " + productId + " was not found in the cart."
                ));
    }

    public void addItem(Product product, int quantity) {
        validateProduct(product);
        if (quantity <= 0)
            throw new InvalidDataException("Quantity to add must be greater than zero.");

        var optionalCartItem = items.stream()
                .filter(item -> item.getProduct().equals(product))
                .findFirst();

        if (optionalCartItem.isPresent()) {
            optionalCartItem.get().addQuantity(quantity);
        } else {
            items.add(new CartItem(product, quantity));
        }
    }

    public void removeItem(long productId) {
        CartItem item = findItemByProductId(productId);
        items.remove(item);
    }

    public void updateQuantity(long productId, int newQuantity) {
        if (newQuantity <= 0) {
            throw new InvalidDataException("Quantity must be greater than zero.");
        }

        CartItem item = findItemByProductId(productId);
        item.updateQuantity(newQuantity);
    }

    public BigDecimal calculateSubtotal() {
        return items.stream().map(CartItem::calculateSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add) ;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public boolean containsProduct(long productId) {
        return items.stream()
                .anyMatch(item -> item.getProduct().getId() == productId);
    }

    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }
}
