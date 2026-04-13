package br.com.heraoliveira.hcommerce.service;

import br.com.heraoliveira.hcommerce.exception.ExternalServiceException;
import br.com.heraoliveira.hcommerce.exception.InvalidCepException;
import br.com.heraoliveira.hcommerce.exception.InvalidDataException;
import br.com.heraoliveira.hcommerce.infra.HttpFetcher;
import br.com.heraoliveira.hcommerce.models.Address;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ViaCepServiceTest {

    @Test
    void shouldParseAddressFromViaCepResponse() {
        HttpFetcher httpFetcher = url -> """
                {
                  "cep": "01001-000",
                  "logradouro": "Praca da Se",
                  "bairro": "Se",
                  "localidade": "Sao Paulo",
                  "uf": "SP"
                }
                """;

        ViaCepService viaCepService = new ViaCepService(httpFetcher);

        Address address = viaCepService.fetchAddress("01001-000");

        assertEquals("01001000", address.zip());
        assertEquals("Praca da Se", address.street());
        assertEquals("Se", address.neighborhood());
        assertEquals("Sao Paulo", address.city());
        assertEquals("SP", address.state());
    }

    @Test
    void shouldThrowWhenViaCepReturnsNotFound() {
        HttpFetcher httpFetcher = url -> "{\"erro\":true}";

        ViaCepService viaCepService = new ViaCepService(httpFetcher);

        assertThrows(InvalidCepException.class, () -> viaCepService.fetchAddress("01001-000"));
    }

    @Test
    void shouldUseTheNormalizedZipCodeInTheRequestUrl() {
        AtomicReference<String> requestedUrl = new AtomicReference<>();
        HttpFetcher httpFetcher = url -> {
            requestedUrl.set(url);
            return """
                    {
                      "cep": "01001-000",
                      "logradouro": "Praca da Se",
                      "bairro": "Se",
                      "localidade": "Sao Paulo",
                      "uf": "SP"
                    }
                    """;
        };

        ViaCepService viaCepService = new ViaCepService(httpFetcher);

        viaCepService.fetchAddress("01001-000");

        assertEquals("https://viacep.com.br/ws/01001000/json/", requestedUrl.get());
    }

    @Test
    void shouldThrowWhenJsonCannotBeParsed() {
        HttpFetcher httpFetcher = url -> "{invalid-json}";

        ViaCepService viaCepService = new ViaCepService(httpFetcher);

        assertThrows(ExternalServiceException.class, () -> viaCepService.fetchAddress("01001-000"));
    }

    @Test
    void shouldWrapRuntimeFailuresFromHttpFetcher() {
        HttpFetcher httpFetcher = url -> {
            throw new RuntimeException("boom");
        };

        ViaCepService viaCepService = new ViaCepService(httpFetcher);

        assertThrows(ExternalServiceException.class, () -> viaCepService.fetchAddress("01001-000"));
    }

    @Test
    void shouldPropagateZipValidationFailuresFromAddress() {
        HttpFetcher httpFetcher = url -> """
                {
                  "cep": "1234",
                  "logradouro": "Praca da Se",
                  "bairro": "Se",
                  "localidade": "Sao Paulo",
                  "uf": "SP"
                }
                """;

        ViaCepService viaCepService = new ViaCepService(httpFetcher);

        assertThrows(InvalidCepException.class, () -> viaCepService.fetchAddress("01001-000"));
    }

    @Test
    void shouldPropagateDomainValidationWhenRequiredExternalFieldsAreMissing() {
        HttpFetcher httpFetcher = url -> """
                {
                  "cep": "01001-000",
                  "logradouro": "Praca da Se",
                  "bairro": "Se",
                  "uf": "SP"
                }
                """;

        ViaCepService viaCepService = new ViaCepService(httpFetcher);

        assertThrows(InvalidDataException.class, () -> viaCepService.fetchAddress("01001-000"));
    }
}
