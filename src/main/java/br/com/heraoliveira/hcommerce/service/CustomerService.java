package br.com.heraoliveira.hcommerce.service;

import br.com.heraoliveira.hcommerce.model.Address;
import br.com.heraoliveira.hcommerce.model.Customer;
import br.com.heraoliveira.hcommerce.repository.CurrentCustomerRepository;

import java.util.Objects;
import java.util.Optional;

public class CustomerService {
    private final CurrentCustomerRepository currentCustomerRepository;
    private final AddressLookupService addressLookupService;

    public CustomerService(
            CurrentCustomerRepository currentCustomerRepository,
            AddressLookupService addressLookupService
    ) {
        this.currentCustomerRepository = Objects.requireNonNull(
                currentCustomerRepository,
                "Current customer repository cannot be null."
        );
        this.addressLookupService = Objects.requireNonNull(
                addressLookupService,
                "Address lookup service cannot be null."
        );
    }

    public Optional<Customer> findCurrentCustomer() {
        return currentCustomerRepository.findCurrent();
    }

    public Customer registerCurrentCustomer(String name, String email, String zip) {
        Address address = addressLookupService.fetchAddress(zip);
        Customer customer = new Customer(name, email, address);
        currentCustomerRepository.saveCurrent(customer);
        return customer;
    }
}
