package br.com.heraoliveira.hcommerce.service;

import br.com.heraoliveira.hcommerce.exception.InvalidCartException;
import br.com.heraoliveira.hcommerce.exception.InvalidDataException;
import br.com.heraoliveira.hcommerce.exception.ProductNotFoundException;
import br.com.heraoliveira.hcommerce.model.Cart;
import br.com.heraoliveira.hcommerce.model.Product;
import br.com.heraoliveira.hcommerce.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CartServiceTest {

    @TempDir
    Path tempDirectory;

    @Test
    void shouldAddCatalogProductToCart() {
        ProductRepository productRepository = new ProductRepository(tempDirectory);
        CartService cartService = new CartService(productRepository);
        Product product = product(1L, "Notebook", "Portable computer", "2500.00");
        Cart cart = new Cart();

        productRepository.save(product);

        cartService.addProductToCart(cart, product.getId(), 2);

        assertEquals(1, cart.getItems().size());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        assertEquals("Notebook", cart.getItems().get(0).getProduct().getName());
    }

    @Test
    void shouldRejectAddingMissingProductToCart() {
        ProductRepository productRepository = new ProductRepository(tempDirectory);
        CartService cartService = new CartService(productRepository);
        Cart cart = new Cart();

        assertThrows(ProductNotFoundException.class, () -> cartService.addProductToCart(cart, 99L, 1));
    }

    @Test
    void shouldRejectInvalidCartOperations() {
        ProductRepository productRepository = new ProductRepository(tempDirectory);
        CartService cartService = new CartService(productRepository);

        assertThrows(InvalidCartException.class, () -> cartService.addProductToCart(null, 1L, 1));
        assertThrows(InvalidCartException.class, () -> cartService.updateProductQuantity(null, 1L, 1));
        assertThrows(InvalidCartException.class, () -> cartService.removeProductFromCart(null, 1L));
    }

    @Test
    void shouldPropagateInvalidQuantityWhenAddingProductToCart() {
        ProductRepository productRepository = new ProductRepository(tempDirectory);
        CartService cartService = new CartService(productRepository);
        Product product = product(1L, "Notebook", "Portable computer", "2500.00");
        Cart cart = new Cart();

        productRepository.save(product);

        assertThrows(InvalidDataException.class, () -> cartService.addProductToCart(cart, product.getId(), 0));
    }

    private static Product product(long id, String name, String description, String price) {
        return new Product(id, name, description, new BigDecimal(price));
    }
}
