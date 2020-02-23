package uk.ac.ebi.spot.gwas.template.validator.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ValidationUtil {

    public static String trimZeros(String s) {
        return s.contains(".") ? s.replaceAll("0*$", "").replaceAll("\\.$", "") : s;
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
