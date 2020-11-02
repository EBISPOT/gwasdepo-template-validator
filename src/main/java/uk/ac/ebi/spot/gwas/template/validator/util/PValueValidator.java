package uk.ac.ebi.spot.gwas.template.validator.util;

public class PValueValidator {

    public static boolean validate(String value, Double lower, Double upper) {
        value = value.toLowerCase();
        if (!value.contains("e")) {
            return false;
        }

        String[] parts = value.split("e");
        String exponent = parts[1].trim();

        try {
            int exp = Math.abs(Integer.parseInt(exponent));
            if (lower != null) {
                if (exp < Math.abs(lower.doubleValue())) {
                    return false;
                }
            }
            if (upper != null) {
                if (exp > Math.abs(upper.doubleValue())) {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}
