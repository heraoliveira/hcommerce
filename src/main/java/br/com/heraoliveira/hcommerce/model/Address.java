package br.com.heraoliveira.hcommerce.model;

import br.com.heraoliveira.hcommerce.exception.InvalidDataException;
import br.com.heraoliveira.hcommerce.util.ZipValidation;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Address(
        @JsonProperty("zip") @JsonAlias("cep") String zip,
        @JsonProperty("street") @JsonAlias("logradouro") String street,
        @JsonProperty("neighborhood") @JsonAlias("bairro") String neighborhood,
        @JsonProperty("city") @JsonAlias("localidade") String city,
        @JsonProperty("state") @JsonAlias("uf") String state
){

    public Address {
        zip = ZipValidation.normalize(zip);
        street = normalizeOptionalField(street);
        neighborhood = normalizeOptionalField(neighborhood);
        city = validateRequiredField(city, "City");
        state = validateRequiredField(state, "State");
    }

    private static String validateRequiredField(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new InvalidDataException(
                    fieldName + " is required and cannot be null or blank."
            );
        }
        return value.strip();
    }

    private static String normalizeOptionalField(String value) {
        return value == null ? "" : value.strip();
    }
}
