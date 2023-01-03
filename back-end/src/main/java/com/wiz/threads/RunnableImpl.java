package com.wiz.threads;

public interface RunnableImpl extends Runnable {
    @Override
    default void run() {
        ThreadImpl threadImpl = (ThreadImpl) Thread.currentThread();
        threadImpl.setLocalDateTime();

        runInternal();
    }

    void runInternal();
}
