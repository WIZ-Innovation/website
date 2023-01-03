package com.wiz.services;

import com.wiz.logs.LogFactory;
import io.vertx.core.Vertx;

public class VertxService {
    private static final LogFactory LOGGER = LogFactory.getLogger(VertxService.class);

    private static volatile VertxService instance;

    public static VertxService getInstance() {
        if (instance == null) {
            synchronized (VertxService.class) {
                if (instance == null) {
                    instance = new VertxService();
                }
            }
        }

        return instance;
    }

    private final Vertx vertx;

    private VertxService() {
        LOGGER.info("Starting {}.", this.getClass().getSimpleName());
        this.vertx = Vertx.vertx();
        LOGGER.info("Starting {} successful.", this.getClass().getSimpleName());
    }

    public Vertx getVertxInstance() {
        return vertx;
    }
}
