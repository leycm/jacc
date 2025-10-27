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

/**
 * Interface for logging framework adapters.
 */
public interface LogAdapter {

    /**
     * Registers this adapter with the underlying logging framework.
     */
    void register();

    /**
     * Unregisters this adapter from the underlying logging framework.
     */
    void unregister();
}