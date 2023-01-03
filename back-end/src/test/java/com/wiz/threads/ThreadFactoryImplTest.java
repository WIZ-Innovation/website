package com.wiz.threads;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import org.junit.jupiter.api.Test;

public class ThreadFactoryImplTest {
    private static final ThreadFactoryImpl THREAD_FACTORY_IMPL = new ThreadFactoryImpl();

    @Test
    void newThread_null_runnable_return_thread() {
        Thread thread = THREAD_FACTORY_IMPL.newThread(null);

        assertInstanceOf(ThreadImpl.class, thread);
    }
}
