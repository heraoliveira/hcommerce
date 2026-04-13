package br.com.heraoliveira.hcommerce.infra;

import br.com.heraoliveira.hcommerce.exception.ExternalServiceException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.Objects;

public class HttpClientFetcher implements HttpFetcher {
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(5);

    private final HttpClient client;

    public HttpClientFetcher() {
        this(HttpClient.newBuilder()
                .connectTimeout(REQUEST_TIMEOUT)
                .build());
    }

    public HttpClientFetcher(HttpClient client) {
        this.client = Objects.requireNonNull(client, "HTTP client cannot be null.");
    }

    @Override
    public String fetch(String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(REQUEST_TIMEOUT)
                .build();
        try {
            HttpResponse<String> response = client
                    .send(request, HttpResponse.BodyHandlers.ofString());
            if  (response.statusCode() >= 300)
                throw new ExternalServiceException("The server responded with HTTP status code "
                        + response.statusCode());
            return response.body();
        } catch (HttpTimeoutException e) {
            throw new ExternalServiceException("The ZIP code lookup service did not respond in time.", e);
        } catch (IOException e) {
            throw new ExternalServiceException("The ZIP code lookup service is currently unavailable.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ExternalServiceException("The HTTP request was unexpectedly interrupted " +
                    "before completion.", e);
        }
    }
}
