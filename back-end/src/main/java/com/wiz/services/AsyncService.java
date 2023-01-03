package com.wiz.services;

import com.wiz.threads.FutureThread;
import io.vertx.core.Future;

public interface AsyncService<R, T> {
    default Future<R> handleRequest(T data) {
        return FutureThread.handle(() -> this.handleRequestInternal(data));
    }

    R handleRequestInternal(T data);
}
