package br.com.heraoliveira.hcommerce;

import br.com.heraoliveira.hcommerce.console.ConsoleApplication;
import br.com.heraoliveira.hcommerce.repository.CurrentCustomerRepository;
import br.com.heraoliveira.hcommerce.repository.OrderRepository;
import br.com.heraoliveira.hcommerce.repository.ProductRepository;
import br.com.heraoliveira.hcommerce.util.DebugMode;

public class Main {
    public static void main(String[] args) {
        try {
            new ConsoleApplication(
                    new ProductRepository(),
                    new OrderRepository(),
                    new CurrentCustomerRepository()
            ).run();
        } catch (IllegalStateException e) {
            reportFatalError("[STATE ERROR] " + e.getMessage(), e);
        } catch (Exception e) {
            reportFatalError(
                    "[UNEXPECTED ERROR] The application closed because of an unexpected error.",
                    e
            );
        }
    }

    private static void reportFatalError(String message, Exception exception) {
        System.out.println(message);

        if (DebugMode.isEnabled()) {
            exception.printStackTrace();
        }
    }
}
