package br.com.heraoliveira.hcommerce.repository;

import br.com.heraoliveira.hcommerce.exception.InvalidDataException;
import br.com.heraoliveira.hcommerce.models.Customer;
import br.com.heraoliveira.hcommerce.util.JsonUtil;
import br.com.heraoliveira.hcommerce.util.PersistencePaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

public class CurrentCustomerRepository {
    private static final String FILE_NAME = "customer.json";
    private static final String LOAD_ERROR_MESSAGE = "Failed to load the current customer from the JSON file.";
    private static final String SAVE_ERROR_MESSAGE = "Failed to save the current customer to the JSON file.";

    private final Path filePath;
    private Customer currentCustomer;

    public CurrentCustomerRepository() {
        this(PersistencePaths.dataDirectory());
    }

    public CurrentCustomerRepository(Path dataDirectory) {
        this.filePath = resolveFilePath(dataDirectory);
        this.currentCustomer = loadFromFile();
    }

    public Optional<Customer> findCurrent() {
        return Optional.ofNullable(currentCustomer);
    }

    public void saveCurrent(Customer customer) {
        if (customer == null) {
            throw new InvalidDataException("Customer cannot be null.");
        }

        this.currentCustomer = customer;
        writeToFile();
    }

    private Customer loadFromFile() {
        try {
            createStorageDirectoryIfNeeded();

            if (Files.notExists(filePath)) {
                return null;
            }

            String content = Files.readString(filePath).strip();
            if (content.isEmpty()) {
                return null;
            }

            return JsonUtil.MAPPER.readValue(filePath.toFile(), Customer.class);
        } catch (IOException e) {
            throw new IllegalStateException(LOAD_ERROR_MESSAGE, e);
        }
    }

    private void writeToFile() {
        try {
            createStorageDirectoryIfNeeded();
            JsonUtil.MAPPER.writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), currentCustomer);
        } catch (IOException e) {
            throw new IllegalStateException(SAVE_ERROR_MESSAGE, e);
        }
    }

    private void createStorageDirectoryIfNeeded() throws IOException {
        Path parentDirectory = filePath.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }
    }

    private static Path resolveFilePath(Path dataDirectory) {
        return Objects.requireNonNull(dataDirectory, "Data directory cannot be null.")
                .toAbsolutePath()
                .normalize()
                .resolve(FILE_NAME);
    }
}
