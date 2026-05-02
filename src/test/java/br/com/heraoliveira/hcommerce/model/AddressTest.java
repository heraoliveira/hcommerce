package br.com.heraoliveira.hcommerce.model;

import br.com.heraoliveira.hcommerce.exception.InvalidDataException;
import br.com.heraoliveira.hcommerce.util.JsonUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AddressTest {

    @Test
    void shouldSerializeUsingDomainFieldNames() throws Exception {
        Address address = new Address("01001-000", "Praca da Se", "Se", "Sao Paulo", "SP");

        String json = JsonUtil.MAPPER.writeValueAsString(address);

        assertTrue(json.contains("\"zip\":\"01001000\""));
        assertTrue(json.contains("\"street\":\"Praca da Se\""));
        assertTrue(json.contains("\"neighborhood\":\"Se\""));
        assertTrue(json.contains("\"city\":\"Sao Paulo\""));
        assertTrue(json.contains("\"state\":\"SP\""));
        assertTrue(!json.contains("\"cep\""));
        assertTrue(!json.contains("\"logradouro\""));
        assertTrue(!json.contains("\"bairro\""));
        assertTrue(!json.contains("\"localidade\""));
        assertTrue(!json.contains("\"uf\""));
    }

    @Test
    void shouldDeserializeLegacyViaCepFieldNames() throws Exception {
        Address address = JsonUtil.MAPPER.readValue("""
                {
                  "cep": "01001-000",
                  "logradouro": "Praca da Se",
                  "bairro": "Se",
                  "localidade": "Sao Paulo",
                  "uf": "SP"
                }
                """, Address.class);

        assertEquals("01001000", address.zip());
        assertEquals("Praca da Se", address.street());
        assertEquals("Se", address.neighborhood());
        assertEquals("Sao Paulo", address.city());
        assertEquals("SP", address.state());
    }

    @Test
    void shouldNormalizeOptionalFieldsAndTrimRequiredFields() {
        Address address = new Address("01001-000", null, " Se ", " Sao Paulo ", " SP ");

        assertEquals("", address.street());
        assertEquals("Se", address.neighborhood());
        assertEquals("Sao Paulo", address.city());
        assertEquals("SP", address.state());
    }

    @Test
    void shouldRejectBlankCity() {
        assertThrows(
                InvalidDataException.class,
                () -> new Address("01001-000", "Praca da Se", "Se", "   ", "SP")
        );
    }
}
