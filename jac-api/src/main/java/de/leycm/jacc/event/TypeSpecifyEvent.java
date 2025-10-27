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
package de.leycm.jacc.event;

import de.leycm.flux.event.Event;
import de.leycm.flux.event.Monitorable;
import de.leycm.jacc.log.CLogRecord;
import java.time.Instant;
import lombok.Getter;
import lombok.NonNull;

/**
 * Event for type specification during logging operations.
 * Contains both the current (potentially modified) record and the original unmodified record.
 */
@Getter
public final class TypeSpecifyEvent implements Event, Monitorable<TypeSpecifyEvent> {

    private final CLogRecord record;
    private final CLogRecord originalRecord;
    private final Instant timestamp;

    public TypeSpecifyEvent(@NonNull CLogRecord record) {
        this(record, record.copy(), Instant.now());
    }

    private TypeSpecifyEvent(
            @NonNull CLogRecord record,
            @NonNull CLogRecord originalRecord,
            @NonNull Instant timestamp) {
        this.record = record;
        this.originalRecord = originalRecord;
        this.timestamp = timestamp;
    }

    @Override
    public @NonNull Instant timestamp() {
        return timestamp;
    }

    @Override
    public @NonNull TypeSpecifyEvent copy() {
        return new TypeSpecifyEvent(record, originalRecord, timestamp);
    }
}