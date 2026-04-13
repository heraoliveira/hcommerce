package br.com.heraoliveira.hcommerce.console;

import br.com.heraoliveira.hcommerce.models.Cart;
import br.com.heraoliveira.hcommerce.models.Customer;
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
            System.out.println(result.message());
            return false;
        }

        System.out.println("Order completed successfully.");
        ConsolePrinter.printOrderSummary(result.order());
        return true;
    }

    void printSavedOrders() {
        ConsolePrinter.printSavedOrders(orderService.findSavedOrders());
    }
}
