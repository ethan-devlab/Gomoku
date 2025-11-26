package handler;

/**
 * Enum representing all protocol commands for the Gomoku game.
 * Replaces the string constants in GameFlags for type-safe protocol handling.
 */
public enum GameCommand {
    SERVER_INIT("SERVER_INIT"),
    CLIENT_INIT("CLIENT_INIT"),
    INIT("INIT"),
    MOVE("MOVE"),
    START("START"),
    READY("READY"),
    WIN("WIN"),
    LOSE("LOSE"),
    TURN_TIME("TURN_TIME"),
    PLAYER_TIME("PLAYER_TIME"),
    WITHDRAW("WITHDRAW"),
    RESTART("RESTART"),
    RESTART_INIT("RESTART_INIT"),
    BYE("BYE"),
    CAN_PLAY("CAN_PLAY"),
    TIE("TIE"),
    PATTERN("PATTERN"),
    UNKNOWN("UNKNOWN");

    private final String wireFormat;

    GameCommand(String wireFormat) {
        this.wireFormat = wireFormat;
    }

    public String getWireFormat() {
        return wireFormat;
    }

    /**
     * Parse a wire format string into a GameCommand.
     * Returns UNKNOWN if the command is not recognized.
     */
    public static GameCommand fromWireFormat(String wire) {
        if (wire == null || wire.isEmpty()) {
            return UNKNOWN;
        }
        for (GameCommand cmd : values()) {
            if (cmd.wireFormat.equals(wire)) {
                return cmd;
            }
        }
        return UNKNOWN;
    }
}
