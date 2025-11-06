/**
 * LECP-LICENSE NOTICE
 * <br><br>
 * This Sourcecode is under the LECP-LICENSE. <br>
 * License at: <a href="https://github.com/leycm/leycm/blob/main/LICENSE">GITHUB</a>
 * <br><br>
 * Copyright (c) LeyCM <leycm@proton.me> <br>
 * Copyright (c) maintainers <br>
 * Copyright (c) contributors
 */
package de.leycm.jacc.adapter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import de.leycm.jacc.LogApiFactory;
import de.leycm.jacc.log.CLogLevel;
import de.leycm.jacc.log.CLogProfile;
import de.leycm.jacc.log.CLogRecord;
import de.leycm.jacc.util.LogRecordUtils;
import lombok.NonNull;
import org.slf4j.LoggerFactory;

/**
 * Adapter for Logback.
 */
public final class LogbackAdapter implements LogAdapter {

    private static final JaccLogbackAppender APPENDER = new JaccLogbackAppender();
    private volatile boolean registered = false;

    @Override
    public void register() {
        if (registered) {
            return;
        }

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME);

        APPENDER.setContext(context);
        APPENDER.start();
        rootLogger.addAppender(APPENDER);

        registered = true;
    }

    @Override
    public void unregister() {
        if (!registered) {
            return;
        }

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME);

        rootLogger.detachAppender(APPENDER);
        APPENDER.stop();

        registered = false;
    }

    private static final class JaccLogbackAppender extends AppenderBase<ILoggingEvent> {

        @Override
        protected void append(@NonNull ILoggingEvent event) {
            CLogLevel level = convertLevel(event.getLevel());
            String loggerName = event.getLoggerName() != null
                    ? event.getLoggerName()
                    : "unknown";
            CLogProfile profile = new CLogProfile(loggerName);

            for (CLogRecord r : LogRecordUtils.splitMessage(level, profile,
                    event.getMessage(), LogApiFactory.getInstance().maxLength())) {
                LogApiFactory.getInstance().send(r);
            }

        }

        @NonNull
        private CLogLevel convertLevel(@NonNull Level level) {
            if (level.isGreaterOrEqual(Level.ERROR)) {
                return CLogLevel.ERROR;
            } else if (level.isGreaterOrEqual(Level.WARN)) {
                return CLogLevel.WARN;
            } else if (level.isGreaterOrEqual(Level.INFO)) {
                return CLogLevel.INFO;
            } else if (level.isGreaterOrEqual(Level.DEBUG)) {
                return CLogLevel.DEBUG;
            } else {
                return CLogLevel.TRACE;
            }
        }

    }
}
