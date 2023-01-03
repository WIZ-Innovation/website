package com.wiz.handlers;

import java.text.DateFormat;
import java.util.Date;
import com.wiz.logs.LogFactory;
import com.wiz.utils.CommonUtils;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpVersion;
import io.vertx.core.net.SocketAddress;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.LoggerFormat;

/**
 * Code based on {@link io.vertx.ext.web.handler.impl.LoggerHandlerImpl}.
 */
public class LoggerHandlerImpl implements Handler<RoutingContext> {
    private static final LogFactory LOGGER = LogFactory.getLogger(LoggerHandlerImpl.class);

    private final boolean immediate;
    private final LoggerFormat format;
    private final DateFormat dateTimeFormat;

    public static LoggerHandlerImpl create() {
        return new LoggerHandlerImpl(false, LoggerFormat.DEFAULT);
    }

    public LoggerHandlerImpl(boolean immediate, LoggerFormat format) {
        this.immediate = immediate;
        this.format = format;
        this.dateTimeFormat = DateFormat.getDateTimeInstance();
    }

    @Override
    public void handle(RoutingContext context) {
        long timestamp = System.currentTimeMillis();
        String remoteClient = this.getClientAddress(context.request().remoteAddress());
        HttpMethod method = context.request().method();
        String uri = context.request().uri();
        HttpVersion version = context.request().version();
        if (this.immediate) {
            this.log(context, timestamp, remoteClient, version, method, uri);
        } else {
            context.addBodyEndHandler((handler) -> {
                this.log(context, timestamp, remoteClient, version, method, uri);
            });
        }

        context.next();
    }

    private void log(RoutingContext context, long timestamp, String remoteClient,
            HttpVersion version, HttpMethod method, String uri) {
        HttpServerRequest request = context.request();
        long contentLength = 0L;
        String versionFormatted;
        if (this.immediate) {
            versionFormatted = request.headers().get(HttpHeaderNames.CONTENT_LENGTH.toString());
            if (versionFormatted != null) {
                try {
                    contentLength = Long.parseLong(versionFormatted.toString());
                } catch (NumberFormatException var17) {
                    contentLength = 0L;
                }
            }
        } else {
            contentLength = request.response().bytesWritten();
        }

        versionFormatted = "-";
        switch (version) {
            case HTTP_1_0:
                versionFormatted = "HTTP/1.0";
                break;
            case HTTP_1_1:
                versionFormatted = "HTTP/1.1";
                break;
            case HTTP_2:
                versionFormatted = "HTTP/2.0";
                break;
            default:
                versionFormatted = "-";
                break;
        }

        MultiMap headers = request.headers();
        int status = request.response().getStatusCode();
        String message = null;
        String referrer = CommonUtils.getOrDefault(headers.get("referrer"),
                headers.get(HttpHeaderNames.REFERER.toString()));
        String userAgent =
                CommonUtils.getOrDefault(headers.get(HttpHeaderNames.USER_AGENT.toString()), "-");

        switch (this.format) {
            case DEFAULT:
                String body = context.body().asString();

                message =
                        "{} -- [{}] \"{} {} {}\" {}, contentLength={}, referrer={}, userAgent={} {}ms\n {}";
                this.doLog(status, message, remoteClient,
                        this.dateTimeFormat.format(new Date(timestamp)), method, uri,
                        versionFormatted, status, contentLength, referrer, userAgent,
                        System.currentTimeMillis() - timestamp, body);
                break;
            case SHORT:
                message = String.format("%s - - [%s] \"%s %s %s\" %d %d \"%s\" \"%s\" %dms",
                        remoteClient, this.dateTimeFormat.format(new Date(timestamp)), method, uri,
                        versionFormatted, status, contentLength, referrer, userAgent,
                        System.currentTimeMillis() - timestamp);
                this.doLog(status, message);

                break;
            case TINY:
                message = String.format("%s - %s %s %d %d", remoteClient, method, uri, status,
                        contentLength);
                this.doLog(status, message);

                break;
            default:
                message = String.format("%s - %s %s %d %d", remoteClient, method, uri, status,
                        contentLength);
                this.doLog(status, message);

                break;
        }
    }

    private void doLog(int status, String message, Object... args) {
        if (status >= 500) {
            LOGGER.error(message, args);
        } else if (status >= 400) {
            LOGGER.warn(message, args);
        } else {
            LOGGER.info(message, args);
        }
    }

    private String getClientAddress(SocketAddress remoteAddress) {
        return remoteAddress == null ? null : remoteAddress.host();
    }
}
