package de.leycm.jacc.util;

import de.leycm.jacc.log.CLogLevel;
import de.leycm.jacc.log.CLogProfile;
import de.leycm.jacc.log.CLogRecord;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

public final class LogRecordUtils {

    private LogRecordUtils() {
        throw new UnsupportedOperationException("This is a Util class it can not be initialized");
    }

    public static @NonNull List<CLogRecord> splitMessage(
            @NonNull CLogLevel level,
            @NonNull CLogProfile profile,
            @NonNull String message,
            int maxLen
    ) {
        List<CLogRecord> records = new ArrayList<>();

        String[] lines = message.split("\\r?\\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            int start = 0;
            while (start < line.length()) {
                int end = Math.min(start + maxLen, line.length());

                if (end < line.length()) {
                    int lastSpace = line.lastIndexOf(' ', end);
                    if (lastSpace > start) {
                        end = lastSpace;
                    }
                }

                String part = line.substring(start, end).trim();
                if (!part.isEmpty()) {
                    CLogRecord record = new CLogRecord(level, profile, part);
                    records.add(record);
                }

                start = end;
                while (start < line.length() && line.charAt(start) == ' ') {
                    start++;
                }
            }
        }

        return records;
    }
}
