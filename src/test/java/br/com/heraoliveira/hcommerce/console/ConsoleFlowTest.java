package br.com.heraoliveira.hcommerce.console;

import br.com.heraoliveira.hcommerce.model.Cart;
import br.com.heraoliveira.hcommerce.model.Product;
import br.com.heraoliveira.hcommerce.repository.ProductRepository;
import br.com.heraoliveira.hcommerce.service.CartService;
import br.com.heraoliveira.hcommerce.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsoleFlowTest {

    @TempDir
    Path tempDirectory;

    @Test
    void shouldRejectMissingCatalogProductBeforePromptingForQuantity() {
        ProductRepository productRepository = new ProductRepository(tempDirectory);
        ProductService productService = new ProductService(productRepository);
        CartService cartService = new CartService(productRepository);
        ProductConsoleHandler handler = new ProductConsoleHandler(productService, cartService);
        Cart cart = new Cart();

        productRepository.save(product(1L, "Notebook", "Portable computer", "2500.00"));

        String output = captureOutput(() -> handler.addProductToCart(
                scannerWithInput("99\n0\n"),
                cart
        ));

        assertTrue(output.contains("Product not found. Enter a listed ID or type 0 to go back."));
        assertFalse(output.contains("Quantity:"));
        assertTrue(cart.isEmpty());
    }

    @Test
    void shouldRejectMissingCartItemBeforePromptingForNewQuantity() {
        ProductRepository productRepository = new ProductRepository(tempDirectory);
        CartService cartService = new CartService(productRepository);
        CartConsoleHandler handler = new CartConsoleHandler(cartService);
        Cart cart = new Cart();

        cart.addItem(product(1L, "Notebook", "Portable computer", "2500.00"), 1);

        String output = captureOutput(() -> handler.updateCartItemQuantity(
                scannerWithInput("99\n0\n"),
                cart
        ));

        assertTrue(output.contains("Product not found in the cart. Enter a listed ID or type 0 to go back."));
        assertFalse(output.contains("New quantity:"));
        assertEquals(1, cart.getItems().get(0).getQuantity());
    }

    @Test
    void shouldPrintMainMenuInTheNewOrder() {
        String output = captureOutput(() -> ConsolePrinter.printMenu(null, new Cart()));

        assertOrdered(output,
                "1 - Register customer",
                "2 - Register product",
                "3 - Remove product from catalog",
                "4 - Search products",
                "5 - List products",
                "6 - Add product to cart",
                "7 - Update cart product quantity",
                "8 - Remove product from cart",
                "9 - View cart",
                "10 - Finalize order",
                "11 - List saved orders",
                "0 - Exit"
        );
    }

    private static void assertOrdered(String output, String... lines) {
        int previousIndex = -1;

        for (String line : lines) {
            int currentIndex = output.indexOf(line);
            assertTrue(currentIndex >= 0, "Expected line was not printed: " + line);
            assertTrue(currentIndex > previousIndex, "Menu line is out of order: " + line);
            previousIndex = currentIndex;
        }
    }

    private static PrintStream originalOut() {
        return System.out;
    }

    private static String captureOutput(Runnable action) {
        PrintStream original = originalOut();
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            action.run();
            return output.toString(StandardCharsets.UTF_8);
        } finally {
            System.setOut(original);
        }
    }

    private static java.util.Scanner scannerWithInput(String input) {
        return new java.util.Scanner(
                new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
                StandardCharsets.UTF_8
        );
    }

    private static Product product(long id, String name, String description, String price) {
        return new Product(id, name, description, new BigDecimal(price));
    }
}
