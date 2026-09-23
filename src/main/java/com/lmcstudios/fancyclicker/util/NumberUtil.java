package com.lmcstudios.fancyclicker.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public final class NumberUtil {

    private static final DecimalFormat FORMAT = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.US));

    private NumberUtil() { }

    /**
     * Formatiert eine Zahl kompakt.
     *
     * Beispiele:
     *   999           -> "999"
     *   1000          -> "1K"
     *   1500          -> "1.5K"
     *   12500         -> "12.5K"
     *   100000        -> "100K"
     *   1234567       -> "1.23M"
     *   5000000       -> "5M"
     *   1000000000    -> "1B"
     *   1234567890    -> "1.23B"
     *   10^12         -> "1T"
     */
    public static String format(long value) {
        if (value < 0) return "-" + format(-value);
        if (value < 1000) return String.valueOf(value);

        String[] suffixes = {"", "K", "M", "B", "T", "Q"};
        int index = 0;
        double v = value;

        while (v >= 1000 && index < suffixes.length - 1) {
            v /= 1000;
            index++;
        }

        return FORMAT.format(v) + suffixes[index];
    }

    /**
     * Formatiert einen double-Wert (z. B. Geld) mit 2 Nachkommastellen.
     * Wird aktuell nicht für K/M/B genutzt, sondern für exakte Geldwerte.
     */
    public static String formatMoney(double value) {
        return String.format(Locale.US, "%.2f", value);
    }
}
