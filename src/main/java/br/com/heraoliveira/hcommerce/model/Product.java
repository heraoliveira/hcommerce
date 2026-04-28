package br.com.heraoliveira.hcommerce.model;

import br.com.heraoliveira.hcommerce.exception.InvalidDataException;
import br.com.heraoliveira.hcommerce.util.MoneyFormatter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class Product {
    private final long id;
    private final String name;
    private final String description;
    private final BigDecimal price;

    @JsonCreator
    public Product(
            @JsonProperty("id") long id,
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("price") BigDecimal price) {
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
            throw new InvalidDataException("Product ID must be greater than zero.");
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank())
            throw new InvalidDataException("Name is required and cannot be null or blank.");
    }

    private static void validateDescription(String description) {
        if (description == null || description.isBlank())
            throw new InvalidDataException("Description is required and cannot be null or blank.");
    }

    private static void validatePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0)
            throw new InvalidDataException("Price must be greater than zero.");
    }

    @Override
    public String toString() {
        return String.format("Product[id=%d, name=%s, description=%s, price=%s]",
                id, name, description, MoneyFormatter.format(price));
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
}
