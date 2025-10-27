
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

import de.leycm.jacc.LogApiModule;
import de.leycm.jacc.log.CLogLevel;
import de.leycm.jacc.log.CLogProfile;
import de.leycm.jacc.log.CLogRecord;
import de.leycm.jacc.util.LogRecordUtils;
import lombok.NonNull;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Adapter for Java Util Logging (JUL).
 */
public final class JulAdapter implements LogAdapter {

    private static final Set<Handler> handlers = new HashSet<>();
    private static final Handler HANDLER = new JulHandler();
    private volatile boolean registered = false;

    @Override
    public void register() {
        if (registered) return;
        Logger rootLogger = LogManager.getLogManager().getLogger("");

        for (Handler h : rootLogger.getHandlers()) {
            rootLogger.removeHandler(h);
            handlers.add(h);
        }

        rootLogger.addHandler(HANDLER);
        registered = true;
    }

    @Override
    public void unregister() {
        if (!registered) return;
        Logger rootLogger = LogManager.getLogManager().getLogger("");

        for (Handler h : handlers) {
            rootLogger.addHandler(h);
        } handlers.clear();

        rootLogger.removeHandler(HANDLER);
        registered = false;
    }

    private static final class JulHandler extends Handler {

        @Override
        public void publish(@NonNull LogRecord record) {
            if (!isLoggable(record)) return;

            CLogLevel level = convertLevel(record.getLevel());
            String loggerName = record.getLoggerName() != null
                    ? record.getLoggerName()
                    : "unknown";
            CLogProfile profile = new CLogProfile(loggerName);

            for (CLogRecord r : LogRecordUtils.splitMessage(level, profile,
                    record.getMessage(), LogApiModule.getInstance().maxLength())) {
                LogApiModule.getInstance().send(r);
            }
        }

        private @NonNull CLogLevel convertLevel(@NonNull Level level) {
            int value = level.intValue();

            if (value >= Level.SEVERE.intValue()) {
                return CLogLevel.ERROR;
            } else if (value >= Level.WARNING.intValue()) {
                return CLogLevel.WARN;
            } else if (value >= Level.INFO.intValue()) {
                return CLogLevel.INFO;
            } else if (value >= Level.FINE.intValue()) {
                return CLogLevel.DEBUG;
            } else {
                return CLogLevel.TRACE;
            }
        }


        @Override
        public void flush() {}

        @Override
        public void close() throws UnsupportedOperationException {
            throw new UnsupportedOperationException("Cannot close JUL adapter handler");
        }
    }
}