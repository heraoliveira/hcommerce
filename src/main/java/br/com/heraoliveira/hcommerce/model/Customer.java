package br.com.heraoliveira.hcommerce.model;

import br.com.heraoliveira.hcommerce.exception.InvalidDataException;
import br.com.heraoliveira.hcommerce.util.EmailValidation;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Customer {
    private final String name;
    private final String email;
    private final Address address;

    @JsonCreator
    public Customer(
            @JsonProperty("name") String name,
            @JsonProperty("email") String email,
            @JsonProperty("address") Address address) {
        validateName(name);
        validateEmail(email);
        validateAddress(address);

        this.name = name.strip();
        this.email = email.strip().toLowerCase();
        this.address = address;
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank())
            throw new InvalidDataException("Name is required and cannot be null or blank.");
    }

    private static void validateEmail(String email) {
        if (!EmailValidation.isValid(email))
            throw new InvalidDataException("Email format is invalid.");
    }

    private static void validateAddress(Address address) {
        if (address == null) throw new InvalidDataException("A valid customer address is required.");
    }

    @Override
    public String toString() {
        return String.format("Customer[name=%s, email=%s, address=%s]", name, email, address);
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Address getAddress() {
        return address;
    }
}