package com.wiz.threads;

@FunctionalInterface
public interface ProcedureThrow {
    void run() throws Throwable;
}
