package br.com.heraoliveira.hcommerce.util;

import java.nio.file.Path;

public final class PersistencePaths {
    private static final String APPLICATION_DIRECTORY_NAME = ".hcommerce";
    private static final String DATA_DIRECTORY_NAME = "data";
    private static final Path DATA_DIRECTORY = resolveDataDirectory();

    private PersistencePaths() {
    }

    public static Path productsFile() {
        return resolveDataFile("products.json");
    }

    public static Path ordersFile() {
        return resolveDataFile("orders.json");
    }

    public static Path customerFile() {
        return resolveDataFile("customer.json");
    }

    public static Path dataDirectory() {
        return DATA_DIRECTORY;
    }

    private static Path resolveDataFile(String fileName) {
        return DATA_DIRECTORY.resolve(fileName);
    }

    private static Path resolveDataDirectory() {
        return Path.of(
                System.getProperty("user.home"),
                APPLICATION_DIRECTORY_NAME,
                DATA_DIRECTORY_NAME
        ).toAbsolutePath().normalize();
    }
}
