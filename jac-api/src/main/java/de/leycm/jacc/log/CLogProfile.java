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
import org.jetbrains.annotations.Nullable;

/**
 * Represents a logging profile identified by a fully qualified name.
 */
public record CLogProfile(@NonNull String id) {

    @NonNull
    public String simpleName() {
        String[] split = id.split("\\.");
        return split[split.length - 1];
    }

    @Nullable
    public Class<?> clazz() {
        try {
            return Class.forName(id);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}