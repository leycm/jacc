package de.leycm.jacc;

import de.leycm.jacc.log.CLogRecord;
import de.leycm.neck.instance.Initializable;
import lombok.NonNull;
import org.jetbrains.annotations.Contract;

/**
 * Module interface for logging API operations.
 */
public interface LogApiFactory extends Initializable {

    /**
     * Returns the singleton instance of the LogApiFactory.
     *
     * @return the LogApiFactory instance
     * @throws NullPointerException if no instance is registered
     * @see Initializable#getInstance(Class)
     */
    @NonNull
    @Contract(pure = true)
    static LogApiFactory getInstance() {
        return Initializable.getInstance(LogApiFactory.class);
    }

    int maxLength();

    /**
     * Sends a log record for processing.
     *
     * @param record the log record to send
     */
    void send(@NonNull CLogRecord record);
}