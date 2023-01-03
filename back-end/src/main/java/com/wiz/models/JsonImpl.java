package com.wiz.models;

public abstract class JsonImpl implements Json {
    @Override
    public String toString() {
        return this.toJson().toString();
    }
}
