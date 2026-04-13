package br.com.heraoliveira.hcommerce.util;

import br.com.heraoliveira.hcommerce.exception.InvalidCepException;

public final class ZipValidation {
    private static final String INPUT_REGEX = "^(\\d{8}|\\d{5}-\\d{3})$";
    private static final String NORMALIZED_REGEX = "^\\d{8}$";

    private ZipValidation() {
    }

    public static boolean isValidInput(String zip) {
        return zip != null && !zip.isBlank() && zip.strip().matches(INPUT_REGEX);
    }

    public static String normalize(String zip) {
        if (!isValidInput(zip)) {
            throw new InvalidCepException("Invalid ZIP code format. Use XXXXXXXX or XXXXX-XXX.");
        }
        return zip.strip().replace("-", "");
    }

    public static boolean isNormalized(String zip) {
        return zip != null && !zip.isBlank() && zip.strip().matches(NORMALIZED_REGEX);
    }
}