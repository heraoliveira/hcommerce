package br.com.heraoliveira.hcommerce.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
record ViaCepResponse(
        @JsonProperty("cep") String zip,
        @JsonProperty("logradouro") String street,
        @JsonProperty("bairro") String neighborhood,
        @JsonProperty("localidade") String city,
        @JsonProperty("uf") String state,
        @JsonProperty("erro") Boolean error
) {
}
