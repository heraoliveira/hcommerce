package br.com.heraoliveira.hcommerce.util;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PersistencePathsTest {

    @Test
    void shouldResolveDataDirectoryUnderTheUserHomeDirectory() {
        Path expectedDataDirectory = Path.of(System.getProperty("user.home"), ".hcommerce", "data")
                .toAbsolutePath()
                .normalize();

        assertEquals(expectedDataDirectory, PersistencePaths.dataDirectory());
        assertEquals(expectedDataDirectory.resolve("products.json"), PersistencePaths.productsFile());
        assertEquals(expectedDataDirectory.resolve("orders.json"), PersistencePaths.ordersFile());
        assertEquals(expectedDataDirectory.resolve("customer.json"), PersistencePaths.customerFile());
    }
}
