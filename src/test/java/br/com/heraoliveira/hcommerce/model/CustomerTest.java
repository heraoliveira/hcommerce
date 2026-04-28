package br.com.heraoliveira.hcommerce.model;

import br.com.heraoliveira.hcommerce.exception.InvalidDataException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CustomerTest {

    @Test
    void shouldNormalizeCustomerEmail() {
        Customer customer = new Customer("Maria", "Maria.Silva@Email.com", validAddress());

        assertEquals("maria.silva@email.com", customer.getEmail());
    }

    @Test
    void shouldRejectInvalidEmail() {
        assertThrows(
                InvalidDataException.class,
                () -> new Customer("Maria", "invalid-email", validAddress())
        );
    }

    @Test
    void shouldRejectNullAddress() {
        assertThrows(
                InvalidDataException.class,
                () -> new Customer("Maria", "maria@email.com", null)
        );
    }

    private Address validAddress() {
        return new Address("01001-000", "Praca da Se", "Se", "Sao Paulo", "SP");
    }
}
