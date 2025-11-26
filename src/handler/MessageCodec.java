package handler;

/**
 * Codec for encoding and decoding protocol messages.
 * Centralizes all wire format parsing/serialization for both client and server.
 */
public class MessageCodec {

    private static final String DELIMITER = ":";

    /**
     * Decode a wire format string into a GameMessage.
     * Format: "COMMAND" or "COMMAND:payload"
     */
    public GameMessage decode(String wireMessage) {
        if (wireMessage == null || wireMessage.isEmpty()) {
            return new GameMessage(GameCommand.UNKNOWN, "");
        }

        int delimiterIndex = wireMessage.indexOf(DELIMITER);
        if (delimiterIndex == -1) {
            // No payload
            GameCommand command = GameCommand.fromWireFormat(wireMessage);
            return new GameMessage(command, "");
        }

        String commandPart = wireMessage.substring(0, delimiterIndex);
        String payload = wireMessage.substring(delimiterIndex + 1);
        GameCommand command = GameCommand.fromWireFormat(commandPart);
        return new GameMessage(command, payload);
    }

    /**
     * Encode a GameMessage into wire format string.
     */
    public String encode(GameMessage message) {
        if (message.hasPayload()) {
            return message.command().getWireFormat() + DELIMITER + message.payload();
        }
        return message.command().getWireFormat();
    }

    /**
     * Convenience method to encode command with payload.
     */
    public String encode(GameCommand command, String payload) {
        return encode(new GameMessage(command, payload));
    }

    /**
     * Convenience method to encode command without payload.
     */
    public String encode(GameCommand command) {
        return encode(new GameMessage(command));
    }
}
