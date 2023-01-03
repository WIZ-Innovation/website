package com.wiz.wrappers;

public abstract class GenericValue<T> implements ToJson {
    protected final T value;

    protected GenericValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    @Override
    public Object toJsonValue() {
        return value;
    };
}
