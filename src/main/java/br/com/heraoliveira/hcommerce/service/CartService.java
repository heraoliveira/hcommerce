package br.com.heraoliveira.hcommerce.service;

import br.com.heraoliveira.hcommerce.exception.InvalidCartException;
import br.com.heraoliveira.hcommerce.model.Cart;
import br.com.heraoliveira.hcommerce.model.Product;
import br.com.heraoliveira.hcommerce.repository.ProductRepository;

import java.util.Objects;

public class CartService {
    private final ProductRepository productRepository;

    public CartService(ProductRepository productRepository) {
        this.productRepository = Objects.requireNonNull(
                productRepository,
                "Product repository cannot be null."
        );
    }

    public void addProductToCart(Cart cart, long productId, int quantity) {
        validateCart(cart);

        Product product = productRepository.findById(productId);
        cart.addItem(product, quantity);
    }

    public void updateProductQuantity(Cart cart, long productId, int quantity) {
        validateCart(cart);

        cart.updateQuantity(productId, quantity);
    }

    public void removeProductFromCart(Cart cart, long productId) {
        validateCart(cart);

        cart.removeItem(productId);
    }

    private static void validateCart(Cart cart) {
        if (cart == null) {
            throw new InvalidCartException("Cart cannot be null.");
        }
    }
}
