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
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new ExternalServiceException("Integration failure.", e);
        }
    }
}
