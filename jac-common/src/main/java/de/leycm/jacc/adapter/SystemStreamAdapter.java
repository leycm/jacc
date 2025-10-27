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

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Adapter for {@link System#out} and {@link System#err} streams.
 * Intercepts console output and redirects it through the log API.
 */
public final class SystemStreamAdapter implements LogAdapter {

    private static final CLogProfile STDOUT_PROFILE = new CLogProfile("System.out");
    private static final CLogProfile STDERR_PROFILE = new CLogProfile("System.err");

    private PrintStream originalOut;
    private PrintStream originalErr;
    private volatile boolean registered = false;

    @Override
    public void register() {
        if (registered) {
            return;
        }

        originalOut = System.out;
        originalErr = System.err;

        System.setOut(new PrintStream(new LogOutputStream(CLogLevel.INFO, STDOUT_PROFILE), true));
        System.setErr(new PrintStream(new LogOutputStream(CLogLevel.ERROR, STDERR_PROFILE), true));

        registered = true;
    }

    @Override
    public void unregister() {
        if (!registered) return;

        System.setOut(originalOut);
        System.setErr(originalErr);

        registered = false;
    }

    private static final class LogOutputStream extends OutputStream {

        private final CLogLevel level;
        private final CLogProfile profile;
        private final StringBuilder buffer = new StringBuilder();

        public LogOutputStream(@NonNull CLogLevel level, @NonNull CLogProfile profile) {
            this.level = level;
            this.profile = profile;
        }

        @Override
        public void write(int b) {
            if (b == '\n' || b == '\r') {
                flush();
            } else {
                buffer.append((char) b);
            }
        }

        @Override
        public void flush() {
            if (!buffer.isEmpty()) {
                String message = buffer.toString().trim();
                if (!message.isEmpty()) {
                    message = message.replace("\n", "\\n").replace("\r", "\\r");
                    for (CLogRecord r : LogRecordUtils.splitMessage(level, profile, message, LogApiModule.getInstance().maxLength())) {
                        LogApiModule.getInstance().send(r);
                    }
                }
                buffer.setLength(0);
            }
        }
    }
}
