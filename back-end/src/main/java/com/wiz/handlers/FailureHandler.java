package com.wiz.handlers;

import com.wiz.exceptions.BusinessException;
import com.wiz.models.BaseResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.DecodeException;
import io.vertx.ext.web.RoutingContext;

public class FailureHandler implements Handler<RoutingContext> {
    @Override
    public void handle(RoutingContext ctx) {
        Throwable throwable = ctx.failure();
        if (HttpResponseStatus.REQUEST_TIMEOUT.code() == ctx.statusCode()) {
            BaseResponse.requestTimeout(ctx);
        } else {
            if (throwable instanceof BusinessException) {
                BusinessException businessException = (BusinessException) throwable;

                BaseResponse.success(ctx, businessException.errorResponse());
            } else if (throwable instanceof DecodeException) {
                BaseResponse.badRequest(ctx);
            } else {
                BaseResponse.serverError(ctx, throwable);
            }
        }
    }

    public static FailureHandler create() {
        return new FailureHandler();
    }

}
