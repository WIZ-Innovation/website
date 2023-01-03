package com.wiz.logs;

import org.apache.ignite.IgniteLogger;
import org.jetbrains.annotations.Nullable;

public class IgniteLoggerImpl implements IgniteLogger {
    private static final LogFactory LOGGER = LogFactory.getLogger(IgniteLoggerImpl.class);

    @Override
    public void debug(String message) {
        LOGGER.debug(message);
    }

    @Override
    public void error(String message, @Nullable Throwable throwable) {
        LOGGER.error(message, throwable);
    }

    @Override
    public String fileName() {
        return null;
    }

    @Override
    public IgniteLogger getLogger(Object arg0) {
        return this;
    }

    @Override
    public void info(String message) {
        LOGGER.info(message);
    }

    @Override
    public boolean isDebugEnabled() {
        return LOGGER.isDebugEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        return LOGGER.isInfoEnabled();
    }

    @Override
    public boolean isQuiet() {
        return false;
    }

    @Override
    public boolean isTraceEnabled() {
        return LOGGER.isTraceEnabled();
    }

    @Override
    public void trace(String message) {
        LOGGER.trace(message);
    }

    @Override
    public void warning(String message, @Nullable Throwable throwable) {
        LOGGER.warn(message, throwable);
    }

}
