package br.com.heraoliveira.hcommerce.models;

import br.com.heraoliveira.hcommerce.exception.InvalidDataException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class OrderItem {
    private final long productId;
    private final String productName;
    private final BigDecimal productPrice;
    private final int quantity;

    @JsonCreator
    public OrderItem(
            @JsonProperty("productId") long productId,
            @JsonProperty("productName") String productName,
            @JsonProperty("productPrice") BigDecimal productPrice,
            @JsonProperty("quantity") int quantity) {
        validateProductId(productId);
        validateProductName(productName);
        validateProductPrice(productPrice);
        validateQuantity(quantity);

        this.productId = productId;
        this.productName = productName.strip();
        this.productPrice = productPrice;
        this.quantity = quantity;
    }

    private static void validateProductId(long productId) {
        if (productId <= 0)
            throw new InvalidDataException("Product ID must be greater than zero.");
    }

    private static void validateProductName(String productName) {
        if (productName == null || productName.isBlank())
            throw new InvalidDataException("Name is required and cannot be null or blank.");
    }

    private static void validateProductPrice(BigDecimal productPrice) {
        if (productPrice == null || productPrice.compareTo(BigDecimal.ZERO) <= 0)
            throw new InvalidDataException("Price must be greater than zero.");
    }

    private static void validateQuantity(int quantity) {
        if (quantity <= 0) throw new InvalidDataException("Quantity must be " +
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