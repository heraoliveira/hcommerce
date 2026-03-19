package br.com.heraoliveira.hcommerce.util;

public class ZipValidation {
    private static final String ZIP_REGEX = "^\\d{5}-?\\d{3}$";

    private ZipValidation() {
    }

    public static boolean isValid(String zip) {
        return zip != null && !zip.isBlank() && zip.matches(ZIP_REGEX);
    }
}