package uk.ac.ebi.spot.gwas.template.validator.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class ValidationUtil {

    public static String rangeMess(String lowerBound, String upperBound) {
        String mess = "";
        if (lowerBound != null) {
            mess = lowerBound;
        }
        mess += " - ";
        if (upperBound != null) {
            mess += upperBound;
        }
        return mess.trim();
    }

    public static String trimSpaces(String s) {
        if (s == null) {
            return s;
        }
        String result = s.trim();
        result = result.replaceAll("\\u00A0", "");
        result = result.replaceAll("\\u2007", "");
        result = result.replaceAll("\\u202F", "");
        return result;
    }

    public static String trimZeros(String s) {
        return s.contains(".") ? s.replaceAll("0*$", "").replaceAll("\\.$", "") : s;
    }

    public static List<Integer> convert(List<String> list) {
        List<Integer> result = new ArrayList<>();
        for (String s : list) {
            result.add(Integer.parseInt(s));
        }
        return result;
    }

    public static List<String> compress(List<Integer> list) {
        List<String> result = new ArrayList<>();
        if (list == null) {
            return result;
        }
        if (list.isEmpty()) {
            return result;
        }

        int start = list.get(0);
        int last = list.get(0);

        for (int i = 1; i < list.size(); i++) {
            int current = list.get(i);
            if (current == last + 1) {
                last = current;
            } else {
                if (start == last) {
                    result.add(Integer.toString(start));
                } else {
                    result.add(start + "-" + last);
                }
                start = current;
                last = current;
            }
        }
        if (start == last) {
            result.add(Integer.toString(start));
        } else {
            result.add(start + "-" + last);
        }
        return result;
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        String s = "aaa";
        String yet = "|";
        String separator = yet;
        separator = "\\" + separator;
        String[] parts = s.split(separator);
        System.out.println(parts.length);
    }

}
