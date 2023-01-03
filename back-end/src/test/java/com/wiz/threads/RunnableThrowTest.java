package com.wiz.threads;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class RunnableThrowTest {
    @Test
    void runnableThrow_return_nothing() {
        ProcedureThrow runnableThrow = () -> {

        };

        assertInstanceOf(ProcedureThrow.class, runnableThrow);
    }

    @Test
    void runnableThrow_return_throw_Exception() {
        assertThrows(RuntimeException.class, () -> {
            throw new RuntimeException();
        });
    }
}
