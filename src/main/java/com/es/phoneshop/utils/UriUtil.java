package com.es.phoneshop.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UriUtil {
    public static Long getProductId(String pathUri) {
        Pattern pattern = Pattern.compile("/(\\d+)(\\?.*)?$");
        Matcher matcher = pattern.matcher(pathUri);

        if (matcher.find()) {
            return Long.valueOf(matcher.group(1));
        } else {
            throw new IllegalArgumentException("Product ID not found in the URI: " + pathUri);
        }
    }
}
