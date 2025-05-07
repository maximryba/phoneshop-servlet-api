package com.es.phoneshop.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UriUtil {
    public static Long getEntityId(String pathUri) {
        Pattern pattern = Pattern.compile("/(\\d+)(\\?.*)?$");
        Matcher matcher = pattern.matcher(pathUri);

        if (matcher.find()) {
            return Long.valueOf(matcher.group(1));
        } else {
            throw new IllegalArgumentException("Entity ID not found in the URI: " + pathUri);
        }
    }

    public static String getSecureId(String pathUri) {
        Pattern pattern = Pattern.compile("/([0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12})(\\?.*)?$");
        Matcher matcher = pattern.matcher(pathUri);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            throw new IllegalArgumentException("Entity UUID not found in the URI: " + pathUri);
        }
    }
}
