package com.wiz.threads;

@FunctionalInterface
public interface SupplierThrow<T> {
    T get() throws Throwable;
}
