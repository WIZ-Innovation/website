package com.wiz.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Objects;
import com.wiz.logs.LogFactory;

public class CommonUtils {
    private static final LogFactory LOGGER = LogFactory.getLogger(CommonUtils.class);

    private CommonUtils() {}

    public static Object getValueGetter(Class<?> modelClass, Object instance, String fieldName) {
        String getterMethodName = CommonUtils.getterMethodName(fieldName);

        Object value = null;
        try {
            value = modelClass.getMethod(getterMethodName).invoke(instance);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            LOGGER.error(e);
        }
        return value;
    }

    private static String getterMethodName(String fieldName) {
        return "get" + StringUtils.camelCaseToPascalCase(fieldName);
    }

    public static <T> T getOrDefault(T value, T defaultValue) {
        return Objects.nonNull(value) ? value : defaultValue;
    }

    public static boolean hasLength(Collection<?> value) {
        return Objects.nonNull(value) && !value.isEmpty();
    }

    public static String setterMethodName(String fieldName) {
        return "set" + StringUtils.camelCaseToPascalCase(fieldName);
    }
}
