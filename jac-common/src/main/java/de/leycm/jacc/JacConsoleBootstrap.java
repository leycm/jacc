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
package de.leycm.jacc;

import de.leycm.flux.registry.EventExecutorBus;
import de.leycm.jacc.event.LogEvent;
import de.leycm.jacc.event.TypeSpecifyEvent;
import de.leycm.jacc.log.CLogRecord;
import lombok.NonNull;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public record JacConsoleBootstrap(int maxLength, boolean typeFilter)
        implements LogApiModule {
    public final static PrintStream OUT = new PrintStream(new FileOutputStream(FileDescriptor.out),
            true, StandardCharsets.UTF_8);
    public final static PrintStream ERR = new PrintStream(new FileOutputStream(FileDescriptor.err),
            true, StandardCharsets.UTF_8);
    public final static PrintStream IN = new PrintStream(new FileOutputStream(FileDescriptor.in),
            true, StandardCharsets.UTF_8);


    @Override
    public int maxLength() {
        return maxLength;
    }

    @Override
    public void send(@NonNull CLogRecord record) {

        TypeSpecifyEvent event = new TypeSpecifyEvent(record);
        if (typeFilter) EventExecutorBus.getInstance().fire(event);

        LogEvent log = new LogEvent(event.getRecord());
        EventExecutorBus.getInstance().fire(log);

        OUT.println(log.getRecord().getFormattedMessage());

    }

}
