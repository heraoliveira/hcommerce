package br.com.heraoliveira.hcommerce.repository;

import br.com.heraoliveira.hcommerce.exception.InvalidDataException;
import br.com.heraoliveira.hcommerce.model.Order;
import br.com.heraoliveira.hcommerce.util.PersistencePaths;
import com.fasterxml.jackson.core.type.TypeReference;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class OrderRepository extends AbstractJsonRepository<Order> {

    private static final String FILE_NAME = "orders.json";
    private static final TypeReference<List<Order>> LIST_TYPE = new TypeReference<List<Order>>() {};
    private static final String LOAD_ERROR_MESSAGE = "Failed to load orders from the JSON file.";
    private static final String SAVE_ERROR_MESSAGE = "Failed to save orders to the JSON file.";

    public OrderRepository() {
        this(PersistencePaths.dataDirectory());
    }

    public OrderRepository(Path dataDirectory) {
        super(resolveFilePath(dataDirectory), LIST_TYPE, LOAD_ERROR_MESSAGE, SAVE_ERROR_MESSAGE);
    }

    @Override
    protected void validateEntity(Order order) {
        if (order == null) {
            throw new InvalidDataException("Order cannot be null.");
        }
    }

    @Override
    protected long getEntityId(Order order) {
        return order.getId();
    }

    private static Path resolveFilePath(Path dataDirectory) {
        return Objects.requireNonNull(dataDirectory, "Data directory cannot be null.")
                .toAbsolutePath()
                .normalize()
                .resolve(FILE_NAME);
    }
}
