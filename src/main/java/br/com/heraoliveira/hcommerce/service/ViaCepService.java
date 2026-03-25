package br.com.heraoliveira.hcommerce.service;

import br.com.heraoliveira.hcommerce.exception.ExternalServiceException;
import br.com.heraoliveira.hcommerce.exception.InvalidCepException;
import br.com.heraoliveira.hcommerce.infra.HttpConsumer;
import br.com.heraoliveira.hcommerce.models.Address;
import br.com.heraoliveira.hcommerce.util.ZipValidation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class ViaCepService {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Address fetchAddress(String zip) {
        if (zip == null || zip.isEmpty()) throw new InvalidCepException("Validation Error: ZIP cannot be null or " +
                "empty.");
        var zipFormated = zip.replaceAll("\\D", "");

        if (!ZipValidation.isValid(zipFormated))
            throw new InvalidCepException("Validation Error: Invalid ZIP code format.");

        var url = "https://viacep.com.br/ws/" + zipFormated + "/json/";
        var json = HttpConsumer.consumer(url);

        try {
            var readTree = objectMapper.readTree(json);
            if (readTree.has("erro") && readTree.get("erro").asBoolean()) {
                throw new InvalidCepException("Not Found Error: ZIP code does not exist.");
            }
            return objectMapper.readValue(json, Address.class);
        } catch (JsonProcessingException e) {
            throw new ExternalServiceException("Processing Error: Unable to parse address data.", e);
        }
    }
}