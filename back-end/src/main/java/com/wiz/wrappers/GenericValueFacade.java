package com.wiz.wrappers;

import com.wiz.wrappers.impl.ObjectValue;

public class GenericValueFacade {
    private GenericValueFacade() {};

    @SuppressWarnings("unchecked")
    private static <V extends GenericValue<T>, T> V of(T convertValue) {

        return (V) new ObjectValue(convertValue);
    }

    public static <T> Object toJsonValue(T convertValue) {
        return of(convertValue).toJsonValue();
    }
}
