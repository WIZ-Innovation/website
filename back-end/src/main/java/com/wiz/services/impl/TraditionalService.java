package com.wiz.services.impl;

import com.wiz.enums.EventBusChannelEnum;
import com.wiz.handlers.HandlerFacade;
import com.wiz.logs.LogFactory;
import com.wiz.models.BaseResponse;
import com.wiz.models.Json;
import com.wiz.services.BaseVerticleService;
import io.vertx.core.json.JsonObject;

public class TraditionalService extends BaseVerticleService {
    private static final LogFactory LOGGER = LogFactory.getLogger(TraditionalService.class);

    public TraditionalService(EventBusChannelEnum eventBusChannelEnum,
            HandlerFacade handlerFacade) {
        super(eventBusChannelEnum, handlerFacade);
    }

    @Override
    public Json handleRequestInternal(JsonObject data) {
        LOGGER.info("Handle Request");
        return BaseResponse.success(data);
    }
}
