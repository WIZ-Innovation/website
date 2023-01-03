package com.wiz.utils;

public class StringUtils {
    private StringUtils() {}

    public static String camelCaseToPascalCase(String value) {
        String firstChar = value.substring(0, 1).toUpperCase();

        return firstChar + value.substring(1);
    };
}
