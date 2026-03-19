package br.com.heraoliveira.hcommerce.service;

import br.com.heraoliveira.hcommerce.exception.InvalidCepException;
import br.com.heraoliveira.hcommerce.infra.HttpConsumer;
import br.com.heraoliveira.hcommerce.models.Address;
import br.com.heraoliveira.hcommerce.util.ZipValidation;
import com.fasterxml.jackson.databind.ObjectMapper;


public class ViaCepService {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Classe não terminada, retornará um Address
    public static void fetchAddress(String zip) {
        if (!ZipValidation.isValid(zip)) throw new InvalidCepException("ZIP is invalid.");

        var url = "https://viacep.com.br/ws/" + zip + "/json/";
        var json = HttpConsumer.consumer(url);
    }
}