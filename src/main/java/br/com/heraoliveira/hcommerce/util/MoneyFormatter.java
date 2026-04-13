package br.com.heraoliveira.hcommerce.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public final class MoneyFormatter {

    private static final Locale BRAZILIAN_LOCALE = Locale.forLanguageTag("pt-BR");
    private static final DecimalFormatSymbols BRAZILIAN_SYMBOLS =
            DecimalFormatSymbols.getInstance(BRAZILIAN_LOCALE);
    private static final String PATTERN = "R$ #,##0.00";

    private MoneyFormatter() {
    }

    public static String format(BigDecimal value) {
        DecimalFormat currencyFormat = new DecimalFormat(PATTERN, BRAZILIAN_SYMBOLS);
        return currencyFormat.format(value);
    }
}
