package br.com.heraoliveira.hcommerce.service;

import br.com.heraoliveira.hcommerce.exception.ProductNotFoundException;
import br.com.heraoliveira.hcommerce.exception.ProductRemovalNotAllowedException;
import br.com.heraoliveira.hcommerce.models.Cart;
import br.com.heraoliveira.hcommerce.models.Product;
import br.com.heraoliveira.hcommerce.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductServiceTest {

    @TempDir
    Path tempDirectory;

    @Test
    void shouldAllowRemovingAProductThatIsNotInTheActiveCart() {
        ProductRepository productRepository = new ProductRepository(tempDirectory);
        ProductService productService = new ProductService(productRepository);
        Product product = product(1L, "Notebook", "Portable computer", "2500.00");
        Cart activeCart = new Cart();

        productRepository.save(product);

        productService.removeProduct(product.getId(), activeCart);

        assertEquals(0, productRepository.findAll().size());
        assertThrows(ProductNotFoundException.class, () -> productRepository.findById(product.getId()));
    }

    @Test
    void shouldBlockRemovingAProductThatIsInTheActiveCart() {
        ProductRepository productRepository = new ProductRepository(tempDirectory);
        ProductService productService = new ProductService(productRepository);
        Product product = product(1L, "Notebook", "Portable computer", "2500.00");
        Cart activeCart = new Cart();

        productRepository.save(product);
        activeCart.addItem(product, 1);

        ProductRemovalNotAllowedException exception = assertThrows(
                ProductRemovalNotAllowedException.class,
                () -> productService.removeProduct(product.getId(), activeCart)
        );

        assertEquals(
                "Product with ID 1 cannot be removed because it is in the active cart.",
                exception.getMessage()
        );
        assertEquals(1, productRepository.findAll().size());
        assertEquals("Notebook", productRepository.findById(product.getId()).getName());
    }

    private static Product product(long id, String name, String description, String price) {
        return new Product(id, name, description, new BigDecimal(price));
    }
}
