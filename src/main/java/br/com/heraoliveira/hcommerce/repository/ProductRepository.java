package br.com.heraoliveira.hcommerce.repository;

import br.com.heraoliveira.hcommerce.exception.InvalidDataException;
import br.com.heraoliveira.hcommerce.exception.ProductNotFoundException;
import br.com.heraoliveira.hcommerce.model.Product;
import br.com.heraoliveira.hcommerce.util.PersistencePaths;
import com.fasterxml.jackson.core.type.TypeReference;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ProductRepository extends AbstractJsonRepository<Product> {

    private static final String FILE_NAME = "products.json";
    private static final TypeReference<List<Product>> LIST_TYPE = new TypeReference<List<Product>>() {};
    private static final String LOAD_ERROR_MESSAGE = "Failed to load products from the JSON file.";
    private static final String SAVE_ERROR_MESSAGE = "Failed to save products to the JSON file.";

    public ProductRepository() {
        this(PersistencePaths.dataDirectory());
    }

    public ProductRepository(Path dataDirectory) {
        super(resolveFilePath(dataDirectory), LIST_TYPE, LOAD_ERROR_MESSAGE, SAVE_ERROR_MESSAGE);
    }

    public Product findById(long id) {
        return findByIdInternal(id)
                .orElseThrow(() -> new ProductNotFoundException(
                        "Product with ID " + id + " was not found."
                ));
    }

    public List<Product> findByNameContaining(String searchTerm) {
        if (searchTerm == null || searchTerm.isBlank()) {
            throw new InvalidDataException("Product name cannot be null or blank.");
        }

        String normalizedSearchTerm = searchTerm.strip().toLowerCase(Locale.ROOT);

        return findAll().stream()
                .filter(product -> product.getName().toLowerCase(Locale.ROOT).contains(normalizedSearchTerm))
                .toList();
    }

    public void removeById(long id) {
        if (!removeByIdInternal(id)) {
            throw new ProductNotFoundException("Product with ID " + id + " was not found.");
        }
    }

    @Override
    protected void validateEntity(Product product) {
        if (product == null) {
            throw new InvalidDataException("Product cannot be null.");
        }
    }

    @Override
    protected long getEntityId(Product product) {
        return product.getId();
    }

    private static Path resolveFilePath(Path dataDirectory) {
        return Objects.requireNonNull(dataDirectory, "Data directory cannot be null.")
                .toAbsolutePath()
                .normalize()
                .resolve(FILE_NAME);
    }
}
