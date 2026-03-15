package br.com.heraoliveira.hcommerce.models;

import br.com.heraoliveira.hcommerce.exception.InvalidDataException;
import br.com.heraoliveira.hcommerce.util.EmailValidation;

public class Customer {
    private final String name;
    private final String email;
    private final Address address;

    public Customer(String name, String email, Address address) {
        validateName(name);
        validateEmail(email);
        validateAddress(address);

        this.name = name.strip();
        this.email = email.strip().toLowerCase();
        this.address = address;
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) throw new InvalidDataException("Name cannot be null or blank.");
    }

    private static void validateEmail(String email) {
        if (!EmailValidation.validateEmail(email)) throw new InvalidDataException("Email is not valid.");
    }

    private static void validateAddress(Address address) {
        if (address == null) throw new InvalidDataException("Address cannot be null.");
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