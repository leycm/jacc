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
package de.leycm.jacc.log;


import lombok.NonNull;

/**
 * Represents a log level with associated name and color.
 */
public record CLogLevel(@NonNull String lvl, @NonNull String name, int color) {

    public static final CLogLevel TRACE = new CLogLevel("trace", "TRACE", 0xAAAAAA);
    public static final CLogLevel DEBUG = new CLogLevel("debug", "DEBUG", 0x0000FF);
    public static final CLogLevel INFO = new CLogLevel("info", "INFO", 0x00FF00);
    public static final CLogLevel SOFT_WARN = new CLogLevel("soft-warn", "SOFT_WARN", 0xFFFF00);
    public static final CLogLevel WARN = new CLogLevel("warn", "WARN", 0xFFA500);
    public static final CLogLevel ERROR = new CLogLevel("error", "ERROR", 0xFF0000);

    @Override
    @NonNull
    public String toString() {
        return name.toUpperCase();
    }
}