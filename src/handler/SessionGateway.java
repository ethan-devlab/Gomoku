package handler;

import java.io.IOException;

/**
 * Interface for network communication.
 * Abstracts socket I/O operations for testability and decoupling.
 */
public interface SessionGateway {

    /**
     * Send a message over the network.
     */
    void sendMessage(String message);

    /**
     * Send a GameMessage over the network.
     */
    default void sendMessage(GameMessage message) {
        MessageCodec codec = new MessageCodec();
        sendMessage(codec.encode(message));
    }

    /**
     * Send a command without payload.
     */
    default void sendCommand(GameCommand command) {
        sendMessage(new GameMessage(command));
    }

    /**
     * Send a command with payload.
     */
    default void sendCommand(GameCommand command, String payload) {
        sendMessage(new GameMessage(command, payload));
    }

    /**
     * Close the connection.
     */
    void close() throws IOException;

    /**
     * Check if the connection is open.
     */
    boolean isConnected();
}
