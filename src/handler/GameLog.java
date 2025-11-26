package handler;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Model for game logging with typed entries.
 * Provides immutable access to log entries and supports observers.
 */
public class GameLog {

    /**
     * Represents a single log entry with timestamp and message.
     */
    public record LogEntry(ZonedDateTime timestamp, String message) {
        public LogEntry(String message) {
            this(ZonedDateTime.now(), message);
        }

        @Override
        public String toString() {
            return message;
        }
    }

    /**
     * Listener interface for log updates.
     */
    public interface LogListener {
        void onLogUpdated(List<LogEntry> entries);
    }

    private final List<LogEntry> entries;
    private final List<LogListener> listeners;

    public GameLog() {
        this.entries = new ArrayList<>();
        this.listeners = new ArrayList<>();
    }

    /**
     * Add a log entry with the current timestamp.
     */
    public void add(String message) {
        entries.add(new LogEntry(message));
        notifyListeners();
    }

    /**
     * Add a log entry with a specific timestamp.
     */
    public void add(ZonedDateTime timestamp, String message) {
        entries.add(new LogEntry(timestamp, message));
        notifyListeners();
    }

    /**
     * Add an empty line to the log.
     */
    public void addBlank() {
        entries.add(new LogEntry(""));
        notifyListeners();
    }

    /**
     * Get all log entries as an immutable list.
     */
    public List<LogEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    /**
     * Get all log messages as strings (for compatibility).
     */
    public ArrayList<String> getMessages() {
        ArrayList<String> messages = new ArrayList<>();
        for (LogEntry entry : entries) {
            messages.add(entry.message());
        }
        return messages;
    }

    /**
     * Clear all log entries.
     */
    public void clear() {
        entries.clear();
        notifyListeners();
    }

    /**
     * Add a listener for log updates.
     */
    public void addListener(LogListener listener) {
        listeners.add(listener);
    }

    /**
     * Remove a listener.
     */
    public void removeListener(LogListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        List<LogEntry> snapshot = Collections.unmodifiableList(new ArrayList<>(entries));
        for (LogListener listener : listeners) {
            listener.onLogUpdated(snapshot);
        }
    }

    /**
     * Get the number of entries.
     */
    public int size() {
        return entries.size();
    }
}
