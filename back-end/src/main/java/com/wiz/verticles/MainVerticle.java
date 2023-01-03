package com.wiz.verticles;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import com.wiz.App;
import com.wiz.configs.MainConfig;
import com.wiz.enums.EventBusChannelEnum;
import com.wiz.exceptions.BadRequestException;
import com.wiz.handlers.ExceptionRequestHandler;
import com.wiz.handlers.FailureHandler;
import com.wiz.handlers.LoggerHandlerImpl;
import com.wiz.logs.LogFactory;
import com.wiz.models.BaseResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RequestBody;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.TimeoutHandler;
import io.vertx.micrometer.PrometheusScrapingHandler;

public class MainVerticle extends AbstractVerticle implements ExceptionRequestHandler {
    private static final LogFactory LOGGER = LogFactory.getLogger(MainVerticle.class);

    private final MainConfig mainConfig;

    public MainVerticle() {
        this.mainConfig = App.getMainConfig();
    }

    private EventBus eventBus;

    @Override
    public void start() throws Exception {
        this.eventBus = vertx.eventBus();

        this.createHttpServer();
    }

    private void createHttpServer() {
        var server = vertx.createHttpServer();

        Router router = Router.router(vertx);

        router.route()
                .handler(TimeoutHandler.create(30000, HttpResponseStatus.REQUEST_TIMEOUT.code()))
                .handler(BodyHandler.create()).failureHandler(FailureHandler.create())
                .handler(LoggerHandlerImpl.create());

        router.route("/metrics").handler(PrometheusScrapingHandler.create());
        router.post("/TraditionalService")
                .handler(ctx -> handleEventBus(ctx, EventBusChannelEnum.TRADITIONAL_SERVICE));
        server.requestHandler(router).listen(mainConfig.getHost());

        LOGGER.info("Start Vertx Server successful at port {}", mainConfig.getHost());
    }

    public void handleEventBus(RoutingContext ctx, EventBusChannelEnum eventBusChannelEnum) {
        try {
            RequestBody requestBody = ctx.body();

            if (requestBody != null) {
                JsonObject payload = requestBody.asJsonObject();

                if (Objects.nonNull(payload)) {
                    eventBus.<Buffer>request(eventBusChannelEnum.name(), payload.toBuffer(), rs -> {
                        if (rs.succeeded()) {
                            try {
                                BaseResponse.success(ctx, rs.result().body().toJson());
                            } catch (Throwable t) {
                                this.onError(ctx, t);
                            }
                        }
                    });
                } else {
                    throw new BadRequestException();
                }
            } else {
                throw new BadRequestException();
            }
        } catch (Throwable t) {
            this.onError(ctx, t);
        }
    }

    void addCors(Router router) {
        Set<String> allowHeaders = new HashSet<>() {
            {
                add(HttpHeaderNames.X_REQUESTED_WITH.toString());
                add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN.toString());
                add(HttpHeaderNames.ORIGIN.toString());
                add(HttpHeaderNames.CONTENT_TYPE.toString());
                add(HttpHeaderNames.ACCEPT.toString());
            }
        };

        Set<HttpMethod> allowMethods = new HashSet<>() {
            {
                add(HttpMethod.GET);
                add(HttpMethod.POST);
                add(HttpMethod.DELETE);
                add(HttpMethod.PUT);
                add(HttpMethod.OPTIONS);
                add(HttpMethod.HEAD);
            }
        };

        router.route("/*").handler(StaticHandler.create());

        router.route().handler(
                CorsHandler.create("*").allowedHeaders(allowHeaders).allowedMethods(allowMethods));
    }
}
