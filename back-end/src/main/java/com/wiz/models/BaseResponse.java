package com.wiz.models;

import java.util.HashMap;
import org.slf4j.MDC;
import com.wiz.enums.LogEnum;
import com.wiz.logs.LogFactory;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

public class BaseResponse extends ModelMapperImpl {
    private static final LogFactory LOGGER = LogFactory.getLogger(BaseResponse.class);

    private int code;
    private String message;
    private String txId;
    private Object data;

    public BaseResponse(int code, String message, Object data) {
        this();

        this.code = code;
        this.message = message;
        this.data = data;
    }

    public BaseResponse() {
        super(new HashMap<String, String>() {
            {
                put("code", "code");
                put("message", "message");
                put("txId", "txId");
                put("data", "data");
            }
        });

        this.txId = MDC.get(LogEnum.TX_ID.name());
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public static BaseResponse serverError() {
        return fail(HttpResponseStatus.INTERNAL_SERVER_ERROR);
    }

    public static void success(RoutingContext ctx, Object data) {
        LOGGER.info("Response: {}", data);

        setResponseStatus(ctx, HttpResponseStatus.OK).end(data.toString());
    }

    public static BaseResponse success(Object data) {
        return new BaseResponse(1, "", data);
    }

    public static BaseResponse fail(HttpResponseStatus httpResponseStatus) {
        return new BaseResponse(httpResponseStatus.code(), httpResponseStatus.reasonPhrase(), null);
    }

    public static BaseResponse fail(int code, String message) {
        return new BaseResponse(code, message, null);
    }

    public static BaseResponse fail(String message, Object data) {
        return new BaseResponse(-1, message, data);
    }

    public static void fail(RoutingContext ctx, String message) {
        setResponseStatus(ctx, HttpResponseStatus.OK).end(message);
    }

    public static void badRequest(RoutingContext ctx) {
        setResponseStatus(ctx, HttpResponseStatus.BAD_REQUEST).end(badRequest().toString());
    }

    public static BaseResponse badRequest() {
        return new BaseResponse(HttpResponseStatus.BAD_REQUEST.code(), "Bad request!", null);
    }

    public static void requestTimeout(RoutingContext ctx) {
        setResponseStatus(ctx, HttpResponseStatus.REQUEST_TIMEOUT)
                .end(BaseResponse.fail(HttpResponseStatus.REQUEST_TIMEOUT).toString());
    }

    private static HttpServerResponse setResponseStatus(RoutingContext ctx,
            HttpResponseStatus httpResponseStatus) {
        return ctx.response().setStatusCode(httpResponseStatus.code())
                .putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
    }

    public static void serverError(RoutingContext ctx, Throwable t) {
        LOGGER.error(t);

        setResponseStatus(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR)
                .end(serverError().toString());
    }
}
