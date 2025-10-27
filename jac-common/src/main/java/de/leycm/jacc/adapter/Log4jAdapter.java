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
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Handler;

/**
 * Adapter for Apache Log4j 2.x.
 */
public final class Log4jAdapter implements LogAdapter {

    private static final Set<Handler> handlers = new HashSet<>();
    private static final String APPENDER_NAME = "JaccLog4jAppender";
    private volatile boolean registered = false;

    @Override
    public void register() {
        if (registered) return;

        LoggerContext context =
                (LoggerContext)
                        LogManager.getContext(false);

        Configuration config = context.getConfiguration();
        JaccAppender appender = JaccAppender.createAppender(APPENDER_NAME, null, null);
        appender.start();
        config.addAppender(appender);
        config.getRootLogger().addAppender(appender, null, null);
        context.updateLoggers();

        registered = true;
    }

    @Override
    public void unregister() {
        if (!registered) return;

        org.apache.logging.log4j.core.LoggerContext context =
                (org.apache.logging.log4j.core.LoggerContext)
                        org.apache.logging.log4j.LogManager.getContext(false);

        org.apache.logging.log4j.core.config.Configuration config = context.getConfiguration();
        Appender appender = config.getAppender(APPENDER_NAME);
        if (appender != null) {
            config.getRootLogger().removeAppender(APPENDER_NAME);
            appender.stop();
        }
        context.updateLoggers();

        registered = false;
    }

    @Plugin(name = "JaccAppender", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE)
    public static final class JaccAppender extends AbstractAppender {

        private JaccAppender(
                @NonNull String name,
                @Nullable Filter filter,
                @Nullable Property[] properties) {
            super(name, filter, null, true, properties != null ? properties : Property.EMPTY_ARRAY);
        }

        @PluginFactory
        @NonNull
        public static JaccAppender createAppender(
                @PluginAttribute("name") @Nullable String name,
                @PluginElement("Filter") @Nullable Filter filter,
                @PluginElement("Properties") @Nullable Property[] properties) {
            if (name == null) {
                name = "JaccAppender";
            }
            return new JaccAppender(name, filter, properties);
        }

        @Override
        public void append(@NonNull LogEvent event) {
            CLogLevel level = convertLevel(event.getLevel());
            String loggerName = event.getLoggerName() != null
                    ? event.getLoggerName()
                    : "unknown";
            CLogProfile profile = new CLogProfile(loggerName);

            for (CLogRecord r : LogRecordUtils.splitMessage(level, profile,
                    event.getMessage().getFormattedMessage(), LogApiModule.getInstance().maxLength())) {
                LogApiModule.getInstance().send(r);
            }
        }

        @NonNull
        private CLogLevel convertLevel(@NonNull Level level) {
            if (level.isMoreSpecificThan(Level.ERROR)) {
                return CLogLevel.ERROR;
            } else if (level.isMoreSpecificThan(Level.WARN)) {
                return CLogLevel.WARN;
            } else if (level.isMoreSpecificThan(Level.INFO)) {
                return CLogLevel.INFO;
            } else if (level.isMoreSpecificThan(Level.DEBUG)) {
                return CLogLevel.DEBUG;
            } else {
                return CLogLevel.TRACE;
            }
        }

    }
}
