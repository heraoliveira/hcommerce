package br.com.heraoliveira.hcommerce.console;

import br.com.heraoliveira.hcommerce.model.Cart;
import br.com.heraoliveira.hcommerce.model.CartPricing;
import br.com.heraoliveira.hcommerce.model.CartItem;
import br.com.heraoliveira.hcommerce.model.Customer;
import br.com.heraoliveira.hcommerce.model.Order;
import br.com.heraoliveira.hcommerce.model.OrderItem;
import br.com.heraoliveira.hcommerce.model.Product;
import br.com.heraoliveira.hcommerce.util.MoneyFormatter;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public final class ConsolePrinter {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private ConsolePrinter() {
    }

    public static void printMenu(Customer customer, Cart cart) {
        String customerLabel = customer == null ? "no customer registered" : customer.getName();
        int cartItemCount = cart.getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        System.out.println("\n=== Main Menu ===");
        System.out.println("Current customer: " + customerLabel);
        System.out.println("Units in cart: " + cartItemCount);
        System.out.println("1 - Register customer");
        System.out.println("2 - Register product");
        System.out.println("3 - Remove product from catalog");
        System.out.println("4 - Search products");
        System.out.println("5 - List products");
        System.out.println("6 - Add product to cart");
        System.out.println("7 - Update cart product quantity");
        System.out.println("8 - Remove product from cart");
        System.out.println("9 - View cart");
        System.out.println("10 - Finalize order");
        System.out.println("11 - List saved orders");
        System.out.println("0 - Exit");
        System.out.print("Choose an option: ");
    }

    public static void printCustomer(Customer customer) {
        System.out.println("\n=== Current Customer ===");
        System.out.println("Name: " + customer.getName());
        System.out.println("Email: " + customer.getEmail());
        System.out.println("ZIP code: " + customer.getAddress().zip());
        System.out.println("Street: " + customer.getAddress().street());
        System.out.println("Neighborhood: " + customer.getAddress().neighborhood());
        System.out.println("City: " + customer.getAddress().city());
        System.out.println("State: " + customer.getAddress().state());
    }

    public static void printCatalog(List<Product> products) {
        printProductList(products, "\n=== Product Catalog ===", "No products registered.");
    }

    public static void printCart(Cart cart) {
        System.out.println("\n=== Cart ===");

        if (cart.isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }

        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            System.out.printf(
                    "%d - %s | quantity: %d | unit: %s | subtotal: %s%n",
                    product.getId(),
                    product.getName(),
                    item.getQuantity(),
                    MoneyFormatter.format(product.getPrice()),
                    MoneyFormatter.format(item.calculateSubtotal())
            );
        }

        CartPricing.PricingSummary pricingSummary = CartPricing.calculate(cart);

        System.out.println("Subtotal: " + MoneyFormatter.format(pricingSummary.subtotal()));
        System.out.println("Applied discount: " + formatPercentage(pricingSummary.discountPercentage()) + "%");
        System.out.println("Discount amount: " + MoneyFormatter.format(pricingSummary.discountAmount()));
        System.out.println("Total: " + MoneyFormatter.format(pricingSummary.total()));
    }

    public static void printSavedOrders(List<Order> orders) {
        System.out.println("\n=== Saved Orders ===");

        if (orders.isEmpty()) {
            System.out.println("No saved orders.");
            return;
        }

        for (Order order : orders) {
            printOrderSummary(order);
            System.out.println("----------------------------------------");
        }
    }

    public static void printOrderSummary(Order order) {
        System.out.println(formatOrderSummary(order));
    }

    public static void printProductSearchResults(List<Product> products) {
        printProductList(products, "\n=== Search Results ===", "No products matched your search.");
    }

    private static String formatPercentage(java.math.BigDecimal percentage) {
        return percentage.stripTrailingZeros().toPlainString();
    }

    private static void printProductList(List<Product> products, String title, String emptyMessage) {
        System.out.println(title);

        if (products.isEmpty()) {
            System.out.println(emptyMessage);
            return;
        }

        for (Product product : products) {
            System.out.printf(
                    "%d - %s | %s | %s%n",
                    product.getId(),
                    product.getName(),
                    product.getDescription(),
                    MoneyFormatter.format(product.getPrice())
            );
        }
    }

    private static String formatOrderSummary(Order order) {
        String products = order.getItems().stream()
                .map(ConsolePrinter::formatOrderItem)
                .collect(Collectors.joining("\n"));

        return String.format("""
            Order ID: %s
            Customer: %s
            Status: %s
            Date: %s
            Subtotal: %s
            Discount: %s
            Total: %s
            Order items:
            %s
            """,
                order.getId(),
                order.getCustomer().getName(),
                order.getOrderStatus(),
                order.getOrderDate().format(DATE_TIME_FORMATTER),
                MoneyFormatter.format(order.getSubtotal()),
                MoneyFormatter.format(order.getDiscountAmount()),
                MoneyFormatter.format(order.getTotal()),
                products);
    }

    private static String formatOrderItem(OrderItem orderItem) {
        return String.format("- %d x %s", orderItem.getQuantity(), orderItem.getProductName());
    }
}
