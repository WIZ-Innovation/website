package com.wiz.threads;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import java.util.concurrent.CountDownLatch;
import org.junit.jupiter.api.Test;

public class FutureThreadTest {
    @Test
    void futureThread_sleep_over_30_seconds_throws_InterruptedException() {
        var countDownLatch = new CountDownLatch(1);

        var future = FutureThread.handle(() -> {
            var threadImpl = (ThreadImpl) Thread.currentThread();
            System.out.println(threadImpl.getName());
            System.out.println(threadImpl.getLocalDateTime());
            Thread.sleep(90_000);
            return "a";
        }).onSuccess(rs -> {
            countDownLatch.countDown();
            System.out.println(rs);
        }).onFailure(t -> {
            countDownLatch.countDown();
        });

        try {
            countDownLatch.await();

            var t = future.cause();

            FutureThread.handle(() -> {
                var threadImpl = (ThreadImpl) Thread.currentThread();
                System.out.println(threadImpl.getName());
                System.out.println(threadImpl.getLocalDateTime());
                return "sadad";
            }).onSuccess(rs -> {
                System.out.println(rs);
            }).onFailure(ta -> {
                System.out.println(ta.getMessage());
            });

            assertInstanceOf(InterruptedException.class, t);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    void futureThread_throws_Exception_return_Failure() {
        var countDownLatch = new CountDownLatch(1);

        var future = FutureThread.handle(() -> {
            throw new RuntimeException();
        }).onFailure(t -> {
            countDownLatch.countDown();
        });

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        var expected = RuntimeException.class;
        var actual = future.cause();

        assertInstanceOf(expected, actual);
    }

    @Test
    void futureThread_return_value_return_Failure_null() {
        var countDownLatch = new CountDownLatch(1);

        var future = FutureThread.handle(() -> {
            return "";
        }).onFailure(t -> {
            countDownLatch.countDown();
        }).onSuccess(rs -> {
            countDownLatch.countDown();
        });

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Throwable expected = null;
        var actual = future.cause();

        assertEquals(expected, actual);
    }
}
