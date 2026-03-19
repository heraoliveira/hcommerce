package br.com.heraoliveira.hcommerce.models;

import br.com.heraoliveira.hcommerce.exception.InvalidCepException;
import br.com.heraoliveira.hcommerce.exception.InvalidDataException;
import br.com.heraoliveira.hcommerce.util.ZipValidation;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Address(
        @JsonProperty("cep") String zip,
        @JsonProperty("logradouro") String street,
        @JsonProperty("bairro") String neighborhood,
        @JsonProperty("localidade") String city,
        @JsonProperty("uf") String state
) {

    public Address {
        if (!ZipValidation.isValid(zip))
            throw new InvalidCepException("ZIP is invalid.");
        if (street == null || street.isBlank())
            throw new InvalidDataException("Street cannot be null or blank.");
        if (neighborhood == null || neighborhood.isBlank())
            throw new InvalidDataException("Neighborhood cannot be null or blank.");
        if (city == null || city.isBlank())
            throw new InvalidDataException("City cannot be null or blank.");
        if (state == null || state.isBlank())
            throw new InvalidDataException("State cannot be null or blank.");
    }
}