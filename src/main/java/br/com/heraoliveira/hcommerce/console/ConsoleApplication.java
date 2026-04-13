package br.com.heraoliveira.hcommerce.console;

import br.com.heraoliveira.hcommerce.exception.ExternalServiceException;
import br.com.heraoliveira.hcommerce.exception.InvalidCartException;
import br.com.heraoliveira.hcommerce.exception.InvalidCepException;
import br.com.heraoliveira.hcommerce.exception.InvalidDataException;
import br.com.heraoliveira.hcommerce.exception.ProductNotFoundException;
import br.com.heraoliveira.hcommerce.exception.ProductRemovalNotAllowedException;
import br.com.heraoliveira.hcommerce.infra.HttpClientFetcher;
import br.com.heraoliveira.hcommerce.models.Cart;
import br.com.heraoliveira.hcommerce.models.Customer;
import br.com.heraoliveira.hcommerce.repository.CurrentCustomerRepository;
import br.com.heraoliveira.hcommerce.repository.OrderRepository;
import br.com.heraoliveira.hcommerce.repository.ProductRepository;
import br.com.heraoliveira.hcommerce.service.AddressLookupService;
import br.com.heraoliveira.hcommerce.service.CustomerService;
import br.com.heraoliveira.hcommerce.service.OrderService;
import br.com.heraoliveira.hcommerce.service.ProductService;
import br.com.heraoliveira.hcommerce.service.ViaCepService;
import br.com.heraoliveira.hcommerce.util.DebugMode;

import java.util.Objects;
import java.util.Scanner;

public class ConsoleApplication {
    private final ProductConsoleHandler productConsoleHandler;
    private final CustomerConsoleHandler customerConsoleHandler;
    private final CartConsoleHandler cartConsoleHandler;
    private final OrderConsoleHandler orderConsoleHandler;
    private Cart cart;
    private Customer customer;

    public ConsoleApplication(
            ProductRepository productRepository,
            OrderRepository orderRepository
    ) {
        this(
                new ProductService(productRepository),
                new CustomerService(
                        new CurrentCustomerRepository(),
                        new ViaCepService(new HttpClientFetcher())
                ),
                new OrderService(orderRepository)
        );
    }

    public ConsoleApplication(
            ProductRepository productRepository,
            OrderRepository orderRepository,
            AddressLookupService addressLookupService
    ) {
        this(
                new ProductService(productRepository),
                new CustomerService(new CurrentCustomerRepository(), addressLookupService),
                new OrderService(orderRepository)
        );
    }

    public ConsoleApplication(
            ProductRepository productRepository,
            OrderRepository orderRepository,
            CurrentCustomerRepository currentCustomerRepository
    ) {
        this(
                new ProductService(productRepository),
                new CustomerService(currentCustomerRepository, new ViaCepService(new HttpClientFetcher())),
                new OrderService(orderRepository)
        );
    }

    public ConsoleApplication(
            ProductRepository productRepository,
            OrderRepository orderRepository,
            CurrentCustomerRepository currentCustomerRepository,
            AddressLookupService addressLookupService
    ) {
        this(
                new ProductService(productRepository),
                new CustomerService(currentCustomerRepository, addressLookupService),
                new OrderService(orderRepository)
        );
    }

    ConsoleApplication(
            ProductService productService,
            CustomerService customerService,
            OrderService orderService
    ) {
        this.productConsoleHandler = new ProductConsoleHandler(
                Objects.requireNonNull(productService, "Product service cannot be null.")
        );
        this.customerConsoleHandler = new CustomerConsoleHandler(
                Objects.requireNonNull(customerService, "Customer service cannot be null.")
        );
        this.cartConsoleHandler = new CartConsoleHandler();
        this.orderConsoleHandler = new OrderConsoleHandler(
                Objects.requireNonNull(orderService, "Order service cannot be null.")
        );
        this.cart = new Cart();
        this.customer = customerService.findCurrentCustomer().orElse(null);
    }

    public void run() {
        System.out.println("=== HCommerce ===");
        System.out.println("Welcome to HCommerce.");

        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;

            while (running) {
                ConsolePrinter.printMenu(customer, cart);
                String option = scanner.nextLine().strip();

                try {
                    running = handleOption(option, scanner);
                } catch (InvalidCepException e) {
                    System.out.println("[ZIP ERROR] " + e.getMessage());
                } catch (InvalidDataException e) {
                    System.out.println("[VALIDATION ERROR] " + e.getMessage());
                } catch (InvalidCartException e) {
                    System.out.println("[CART ERROR] " + e.getMessage());
                } catch (ProductRemovalNotAllowedException e) {
                    System.out.println("[PRODUCT ERROR] " + e.getMessage());
                } catch (ProductNotFoundException e) {
                    System.out.println("[PRODUCT ERROR] " + e.getMessage());
                } catch (ExternalServiceException e) {
                    System.out.println("[INTEGRATION ERROR] " + e.getMessage());
                } catch (IllegalStateException e) {
                    System.out.println("[STATE ERROR] " + e.getMessage());
                } catch (RuntimeException e) {
                    System.out.println("[UNEXPECTED ERROR] Something unexpected happened. Returning to the main menu.");
                    if (DebugMode.isEnabled()) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private boolean handleOption(String option, Scanner scanner) {
        switch (option) {
            case "1" -> {
                Customer registeredCustomer = customerConsoleHandler.registerCustomer(scanner);
                customer = registeredCustomer;
                ConsolePrinter.printCustomer(registeredCustomer);
            }
            case "2" -> productConsoleHandler.registerProduct(scanner);
            case "3" -> productConsoleHandler.printCatalog();
            case "4" -> productConsoleHandler.addProductToCart(scanner, cart);
            case "5" -> cartConsoleHandler.updateCartItemQuantity(scanner, cart);
            case "6" -> cartConsoleHandler.removeCartItem(scanner, cart);
            case "7" -> ConsolePrinter.printCart(cart);
            case "8" -> orderConsoleHandler.printSavedOrders();
            case "9" -> {
                if (orderConsoleHandler.finalizeOrder(customer, cart)) {
                    cart = new Cart();
                }
            }
            case "10" -> productConsoleHandler.searchProducts(scanner);
            case "11" -> productConsoleHandler.removeProduct(scanner, cart);
            case "0" -> {
                System.out.println("\nShutting down. See you later!");
                return false;
            }
            default -> System.out.println("\nInvalid option. Choose an option from the menu.");
        }

        return true;
    }
}
