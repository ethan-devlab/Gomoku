package handler;

/**
 * Interface for receiving game session events.
 * Implementations can update UI or perform other actions in response to game
 * events.
 * This decouples the game logic from UI concerns.
 */
public interface GameEventListener {

    /**
     * Called when the game is initialized with configuration data.
     */
    void onGameInitialized(GameConfig config);

    /**
     * Called when the game starts.
     */
    void onGameStarted();

    /**
     * Called when a move is made on the board.
     */
    void onMoveMade(int player, int row, int col);

    /**
     * Called when a player wins.
     */
    void onGameWon(int player, String playerColor);

    /**
     * Called when the game ends in a tie.
     */
    void onGameTied();

    /**
     * Called when a player loses (e.g., timeout).
     */
    void onGameLost(int player, String reason);

    /**
     * Called when a pattern is detected (alive three, dead four, etc.).
     */
    void onPatternDetected(String pattern);

    /**
     * Called when a withdraw request is made.
     */
    void onWithdrawRequested(int player);

    /**
     * Called when a withdraw is approved.
     */
    void onWithdrawApproved(int row, int col);

    /**
     * Called when a withdraw is denied.
     */
    void onWithdrawDenied();

    /**
     * Called when the game is restarting.
     */
    void onGameRestarting();

    /**
     * Called when the game has restarted.
     */
    void onGameRestarted();

    /**
     * Called when turn time is updated.
     */
    void onTurnTimeUpdated(int timeLeft);

    /**
     * Called when player time is updated.
     */
    void onPlayerTimeUpdated(double timeLeft);

    /**
     * Called when it becomes this player's turn.
     */
    void onCanPlay(boolean canPlay);

    /**
     * Called when connection is closed.
     */
    void onConnectionClosed();

    /**
     * Called to display a message to the user.
     */
    void onMessage(String message);

    /**
     * Called to log game data.
     */
    void onLogEntry(String entry);
}
