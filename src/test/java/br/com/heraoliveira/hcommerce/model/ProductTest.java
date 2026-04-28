package br.com.heraoliveira.hcommerce.model;

import br.com.heraoliveira.hcommerce.exception.InvalidDataException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductTest {

    @Test
    void shouldNormalizeFieldsWhenCreatingAProduct() {
        Product product = new Product(1L, " Notebook ", " Portable computer ", new BigDecimal("3500.00"));

        assertEquals("Notebook", product.getName());
        assertEquals("Portable computer", product.getDescription());
    }

    @Test
    void shouldRejectBlankProductName() {
        assertThrows(
                InvalidDataException.class,
                () -> new Product(1L, "   ", "Portable computer", new BigDecimal("3500.00"))
        );
    }

    @Test
    void shouldRejectNonPositivePrice() {
        assertThrows(
                InvalidDataException.class,
                () -> new Product(1L, "Notebook", "Portable computer", BigDecimal.ZERO)
        );
    }
}
