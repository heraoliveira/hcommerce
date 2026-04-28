package br.com.heraoliveira.hcommerce.console;

import br.com.heraoliveira.hcommerce.model.Cart;
import br.com.heraoliveira.hcommerce.model.Customer;
import br.com.heraoliveira.hcommerce.service.OrderService;

final class OrderConsoleHandler {

    private final OrderService orderService;

    OrderConsoleHandler(OrderService orderService) {
        this.orderService = orderService;
    }

    boolean finalizeOrder(Customer customer, Cart cart) {
        System.out.println("\n=== Finalize Order ===");

        OrderService.FinalizeOrderResult result = orderService.finalizeOrder(customer, cart);
        if (!result.completed()) {
            System.out.println(messageFor(result.failureReason()));
            return false;
        }

        System.out.println("Order completed successfully.");
        ConsolePrinter.printOrderSummary(result.order());
        return true;
    }

    void printSavedOrders() {
        ConsolePrinter.printSavedOrders(orderService.findSavedOrders());
    }

    private static String messageFor(OrderService.FinalizeOrderFailure failureReason) {
        return switch (failureReason) {
            case MISSING_CUSTOMER -> "Register a customer before finalizing the order.";
            case EMPTY_CART -> "Cart is empty. Add products before finalizing the order.";
        };
    }
}
