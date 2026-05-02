package br.com.heraoliveira.hcommerce.service;

import br.com.heraoliveira.hcommerce.model.Address;
import br.com.heraoliveira.hcommerce.model.Customer;
import br.com.heraoliveira.hcommerce.repository.CurrentCustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomerServiceTest {

    @TempDir
    Path tempDirectory;

    @Test
    void shouldRegisterAndPersistTheCurrentCustomer() {
        CurrentCustomerRepository currentCustomerRepository = new CurrentCustomerRepository(tempDirectory);
        AtomicReference<String> requestedZip = new AtomicReference<>();
        AddressLookupService addressLookupService = zip -> {
            requestedZip.set(zip);
            return new Address("01001-000", "Praca da Se", "Se", "Sao Paulo", "SP");
        };
        CustomerService customerService = new CustomerService(currentCustomerRepository, addressLookupService);

        Customer customer = customerService.registerCurrentCustomer("Ana", "ANA@EXAMPLE.COM", "01001-000");

        assertEquals("01001-000", requestedZip.get());
        assertEquals("Ana", customer.getName());
        assertEquals("ana@example.com", customer.getEmail());
        assertTrue(currentCustomerRepository.findCurrent().isPresent());
        assertEquals("Ana", currentCustomerRepository.findCurrent().orElseThrow().getName());
    }

    @Test
    void shouldFindThePersistedCurrentCustomer() {
        CurrentCustomerRepository currentCustomerRepository = new CurrentCustomerRepository(tempDirectory);
        Customer customer = new Customer(
                "Ana",
                "ana@example.com",
                new Address("01001-000", "Praca da Se", "Se", "Sao Paulo", "SP")
        );
        currentCustomerRepository.saveCurrent(customer);

        CustomerService customerService = new CustomerService(
                currentCustomerRepository,
                zip -> new Address(zip, "Praca da Se", "Se", "Sao Paulo", "SP")
        );

        assertTrue(customerService.findCurrentCustomer().isPresent());
        assertEquals("Ana", customerService.findCurrentCustomer().orElseThrow().getName());
    }
}
