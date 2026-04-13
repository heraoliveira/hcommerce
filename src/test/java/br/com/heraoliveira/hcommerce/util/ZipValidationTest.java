package br.com.heraoliveira.hcommerce.util;

import br.com.heraoliveira.hcommerce.exception.InvalidCepException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ZipValidationTest {

    @Test
    void shouldNormalizeAFormattedZipCode() {
        assertEquals("01001000", ZipValidation.normalize("01001-000"));
        assertTrue(ZipValidation.isNormalized("01001000"));
    }

    @Test
    void shouldRejectInvalidZipCodeFormat() {
        assertThrows(InvalidCepException.class, () -> ZipValidation.normalize("1234"));
    }
}
