package br.com.heraoliveira.hcommerce.console;

import br.com.heraoliveira.hcommerce.exception.InvalidDataException;
import br.com.heraoliveira.hcommerce.exception.ProductNotFoundException;
import br.com.heraoliveira.hcommerce.model.Cart;
import br.com.heraoliveira.hcommerce.service.CartService;

import java.util.Scanner;

final class CartConsoleHandler {
    private static final long CANCEL_OPTION = 0L;
    private final CartService cartService;

    CartConsoleHandler(CartService cartService) {
        this.cartService = cartService;
    }

    void updateCartItemQuantity(Scanner scanner, Cart cart) {
        System.out.println("\n=== Update Product Quantity ===");

        if (cart.isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }

        while (true) {
            ConsolePrinter.printCart(cart);

            long productId = ConsoleInput.readLong(scanner, "Product ID in cart (0 to go back): ");
            if (productId == CANCEL_OPTION) {
                System.out.println("Operation canceled. Returning to the main menu.");
                return;
            }

            try {
                if (!cart.containsProduct(productId)) {
                    throw new ProductNotFoundException(
                            "Product with ID " + productId + " was not found in the cart."
                    );
                }

                int quantity = ConsoleInput.readPositiveInt(scanner, "New quantity: ");
                cartService.updateProductQuantity(cart, productId, quantity);

                System.out.println("\nQuantity updated successfully.");
                return;
            } catch (ProductNotFoundException e) {
                System.out.println("Product not found in the cart. Enter a listed ID or type 0 to go back.");
            } catch (InvalidDataException e) {
                System.out.println("Invalid quantity. Enter a value greater than zero.");
            }
        }
    }

    void removeCartItem(Scanner scanner, Cart cart) {
        System.out.println("\n=== Remove Product from Cart ===");

        if (cart.isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }

        while (true) {
            ConsolePrinter.printCart(cart);

            long productId = ConsoleInput.readLong(scanner, "Product ID to remove (0 to go back): ");
            if (productId == CANCEL_OPTION) {
                System.out.println("Operation canceled. Returning to the main menu.");
                return;
            }

            try {
                cartService.removeProductFromCart(cart, productId);

                System.out.println("\nProduct removed successfully.");
                return;
            } catch (ProductNotFoundException e) {
                System.out.println("Product not found in the cart. Enter a listed ID or type 0 to go back.");
            }
        }
    }
}
