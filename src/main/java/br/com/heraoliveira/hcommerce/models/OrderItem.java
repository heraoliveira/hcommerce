package br.com.heraoliveira.hcommerce.models;

import br.com.heraoliveira.hcommerce.exception.InvalidDataException;

import java.math.BigDecimal;

public class OrderItem {
    private final long productId;
    private final String productName;
    private final BigDecimal productPrice;
    private final int quantity;

    public OrderItem(long productId, String productName, BigDecimal productPrice, int quantity) {
        validateProductName(productName);
        validateProductPrice(productPrice);
        validateQuantity(quantity);

        this.productId = productId;
        this.productName = productName.strip();
        this.productPrice = productPrice;
        this.quantity = quantity;
    }

    private static void validateProductName(String productName) {
        if (productName == null || productName.isBlank())
            throw new InvalidDataException("Validation Error: Name is required and cannot be null or blank.");
    }

    private static void validateProductPrice(BigDecimal productPrice) {
        if (productPrice == null || productPrice.compareTo(BigDecimal.ZERO) <= 0)
            throw new InvalidDataException("Validation Error: Price must be strictly greater than zero.");
    }

    private static void validateQuantity(int quantity) {
        if (quantity <= 0) throw new InvalidDataException("Business Error: Quantity must be strictly " +
                "greater than zero.");
    }

    public BigDecimal calculateSubtotal() {
        return productPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public int getQuantity() {
        return quantity;
    }
}
