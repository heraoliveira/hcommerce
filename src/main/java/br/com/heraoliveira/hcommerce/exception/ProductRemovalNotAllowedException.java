package br.com.heraoliveira.hcommerce.exception;

public class ProductRemovalNotAllowedException extends RuntimeException {
    public ProductRemovalNotAllowedException(String message) {
        super(message);
    }
}
