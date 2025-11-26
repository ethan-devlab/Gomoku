package handler;

/**
 * Immutable record representing a parsed protocol message.
 * Contains the command type and optional payload data.
 */
public record GameMessage(GameCommand command, String payload) {

    /**
     * Create a message with no payload.
     */
    public GameMessage(GameCommand command) {
        this(command, "");
    }

    /**
     * Check if this message has a payload.
     */
    public boolean hasPayload() {
        return payload != null && !payload.isEmpty();
    }
}
