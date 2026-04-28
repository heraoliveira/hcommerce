package br.com.heraoliveira.hcommerce.service;

import br.com.heraoliveira.hcommerce.exception.ProductRemovalNotAllowedException;
import br.com.heraoliveira.hcommerce.exception.InvalidCartException;
import br.com.heraoliveira.hcommerce.model.Cart;
import br.com.heraoliveira.hcommerce.model.Product;
import br.com.heraoliveira.hcommerce.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = Objects.requireNonNull(productRepository, "Product repository cannot be null.");
    }

    public Product registerProduct(String name, String description, BigDecimal price) {
        Product product = new Product(
                productRepository.nextId(),
                name,
                description,
                price
        );

        productRepository.save(product);
        return product;
    }

    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    public Product findProductById(long productId) {
        return productRepository.findById(productId);
    }

    public List<Product> findProductsByName(String productName) {
        return productRepository.findByNameContaining(productName);
    }

    public void removeProduct(long productId, Cart activeCart) {
        if (activeCart == null) {
            throw new InvalidCartException("Active cart cannot be null.");
        }

        productRepository.findById(productId);

        if (isProductInActiveCart(activeCart, productId)) {
            throw new ProductRemovalNotAllowedException(
                    "Product with ID " + productId + " cannot be removed because it is in the active cart."
            );
        }

        productRepository.removeById(productId);
    }

    private static boolean isProductInActiveCart(Cart activeCart, long productId) {
        return activeCart.getItems().stream()
                .anyMatch(item -> item.getProduct().getId() == productId);
    }
}
