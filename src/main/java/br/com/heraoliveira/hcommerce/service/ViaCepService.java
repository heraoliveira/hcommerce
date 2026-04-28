package br.com.heraoliveira.hcommerce.service;

import br.com.heraoliveira.hcommerce.exception.ExternalServiceException;
import br.com.heraoliveira.hcommerce.exception.InvalidCepException;
import br.com.heraoliveira.hcommerce.infra.HttpFetcher;
import br.com.heraoliveira.hcommerce.model.Address;
import br.com.heraoliveira.hcommerce.util.JsonUtil;
import br.com.heraoliveira.hcommerce.util.ZipValidation;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Objects;

public class ViaCepService implements AddressLookupService {
    private final HttpFetcher httpFetcher;

    public ViaCepService(HttpFetcher httpFetcher) {
        this.httpFetcher = Objects.requireNonNull(httpFetcher, "HTTP fetcher cannot be null.");
    }

    @Override
    public Address fetchAddress(String zip) {
        if (zip == null || zip.isBlank())
            throw new InvalidCepException("ZIP code cannot be null or blank.");
        String normalizedZip = ZipValidation.normalize(zip);

        var url = "https://viacep.com.br/ws/" + normalizedZip + "/json/";
        String json;
        try {
            json = httpFetcher.fetch(url);
        } catch (ExternalServiceException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new ExternalServiceException("The ZIP code lookup service is currently unavailable.", e);
        }

        ViaCepResponse response = parseViaCepResponse(json);
        if (Boolean.TRUE.equals(response.error())) {
            throw new InvalidCepException("ZIP code was not found.");
        }

        return toAddress(response);
    }

    private static ViaCepResponse parseViaCepResponse(String json) {
        try {
            return JsonUtil.MAPPER.readValue(json, ViaCepResponse.class);
        } catch (JsonProcessingException e) {
            throw new ExternalServiceException("Unable to parse the address data.", e);
        }
    }

    private static Address toAddress(ViaCepResponse response) {
        return new Address(
                response.zip(),
                response.street(),
                response.neighborhood(),
                response.city(),
                response.state()
        );
    }
}
