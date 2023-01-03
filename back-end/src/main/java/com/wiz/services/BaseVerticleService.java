package com.wiz.services;

import java.util.Objects;
import java.util.UUID;
import org.slf4j.MDC;
import com.wiz.enums.EventBusChannelEnum;
import com.wiz.enums.LogEnum;
import com.wiz.exceptions.BusinessException;
import com.wiz.handlers.HandlerFacade;
import com.wiz.logs.LogFactory;
import com.wiz.models.BaseResponse;
import com.wiz.models.Json;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;

public abstract class BaseVerticleService extends AbstractVerticle
        implements AsyncService<Json, JsonObject> {
    private static final LogFactory LOGGER = LogFactory.getLogger(BaseVerticleService.class);

    private final String channelName;

    protected final HandlerFacade handlerFacade;

    protected BaseVerticleService(EventBusChannelEnum eventBusChannelEnum,
            HandlerFacade handlerFacade) {
        this.channelName = eventBusChannelEnum.name();

        this.handlerFacade = handlerFacade;
    }

    @Override
    public void start() throws Exception {
        vertx.eventBus().<Buffer>consumer(channelName, message -> {
            long start = System.currentTimeMillis();

            MDC.put(LogEnum.TX_ID.name(), UUID.randomUUID().toString());
            LOGGER.trace("{} eventbus received request", channelName);

            JsonObject payload = message.body().toJsonObject();

            this.handleRequest(payload).onComplete(ar -> {
                if (ar.succeeded()) {
                    Json rs = ar.result();

                    if (Objects.nonNull(rs)) {
                        message.reply(rs.toBuffer());
                    } else {
                        message.reply(BaseResponse.serverError());
                    }
                } else {
                    Throwable t = ar.cause();

                    message.reply(onError(t));
                }

                LOGGER.info("{} eventbus handle request end. Excute time: {} ms", this.channelName,
                        (System.currentTimeMillis() - start));

                MDC.clear();
            });
        });
    }

    public String getChannelName() {
        return channelName;
    }

    private BaseResponse onError(Throwable t) {
        if (t instanceof BusinessException) {
            BusinessException businessException = (BusinessException) t;

            return businessException.errorResponse();
        } else {
            LOGGER.error(t);

            return BaseResponse.serverError();
        }
    }
}
