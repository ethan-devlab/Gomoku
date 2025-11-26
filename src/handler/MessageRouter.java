package handler;

/**
 * MessageRouter parses protocol messages and dispatches them to appropriate
 * handlers.
 * This decouples message parsing from handling logic.
 */
public class MessageRouter {

    private final MessageCodec codec;
    private final MessageHandler handler;

    /**
     * Interface for handling parsed messages.
     */
    public interface MessageHandler {
        void handleServerInit();

        void handleClientInit();

        void handleInit(String data);

        void handleMove(String moveData);

        void handleReady();

        void handleStart();

        void handleWithdraw(String data);

        void handleCanPlay();

        void handleWin(String winner);

        void handleLose(String loser);

        void handleTie();

        void handlePattern(String pattern);

        void handleRestartInit();

        void handleRestart();

        void handleTurnTime(String time);

        void handlePlayerTime(String time);

        void handleBye();

        void handleUnknown(String rawMessage);
    }

    public MessageRouter(MessageHandler handler) {
        this.codec = new MessageCodec();
        this.handler = handler;
    }

    /**
     * Route a raw wire message to the appropriate handler.
     */
    public void route(String rawMessage) {
        if (rawMessage == null || rawMessage.isEmpty()) {
            return;
        }

        GameMessage message = codec.decode(rawMessage);

        switch (message.command()) {
            case SERVER_INIT -> handler.handleServerInit();
            case CLIENT_INIT -> handler.handleClientInit();
            case INIT -> handler.handleInit(message.payload());
            case MOVE -> handler.handleMove(message.payload());
            case READY -> handler.handleReady();
            case START -> handler.handleStart();
            case WITHDRAW -> handler.handleWithdraw(message.payload());
            case CAN_PLAY -> handler.handleCanPlay();
            case WIN -> handler.handleWin(message.payload());
            case LOSE -> handler.handleLose(message.payload());
            case TIE -> handler.handleTie();
            case PATTERN -> handler.handlePattern(message.payload());
            case RESTART_INIT -> handler.handleRestartInit();
            case RESTART -> handler.handleRestart();
            case TURN_TIME -> handler.handleTurnTime(message.payload());
            case PLAYER_TIME -> handler.handlePlayerTime(message.payload());
            case BYE -> handler.handleBye();
            case UNKNOWN -> handler.handleUnknown(rawMessage);
        }
    }
}
