package de.leycm.jacc;

import de.leycm.jacc.log.CLogRecord;
import de.leycm.neck.instance.Initializable;
import lombok.NonNull;
import org.jetbrains.annotations.Contract;

/**
 * Module interface for logging API operations.
 */
public interface LogApiModule extends Initializable {

    /**
     * Returns the singleton instance of the LogApiModule.
     *
     * @return the LogApiModule instance
     * @throws NullPointerException if no instance is registered
     * @see Initializable#getInstance(Class)
     */
    @NonNull
    @Contract(pure = true)
    static LogApiModule getInstance() {
        return Initializable.getInstance(LogApiModule.class);
    }

    int maxLength();

    /**
     * Sends a log record for processing.
     *
     * @param record the log record to send
     */
    void send(@NonNull CLogRecord record);
}