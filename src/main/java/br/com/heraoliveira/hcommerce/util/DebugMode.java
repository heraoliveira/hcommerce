package br.com.heraoliveira.hcommerce.util;

public final class DebugMode {
    private static final boolean ENABLED = Boolean.getBoolean("hcommerce.debug")
            || Boolean.parseBoolean(System.getenv().getOrDefault("HCOMMERCE_DEBUG", "false"));

    private DebugMode() {
    }

    public static boolean isEnabled() {
        return ENABLED;
    }
}
