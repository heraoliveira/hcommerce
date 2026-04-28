package br.com.heraoliveira.hcommerce.console;

import br.com.heraoliveira.hcommerce.model.Customer;
import br.com.heraoliveira.hcommerce.service.CustomerService;

import java.util.Scanner;
import java.util.Objects;

final class CustomerConsoleHandler {
    private final CustomerService customerService;

    CustomerConsoleHandler(CustomerService customerService) {
        this.customerService = Objects.requireNonNull(
                customerService,
                "Customer service cannot be null."
        );
    }

    Customer registerCustomer(Scanner scanner) {
        System.out.println("\n=== Register Customer ===");

        String name = ConsoleInput.readRequiredText(scanner, "Name: ");
        String email = ConsoleInput.readValidEmail(scanner, "Email: ");
        String zip = ConsoleInput.readZipCode(scanner, "ZIP code: ");

        System.out.println("\nLooking up the address from the ZIP code...");

        Customer customer = customerService.registerCurrentCustomer(name, email, zip);

        System.out.println("Customer registered successfully.");
        return customer;
    }
}
