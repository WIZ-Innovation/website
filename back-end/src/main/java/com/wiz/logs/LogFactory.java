package com.wiz.logs;

import io.vertx.core.logging.Log4j2LogDelegateFactory;
import io.vertx.core.spi.logging.LogDelegate;

public class LogFactory {
    private final LogDelegate LOGGER;

    private LogFactory(Class<?> clazz) {
        this.LOGGER = new Log4j2LogDelegateFactory().createDelegate(clazz.getName());
    }

    public static LogFactory getLogger(Class<?> clazz) {
        return new LogFactory(clazz);
    }

    public void info(String message) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(message);
        }
    }

    public void info(String message, Object... args) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(message, args);
        }
    }


    public void debug(String message) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(message);
        }
    }

    public void error(Throwable t) {
        LOGGER.error(t.getMessage(), t);
    }

    public void error(String message, Object... args) {
        LOGGER.error(message, args);
    }

    public boolean isDebugEnabled() {
        return LOGGER.isDebugEnabled();
    }

    public boolean isInfoEnabled() {
        return LOGGER.isInfoEnabled();
    }

    public boolean isTraceEnabled() {
        return LOGGER.isTraceEnabled();
    }

    public void trace(String message, Object... args) {
        if (isTraceEnabled()) {
            LOGGER.trace(message, args);
        }
    }

    public void trace(String message) {
        if (isTraceEnabled()) {
            LOGGER.trace(message);
        }
    }

    public void warn(String message, Object... args) {
        if (LOGGER.isWarnEnabled()) {
            LOGGER.warn(message, args);
        }
    }
}
