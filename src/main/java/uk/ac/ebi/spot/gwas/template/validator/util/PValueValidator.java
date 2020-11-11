package uk.ac.ebi.spot.gwas.template.validator.util;

import org.apache.commons.lang3.tuple.Pair;

public class PValueValidator {

    public static boolean validate(String value, String lower, String upper) {
        value = value.toLowerCase();
        if (!value.contains("e")) {
            return false;
        }

        Pair<Double, Integer> lowerPair = extractPair(lower);
        Pair<Double, Integer> upperPair = extractPair(upper);

        String[] parts = value.split("e");
        String mantissa = parts[0].trim();
        String exponent = parts[1].trim();

        try {
            int exp = Math.abs(Integer.parseInt(exponent));
            double mant = Double.parseDouble(mantissa);

            if (lowerPair != null) {
                if (exp < Math.abs(lowerPair.getRight())) {
                    return false;
                }
                if (exp == Math.abs(lowerPair.getRight())) {
                    if (mant >= lowerPair.getLeft()) {
                        return false;
                    }
                }
            }
            if (upperPair != null) {
                if (exp > Math.abs(upperPair.getRight())) {
                    return false;
                }
                if (exp == Math.abs(upperPair.getRight())) {
                    if (mant <= upperPair.getLeft()) {
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public static Pair<Double, Integer> extractPair(String value) {
        if (value == null) {
            return null;
        }

        String[] parts = value.split("\\|");
        return Pair.of(Double.parseDouble(parts[0].trim()), Integer.parseInt(parts[1].trim()));
    }

    public static String formatBound(String bound) {
        if (bound == null) {
            return null;
        }
        Pair<Double, Integer> pair = extractPair(bound);
        return Double.toString(pair.getLeft()) + "e" + Integer.toString(pair.getRight());
    }
}
