package com.wiz.configs;

import java.util.function.Consumer;
import com.wiz.logs.LogFactory;
import com.wiz.models.BaseBuilder;
import com.wiz.threads.FutureThread;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class Config {
    private static final LogFactory LOGGER = LogFactory.getLogger(Config.class);

    private final Vertx vertx;
    private final String filePath;
    private final Consumer<JsonObject> consumer;
    private final boolean isRealtimeConfig;
    private ConfigRetriever retriever;

    private Config(Builder builder) {
        this.vertx = builder.vertx;
        this.filePath = builder.filePath;
        this.consumer = builder.consumer;

        this.isRealtimeConfig = builder.isRealtimeConfig;
    }

    public Future<JsonObject> loadConfigFirstTimeAsync() {
        return Future.future(promise -> {
            FutureThread.handleFuture(() -> {
                LOGGER.info("Start load config {}", filePath);
                ConfigStoreOptions configStoreOptions = new ConfigStoreOptions().setType("file")
                        .setConfig(new JsonObject().put("path", filePath));

                ConfigRetrieverOptions options =
                        new ConfigRetrieverOptions().addStore(configStoreOptions);

                retriever = ConfigRetriever.create(vertx, options);

                return retriever.getConfig();
            }).onSuccess(result -> {
                consumer.accept(result);
                promise.complete(result);

                listenConfigChange();
                LOGGER.info("Load {} first time successful!", filePath);
            }).onFailure(t -> {
                promise.fail(t);
                LOGGER.error("Load {} first time failed!", filePath, t);
            });
        });
    }

    private void listenConfigChange() {
        if (!isRealtimeConfig) {
            return;
        }

        retriever.listen(change -> {
            LOGGER.info("Config {} changed!", filePath);
            consumer.accept(change.getNewConfiguration());
        });
    }

    public static Future<JsonObject> loadConfig(Vertx vertx, String filePath,
            Consumer<JsonObject> consumer, boolean isRealtimeConfig) {
        Config config = Builder.builder(vertx, filePath, consumer)
                .isRealtimeConfig(isRealtimeConfig).build();

        return config.loadConfigFirstTimeAsync();
    }

    public static Future<JsonObject> loadConfig(Vertx vertx, String filePath,
            Consumer<JsonObject> consumer) {
        return loadConfig(vertx, filePath, consumer, false);
    }

    public static class Builder implements BaseBuilder<Config> {
        private final Vertx vertx;
        private final String filePath;
        private final Consumer<JsonObject> consumer;

        private boolean isRealtimeConfig = false;

        private Builder(Vertx vertx, String filePath, Consumer<JsonObject> consumer) {
            this.vertx = vertx;
            this.filePath = filePath;
            this.consumer = consumer;
        }

        public static Builder builder(Vertx vertx, String filePath, Consumer<JsonObject> consumer) {
            return new Builder(vertx, filePath, consumer);
        }

        public Builder isRealtimeConfig(boolean isRealtimeConfig) {
            this.isRealtimeConfig = isRealtimeConfig;

            return this;
        }

        @Override
        public Config build() {
            return new Config(this);
        }
    }
}
