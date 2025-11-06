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

import de.leycm.flux.event.Cancelable;
import de.leycm.flux.event.Event;
import de.leycm.flux.event.Monitorable;
import de.leycm.jacc.log.CLogRecord;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import lombok.Getter;
import lombok.NonNull;

/**
 * Event that is triggered during logging operations.
 * Contains both the current (potentially modified) record and the original unmodified record.
 */
@Getter
public final class LogEvent implements Event, Cancelable, Monitorable<LogEvent> {

    private final CLogRecord record;
    private final CLogRecord originalRecord;
    private final Instant timestamp;
    private boolean canceled;

    DateTimeFormatter timestampFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
            .withZone(ZoneId.systemDefault());

    DateTimeFormatter daystampFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            .withZone(ZoneId.systemDefault());

    public LogEvent(@NonNull CLogRecord record) {
        this(record, record.copy(), Instant.now());
    }

    private LogEvent(
            @NonNull CLogRecord record,
            @NonNull CLogRecord originalRecord,
            @NonNull Instant timestamp) {
        this.record = record;
        this.originalRecord = originalRecord;
        this.timestamp = timestamp;
        this.canceled = false;
    }

    @Override
    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    @Override
    public boolean isCanceled() {
        return canceled;
    }

    @Override
    public @NonNull Instant timestamp() {
        return timestamp;
    }

    @Override
    public @NonNull LogEvent copy() {
        LogEvent copy = new LogEvent(record, originalRecord, timestamp);
        copy.setCanceled(canceled);
        return copy;
    }
}