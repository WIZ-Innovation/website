package com.wiz.handlers;

import com.wiz.exceptions.BadRequestException;
import com.wiz.models.BaseResponse;
import io.vertx.ext.web.RoutingContext;

public interface ExceptionRequestHandler {
    default void onError(RoutingContext ctx, Throwable t) {
        if (t instanceof BadRequestException) {
            BaseResponse.badRequest(ctx);
        } else {
            BaseResponse.serverError(ctx, t);
        }
    }
}
