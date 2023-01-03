package com.wiz.ignite;

import java.util.Arrays;
import com.wiz.threads.FutureThread;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;

public class CacheFactory {
    private static volatile CacheFactory instance;

    public static CacheFactory getInstance() {
        if (instance == null) {
            synchronized (CacheFactory.class) {
                if (instance == null) {
                    instance = new CacheFactory();
                }
            }
        }

        return instance;
    }

    public Future<CompositeFuture> addCachesToMap() {
        return FutureThread.handle(() -> {
            // Future<IgniteCache<?, ?>> userSessionCache = addCache(CacheNameEnum.USER_SESSION,
            // UserSession.class, new Duration(TimeUnit.DAYS, 2));

            return CompositeFuture.all(Arrays.asList());
        });
    }
}
