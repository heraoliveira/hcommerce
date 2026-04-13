package br.com.heraoliveira.hcommerce.console;

import br.com.heraoliveira.hcommerce.util.EmailValidation;
import br.com.heraoliveira.hcommerce.util.ZipValidation;

import java.math.BigDecimal;
import java.util.Scanner;

public final class ConsoleInput {

    private ConsoleInput() {
    }

    public static String readRequiredText(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine();

            if (value != null && !value.isBlank()) {
                return value.strip();
            }

            System.out.println("Invalid input. Please enter a non-empty value.");
        }
    }

    public static long readLong(Scanner scanner, String prompt) {
        while (true) {
            String value = readRequiredText(scanner, prompt);

            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
            }
        }
    }

    public static int readInt(Scanner scanner, String prompt) {
        while (true) {
            String value = readRequiredText(scanner, prompt);

            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
            }
        }
    }

    public static int readPositiveInt(Scanner scanner, String prompt) {
        while (true) {
            int value = readInt(scanner, prompt);

            if (value > 0) {
                return value;
            }

            System.out.println("Invalid input. Please enter an integer greater than zero.");
        }
    }

    public static BigDecimal readBigDecimal(Scanner scanner, String prompt) {
        while (true) {
            String value = readRequiredText(scanner, prompt).replace(',', '.');

            try {
                return new BigDecimal(value);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid numeric value.");
            }
        }
    }

    public static String readValidEmail(Scanner scanner, String prompt) {
        while (true) {
            String value = readRequiredText(scanner, prompt);

            if (EmailValidation.isValid(value)) {
                return value.strip();
            }

            System.out.println("Invalid email address. Please enter a valid email.");
        }
    }

    public static String readZipCode(Scanner scanner, String prompt) {
        while (true) {
            String value = readRequiredText(scanner, prompt);

            if (ZipValidation.isValidInput(value)) {
                return value.strip();
            }

            System.out.println("Invalid ZIP code format. Use XXXXXXXX or XXXXX-XXX.");
        }
    }
}
