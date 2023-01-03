package com.wiz;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import com.wiz.configs.Config;
import com.wiz.configs.MainConfig;
import com.wiz.constants.ConfigContants;
import com.wiz.enums.EventBusChannelEnum;
import com.wiz.handlers.HandlerFacade;
import com.wiz.ignite.IgniteService;
import com.wiz.logs.LogFactory;
import com.wiz.services.BaseVerticleService;
import com.wiz.services.VertxService;
import com.wiz.services.impl.TraditionalService;
import com.wiz.threads.FutureThread;
import com.wiz.utils.JsonUtils;
import com.wiz.verticles.MainVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;

public class App {
    private static final LogFactory LOGGER = LogFactory.getLogger(App.class);

    private static final String MAIN_CONFIG_FILE = "main.json";

    private static Vertx VERTX;

    private static MainConfig mainConfig;

    public static void main(String[] args) {
        // Set log delegate for vertx
        System.setProperty("vertx.logger-delegate-factory-class-name",
                "io.vertx.core.logging.Log4j2LogDelegateFactory");

        // set value for async logging lmax
        System.setProperty("Log4jContextSelector",
                "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");

        // Fix warning

        VERTX = VertxService.getInstance().getVertxInstance();

        Config.loadConfig(VERTX, ConfigContants.CONFIG_FOLDER + File.separator + MAIN_CONFIG_FILE,
                json -> {
                    App.mainConfig = JsonUtils.map(json, MainConfig.class);
                }).onSuccess(mainConfig -> {
                    long start = System.currentTimeMillis();
                    startIgnite(VERTX).onSuccess(rs -> {
                        HandlerFacade handlerFacade = HandlerFacade.getInstance();

                        deployWorkersAsync(handlerFacade);

                        deployVerticleAsync(MainVerticle.class);
                        LOGGER.info("DEPLOY VERTICLE take {} ms.",
                                System.currentTimeMillis() - start);
                    }).onFailure(t -> {
                        LOGGER.error(t);
                        System.exit(-1);
                    });
        }).onFailure(t -> {
            LOGGER.error(t);
            System.exit(-1);
        });
    }

    private static void deployVerticleAsync(Class<? extends Verticle> clazz) {
        FutureThread.handle(() -> {
            VERTX.deployVerticle(clazz, new DeploymentOptions().setInstances(1)).onSuccess(id -> {
                LOGGER.info("Success deploy {}, id: {}", clazz.getSimpleName(), id);
            }).onFailure(t -> {
                LOGGER.error("Deploy {} failed!", clazz.getSimpleName(), t);
            });
        });
    }

    @SuppressWarnings("rawtypes")
    private static Future<Void> deployWorkersAsync(HandlerFacade handlerFacade) {
        return FutureThread.handle(() -> {
            var deployWorkers = new ArrayList<Future>();
            deployWorkers.add(deployWorkerAsync(new TraditionalService(
                    EventBusChannelEnum.TRADITIONAL_SERVICE, handlerFacade)));

            CompositeFuture.all(deployWorkers).onSuccess(rs -> {
                LOGGER.info("Start all workers success!");
            }).onFailure(t -> {
                LOGGER.error("Start all workers failed!");
            });
        });
    }

    private static Future<Void> deployWorkerAsync(BaseVerticleService verticleService) {
        return FutureThread.handle(() -> {
            var channelEventBus = verticleService.getChannelName();

            VERTX.deployVerticle(verticleService, new DeploymentOptions().setWorker(true)
                    .setWorkerPoolSize(5).setWorkerPoolName(channelEventBus)
                    .setMaxWorkerExecuteTimeUnit(TimeUnit.SECONDS).setMaxWorkerExecuteTime(30))
                    .onSuccess(rs -> {
                        LOGGER.info("Deploy {} with id {} success!", channelEventBus, rs);
                    }).onFailure(t -> {
                        LOGGER.error("Deploy {} failed!", channelEventBus);
                    });
        });
    }

    private static Future<Void> startIgnite(Vertx vertx) {
        return FutureThread.handle(() -> {
            var igniteService = IgniteService.getInstance();

            igniteService.initServiceBlocking();

            return igniteService;
        }).compose(rs -> rs.initilizeCacheAsync());
    }

    public static MainConfig getMainConfig() {
        return mainConfig;
    }
}
