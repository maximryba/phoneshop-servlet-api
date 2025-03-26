package com.es.phoneshop.utils;

public class UriUtil {
    public static Long getProductId(String pathUri) {
        String[] parts = pathUri.split("/");
        return Long.valueOf(parts[parts.length - 1]);
    }
}
