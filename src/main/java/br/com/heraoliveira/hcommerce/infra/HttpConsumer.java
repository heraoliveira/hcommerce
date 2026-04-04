package br.com.heraoliveira.hcommerce.infra;

import br.com.heraoliveira.hcommerce.exception.ExternalServiceException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpConsumer {
    private static final HttpClient client = HttpClient.newHttpClient();

    public static String consumer(String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
        try {
            HttpResponse<String> response = client
                    .send(request, HttpResponse.BodyHandlers.ofString());
            if  (response.statusCode() >= 300)
                throw new ExternalServiceException("Integration Error: Server responded with HTTP status code "
                        + response.statusCode());
            return response.body();
        } catch (IOException e) {
            throw new ExternalServiceException("Integration Error: Failed to communicate with the external API " +
                    "via network.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ExternalServiceException("Integration Error: The HTTP request was unexpectedly interrupted " +
                    "before completion.", e);
        }
    }
}