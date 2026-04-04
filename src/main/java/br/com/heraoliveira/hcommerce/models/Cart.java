package br.com.heraoliveira.hcommerce.models;

import br.com.heraoliveira.hcommerce.exception.InvalidDataException;
import br.com.heraoliveira.hcommerce.exception.ProductNotFoundException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cart {
    private final List<CartItem> items = new ArrayList<>();
    private BigDecimal discountPercentage =  BigDecimal.ZERO;

    private static void validateProduct(Product product) {
        if (product == null)
            throw new InvalidDataException("Validation Error: A valid product is required for cart operations.");
    }

    private CartItem findItemByProductId(long productId) {
        return items.stream()
                .filter(item -> item.getProduct().getId() == productId)
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException(
                        "Business Error: Product with ID " + productId + " not found in the cart."
                ));
    }

    public void addItem(Product product, int quantity) {
        validateProduct(product);
        if (quantity <= 0)
            throw new InvalidDataException("Business Error: Quantity to add must be strictly greater than zero.");

        var optionalCartItem = items.stream().filter(item -> item.getProduct().equals(product)).findFirst();

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
            throw new InvalidDataException("Business Error: Quantity must be strictly greater than zero.");
        }

        CartItem item = findItemByProductId(productId);
        item.updateQuantity(newQuantity);
    }

    public BigDecimal calculateSubtotal() {
        return items.stream().map(CartItem::calculateSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add) ;
    }

    public BigDecimal calculateTotal() {
        return calculateSubtotal().subtract(calculateDiscountAmount());
    }

    public void applyDiscount(BigDecimal discountPercentage) {
        if (discountPercentage == null)
            throw new InvalidDataException("Validation Error: Discount value cannot be null.");

        if (discountPercentage.compareTo(BigDecimal.ZERO) <= 0 ||
                discountPercentage.compareTo(BigDecimal.valueOf(100)) >= 0)
            throw new InvalidDataException("Business Error: Discount must be between 1 and 99.");

        this.discountPercentage = discountPercentage;
    }

    public BigDecimal calculateDiscountAmount() {
        return calculateSubtotal().multiply(discountPercentage)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    public void clearDiscount() {
        this.discountPercentage = BigDecimal.ZERO;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }
}