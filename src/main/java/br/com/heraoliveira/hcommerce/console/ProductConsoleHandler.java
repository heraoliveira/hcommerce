package br.com.heraoliveira.hcommerce.console;

import br.com.heraoliveira.hcommerce.exception.InvalidDataException;
import br.com.heraoliveira.hcommerce.exception.ProductNotFoundException;
import br.com.heraoliveira.hcommerce.exception.ProductRemovalNotAllowedException;
import br.com.heraoliveira.hcommerce.models.Cart;
import br.com.heraoliveira.hcommerce.models.Product;
import br.com.heraoliveira.hcommerce.service.ProductService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

final class ProductConsoleHandler {
    private static final long CANCEL_OPTION = 0L;
    private static final String CANCEL_OPTION_TEXT = "0";

    private final ProductService productService;

    ProductConsoleHandler(ProductService productService) {
        this.productService = productService;
    }

    void registerProduct(Scanner scanner) {
        System.out.println("\n=== Register Product ===");

        String name = ConsoleInput.readRequiredText(scanner, "Name: ");
        String description = ConsoleInput.readRequiredText(scanner, "Description: ");
        BigDecimal price = ConsoleInput.readBigDecimal(scanner, "Price: ");

        productService.registerProduct(name, description, price);

        System.out.println("\nProduct registered successfully.");
    }

    void printCatalog() {
        ConsolePrinter.printCatalog(productService.findAllProducts());
    }

    void searchProducts(Scanner scanner) {
        System.out.println("\n=== Search Products ===");

        List<Product> products = productService.findAllProducts();
        if (products.isEmpty()) {
            System.out.println("No products registered.");
            return;
        }

        while (true) {
            System.out.println("1 - Search by ID");
            System.out.println("2 - Search by name");
            System.out.println("0 - Return to the main menu");
            String option = scanner.nextLine().strip();

            switch (option) {
                case "1" -> {
                    searchProductById(scanner);
                    return;
                }
                case "2" -> {
                    searchProductsByName(scanner);
                    return;
                }
                case CANCEL_OPTION_TEXT -> {
                    System.out.println("Operation canceled. Returning to the main menu.");
                    return;
                }
                default -> System.out.println("Invalid option. Choose one of the listed options.");
            }
        }
    }

    void removeProduct(Scanner scanner, Cart activeCart) {
        System.out.println("\n=== Remove Product from Catalog ===");

        if (productService.findAllProducts().isEmpty()) {
            System.out.println("No products registered.");
            return;
        }

        while (true) {
            ConsolePrinter.printCatalog(productService.findAllProducts());

            long productId = ConsoleInput.readLong(scanner, "Product ID to remove (0 to go back): ");
            if (productId == CANCEL_OPTION) {
                System.out.println("Operation canceled. Returning to the main menu.");
                return;
            }

            try {
                productService.removeProduct(productId, activeCart);
                System.out.println("\nProduct removed from the catalog.");
                return;
            } catch (ProductRemovalNotAllowedException e) {
                System.out.println("This product cannot be removed because it is currently in the active cart.");
            } catch (ProductNotFoundException e) {
                System.out.println("Product not found. Enter a listed ID or type 0 to go back.");
            }
        }
    }

    void addProductToCart(Scanner scanner, Cart cart) {
        System.out.println("\n=== Add Product to Cart ===");

        List<Product> products = productService.findAllProducts();
        if (products.isEmpty()) {
            System.out.println("No products registered.");
            return;
        }

        while (true) {
            ConsolePrinter.printCatalog(products);

            long productId = ConsoleInput.readLong(scanner, "Product ID (0 to go back): ");
            if (productId == CANCEL_OPTION) {
                System.out.println("Operation canceled. Returning to the main menu.");
                return;
            }

            try {
                int quantity = ConsoleInput.readPositiveInt(scanner, "Quantity: ");
                productService.addProductToCart(cart, productId, quantity);

                System.out.println("\nProduct added to the cart.");
                return;
            } catch (ProductNotFoundException e) {
                System.out.println("Product not found. Enter a listed ID or type 0 to go back.");
            } catch (InvalidDataException e) {
                System.out.println("Invalid quantity. Enter a value greater than zero.");
            }
        }
    }

    private void searchProductById(Scanner scanner) {
        while (true) {
            long productId = ConsoleInput.readLong(scanner, "Product ID (0 to go back): ");
            if (productId == CANCEL_OPTION) {
                System.out.println("Operation canceled. Returning to the main menu.");
                return;
            }

            try {
                ConsolePrinter.printProductSearchResults(List.of(productService.findProductById(productId)));
                return;
            } catch (ProductNotFoundException e) {
                System.out.println("Product not found. Enter a listed ID or type 0 to go back.");
            }
        }
    }

    private void searchProductsByName(Scanner scanner) {
        while (true) {
            String productName = ConsoleInput.readRequiredText(
                    scanner,
                    "Product name (0 to go back): "
            );

            if (CANCEL_OPTION_TEXT.equals(productName)) {
                System.out.println("Operation canceled. Returning to the main menu.");
                return;
            }

            List<Product> matchingProducts = productService.findProductsByName(productName);
            if (matchingProducts.isEmpty()) {
                System.out.println("No products matched that name. Try again or type 0 to go back.");
                continue;
            }

            ConsolePrinter.printProductSearchResults(matchingProducts);
            return;
        }
    }
}
