package br.com.heraoliveira.hcommerce.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MoneyFormatterTest {

    @Test
    void shouldFormatValuesUsingBrazilianCurrencyPresentation() {
        assertEquals("R$ 1.234,56", MoneyFormatter.format(new BigDecimal("1234.56")));
    }
}
