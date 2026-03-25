package br.com.heraoliveira.hcommerce.util;

public class EmailValidation {
    private static final String EMAIL_REGEX = "^[\\w+.-]+@([\\w-]+\\.)+[a-zA-Z]{2,}$";

    private EmailValidation() {
    }

    public static boolean isValid(String email) {
        return email != null && !email.isBlank() && email.matches(EMAIL_REGEX) ;
    }
}