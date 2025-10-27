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

import de.leycm.flux.event.Event;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import de.leycm.jacc.LogApiModule;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

/**
 * Represents a log record with message validation and event tracking.
 */
@Getter
public final class CLogRecord {

    private final CLogLevel level;
    private final CLogProfile profile;
    private final String message;
    private final Instant timestamp;
    private final List<Event> events;

    @Nullable
    private CLogType type;

    private String formattedMessage;

    public CLogRecord(
            @NonNull CLogLevel level,
            @NonNull CLogProfile profile,
            @NonNull String message) {
        this(level, profile, message, Instant.now(), message);
    }

    private CLogRecord(
            @NonNull CLogLevel level,
            @NonNull CLogProfile profile,
            @NonNull String message,
            @NonNull Instant timestamp,
            @NonNull String formattedMessage) {
        validateMessage(message);
        this.level = level;
        this.profile = profile;
        this.message = message;
        this.timestamp = timestamp;
        this.formattedMessage = formattedMessage;
        this.events = new ArrayList<>();
    }

    private void validateMessage(@NonNull String message) {
        if (message.contains("\n")) {
            throw new IllegalArgumentException("Message can not contain newline characters (\\n)");
        }
        if (message.length() > LogApiModule.getInstance().maxLength()) {
            throw new IllegalArgumentException(
                    "Message exceeds maximum length of " + LogApiModule.getInstance().maxLength() + " characters");
        }
    }

    @NonNull
    public CLogLevel getLevel() {
        return level;
    }

    @NonNull
    public CLogProfile getProfile() {
        return profile;
    }

    @NonNull
    public String getMessage() {
        return message;
    }

    @NonNull
    public String getFormattedMessage() {
        return formattedMessage;
    }

    public void setFormattedMessage(@NonNull String formattedMessage) {
        validateMessage(formattedMessage);
        this.formattedMessage = formattedMessage;
    }

    @NonNull
    public Instant getTimestamp() {
        return timestamp;
    }

    @Nullable
    public CLogType getType() {
        return type;
    }

    public void setType(@Nullable CLogType type) {
        this.type = type;
    }

    public void addEvent(@NonNull Event event) {
        events.add(event);
    }

    @Contract(pure = true)
    @NonNull
    public @Unmodifiable List<Event> getEvents() {
        return List.copyOf(events);
    }

    @NonNull
    public CLogRecord copy() {
        CLogRecord copy = new CLogRecord(level, profile, message, timestamp, formattedMessage);
        copy.setType(type);
        copy.events.addAll(events);
        return copy;
    }

    @Override
    @NonNull
    public String toString() {
        return formattedMessage;
    }

}