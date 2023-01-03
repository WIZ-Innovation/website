package com.wiz.threads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import io.vertx.core.Future;
import io.vertx.core.Promise;

public class FutureThread {
    private static final ExecutorService executorService =
            Executors.newCachedThreadPool(new ThreadFactoryImpl());

    public static <R> Future<R> handleFuture(SupplierThrow<Future<R>> supplier) {
        return Future.future(promise -> {
            try {
                hanldeFutureInternal(promise, supplier);
            } catch (InterruptedException e) {
                promise.fail(e);
            }
        });
    }


    private static <R> void hanldeFutureInternal(Promise<R> promise,
            SupplierThrow<Future<R>> supplier) throws InterruptedException {
        executorService.submit(new RunnableImpl() {
            @Override
            public void runInternal() {
                try {
                    Future<R> future = supplier.get();

                    future.onSuccess(rs -> {
                        promise.complete(rs);
                    }).onFailure(t -> {
                        promise.fail(t);
                    });
                } catch (Throwable e) {
                    promise.fail(e);
                }
            }
        });
    }


    public static <R> Future<R> handle(SupplierThrow<R> supplier) {
        return Future.future(promise -> {
            try {
                hanldeInternal(promise, supplier);
            } catch (InterruptedException e) {
                promise.fail(e);
            }
        });
    }

    private static <R> void hanldeInternal(Promise<R> promise, SupplierThrow<R> supplier)
            throws InterruptedException {
        executorService.submit(new RunnableImpl() {
            @Override
            public void runInternal() {
                try {
                    R result = supplier.get();
                    promise.complete(result);
                } catch (Throwable e) {
                    promise.fail(e);
                }
            }
        });
    }

    public static Future<Void> handle(ProcedureThrow procedure) {
        return Future.future(promise -> {
            try {
                hanldeInternal(promise, procedure);
            } catch (InterruptedException e) {
                promise.fail(e);
            }
        });
    }

    private static void hanldeInternal(Promise<?> promise, ProcedureThrow procedure)
            throws InterruptedException {
        executorService.submit(new RunnableImpl() {
            @Override
            public void runInternal() {
                try {
                    procedure.run();
                    promise.complete();
                } catch (Throwable e) {
                    promise.fail(e);
                }
            }
        });
    }
}
