package br.com.heraoliveira.hcommerce.models;

import br.com.heraoliveira.hcommerce.exception.InvalidDataException;

import java.math.BigDecimal;

public class Product {
    private final long id;
    private final String name;
    private final String description;
    private BigDecimal price;

    public Product(long id , String name, String description, BigDecimal price) {
        validateId(id);
        validateName(name);
        validateDescription(description);
        validatePrice(price);

        this.id = id;
        this.name = name.strip();
        this.description = description.strip();
        this.price = price;
    }

    private static void validateId(long id) {
        if (id <= 0)
            throw new InvalidDataException("Validation Error: Product ID must be strictly greater than zero.");

    }

    private static void validateName(String name) {
        if (name == null || name.isBlank())
            throw new InvalidDataException("Validation Error: Name is required and cannot be null or blank.");
    }

    private static void validateDescription(String description) {
        if (description == null || description.isBlank())
            throw new InvalidDataException("Validation Error: Description is required and cannot be null or blank.");
    }

    private static void validatePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0)
            throw new InvalidDataException("Validation Error: Price must be strictly greater than zero.");
    }

    @Override
    public String toString() {
        return String.format("Product[id=%d, name=%s, description=%s, price=%s]",
                id, name, description, price);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product product)) return false;
        return id == product.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        validatePrice(price);
        this.price = price;
    }
}