package com.wiz.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import com.wiz.logs.LogFactory;
import com.wiz.models.ModelMapper;
import com.wiz.wrappers.GenericValueFacade;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class JsonUtils {
    private static final LogFactory LOGGER = LogFactory.getLogger(JsonUtils.class);

    private JsonUtils() {}

    @SuppressWarnings("unchecked")
    public static <M extends ModelMapper> JsonObject toJsonObject(M modelMapper) {
        JsonObject jsonObject = new JsonObject();

        Class<?> modelClass = modelMapper.getClass();
        try {
            M model = (M) modelClass.getDeclaredConstructor().newInstance();

            model.getMap().forEach((dbField, field) -> {
                Object value = CommonUtils.getValueGetter(modelClass, modelMapper, field);

                if (Objects.nonNull(value)) {
                    jsonObject.put(dbField, GenericValueFacade.toJsonValue(value));
                }
            });
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            LOGGER.error(e);

        }

        return jsonObject;
    }

    public static <T extends ModelMapper> T map(JsonObject jsonObject, Class<T> modelClass) {
        try {
            Constructor<T> constructor = modelClass.getConstructor();
            T instance = constructor.newInstance();

            instance.getMap().forEach((jsonField, fieldName) -> {
                Field field;
                try {
                    field = modelClass.getDeclaredField(fieldName);

                    if (Objects.nonNull(field)) {
                        Class<?> fieldType = field.getType();

                        String setterMethod = CommonUtils.setterMethodName(fieldName);
                        Object value = getValueByType(jsonObject, jsonField, fieldType);

                        modelClass.getMethod(setterMethod, fieldType).invoke(instance, value);
                    }
                } catch (NoSuchFieldException | SecurityException | IllegalAccessException
                        | IllegalArgumentException | InvocationTargetException
                        | NoSuchMethodException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            });

            return instance;
        } catch (NoSuchMethodException | SecurityException | InstantiationException
                | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return null;
    }

    private static Object getValueByType(JsonObject jsonObject, String jsonField,
            Class<?> fieldType) {
        if (Objects.isNull(jsonObject)) {
            return null;
        }

        switch (fieldType.getSimpleName()) {
            case "String":
                return jsonObject.getString(jsonField);
            case "Double":
                try {
                    return jsonObject.getDouble(jsonField);
                } catch (ClassCastException e) {
                    return Double.parseDouble(jsonObject.getString(jsonField));
                }
            case "List":
                JsonArray jsonArray = jsonObject.getJsonArray(jsonField);
                if (Objects.nonNull(jsonArray)) {
                    return jsonArray.getList();
                }

                return null;
            case "Integer":
                return jsonObject.getInteger(jsonField);
            default:
                LOGGER.error("fieldType: {}", fieldType);
                return jsonObject.getValue(jsonField);
        }
    }

}
