package br.com.heraoliveira.hcommerce.infra;

import br.com.heraoliveira.hcommerce.exception.ExternalServiceException;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.net.http.WebSocket;
import java.security.cert.Certificate;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpClientFetcherTest {

    @Test
    void shouldReturnTheResponseBodyWhenTheRequestSucceeds() {
        HttpClientFetcher fetcher = new HttpClientFetcher(
                new StubHttpClient(request -> new StubHttpResponse(request, 200, "ok"))
        );

        assertEquals("ok", fetcher.fetch("https://example.com/test"));
    }

    @Test
    void shouldThrowWhenTheServerReturnsAnErrorStatus() {
        HttpClientFetcher fetcher = new HttpClientFetcher(
                new StubHttpClient(request -> new StubHttpResponse(request, 503, "unavailable"))
        );

        ExternalServiceException exception = assertThrows(
                ExternalServiceException.class,
                () -> fetcher.fetch("https://example.com/test")
        );

        assertEquals("The server responded with HTTP status code 503", exception.getMessage());
    }

    @Test
    void shouldWrapTimeoutFailures() {
        HttpClientFetcher fetcher = new HttpClientFetcher(
                new StubHttpClient(request -> {
                    throw new HttpTimeoutException("timed out");
                })
        );

        ExternalServiceException exception = assertThrows(
                ExternalServiceException.class,
                () -> fetcher.fetch("https://example.com/test")
        );

        assertEquals("The ZIP code lookup service did not respond in time.", exception.getMessage());
    }

    @Test
    void shouldWrapIoFailuresAsUnavailableService() {
        HttpClientFetcher fetcher = new HttpClientFetcher(
                new StubHttpClient(request -> {
                    throw new IOException("connection reset");
                })
        );

        ExternalServiceException exception = assertThrows(
                ExternalServiceException.class,
                () -> fetcher.fetch("https://example.com/test")
        );

        assertEquals("The ZIP code lookup service is currently unavailable.", exception.getMessage());
    }

    @Test
    void shouldPreserveTheInterruptFlagWhenTheRequestIsInterrupted() {
        Thread.interrupted();
        HttpClientFetcher fetcher = new HttpClientFetcher(
                new StubHttpClient(request -> {
                    throw new InterruptedException("stopped");
                })
        );

        ExternalServiceException exception = assertThrows(
                ExternalServiceException.class,
                () -> fetcher.fetch("https://example.com/test")
        );

        assertEquals(
                "The HTTP request was unexpectedly interrupted before completion.",
                exception.getMessage()
        );
        assertTrue(Thread.currentThread().isInterrupted());
        Thread.interrupted();
    }

    @FunctionalInterface
    private interface Sender {
        HttpResponse<String> send(HttpRequest request) throws IOException, InterruptedException;
    }

    private static final class StubHttpClient extends HttpClient {
        private final Sender sender;

        private StubHttpClient(Sender sender) {
            this.sender = sender;
        }

        @Override
        public Optional<CookieHandler> cookieHandler() {
            return Optional.empty();
        }

        @Override
        public Optional<Duration> connectTimeout() {
            return Optional.empty();
        }

        @Override
        public Redirect followRedirects() {
            return Redirect.NEVER;
        }

        @Override
        public Optional<ProxySelector> proxy() {
            return Optional.empty();
        }

        @Override
        public SSLContext sslContext() {
            return null;
        }

        @Override
        public SSLParameters sslParameters() {
            return new SSLParameters();
        }

        @Override
        public Optional<Authenticator> authenticator() {
            return Optional.empty();
        }

        @Override
        public Version version() {
            return Version.HTTP_1_1;
        }

        @Override
        public Optional<Executor> executor() {
            return Optional.empty();
        }

        @Override
        public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler)
                throws IOException, InterruptedException {
            @SuppressWarnings("unchecked")
            HttpResponse<T> response = (HttpResponse<T>) sender.send(request);
            return response;
        }

        @Override
        public <T> CompletableFuture<HttpResponse<T>> sendAsync(
                HttpRequest request,
                HttpResponse.BodyHandler<T> responseBodyHandler
        ) {
            throw new UnsupportedOperationException("sendAsync is not used in these tests.");
        }

        @Override
        public <T> CompletableFuture<HttpResponse<T>> sendAsync(
                HttpRequest request,
                HttpResponse.BodyHandler<T> responseBodyHandler,
                HttpResponse.PushPromiseHandler<T> pushPromiseHandler
        ) {
            throw new UnsupportedOperationException("sendAsync is not used in these tests.");
        }

        @Override
        public WebSocket.Builder newWebSocketBuilder() {
            throw new UnsupportedOperationException("WebSocket support is not used in these tests.");
        }
    }

    private static final class StubHttpResponse implements HttpResponse<String> {
        private final HttpRequest request;
        private final int statusCode;
        private final String body;

        private StubHttpResponse(HttpRequest request, int statusCode, String body) {
            this.request = request;
            this.statusCode = statusCode;
            this.body = body;
        }

        @Override
        public int statusCode() {
            return statusCode;
        }

        @Override
        public HttpRequest request() {
            return request;
        }

        @Override
        public Optional<HttpResponse<String>> previousResponse() {
            return Optional.empty();
        }

        @Override
        public HttpHeaders headers() {
            return HttpHeaders.of(Map.of(), (name, value) -> true);
        }

        @Override
        public String body() {
            return body;
        }

        @Override
        public Optional<SSLSession> sslSession() {
            return Optional.empty();
        }

        @Override
        public URI uri() {
            return request.uri();
        }

        @Override
        public HttpClient.Version version() {
            return HttpClient.Version.HTTP_1_1;
        }
    }
}
