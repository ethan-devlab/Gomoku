package handler;

/**
 * Interface for receiving timer events.
 * Decouples timer logic from UI concerns.
 */
public interface TimeEventListener {

    /**
     * Called when turn time is updated.
     * 
     * @param timeLeft seconds remaining in the turn
     */
    void onTurnTimeUpdate(int timeLeft);

    /**
     * Called when player time is updated.
     * 
     * @param timeLeft seconds remaining for the player
     */
    void onPlayerTimeUpdate(double timeLeft);

    /**
     * Called when turn time expires.
     * 
     * @param player the player who timed out
     */
    void onTurnTimeExpired(int player);

    /**
     * Called when player time expires.
     * 
     * @param player the player who timed out
     */
    void onPlayerTimeExpired(int player);

    /**
     * Called to broadcast time update over the network.
     * 
     * @param command the time command (TURN_TIME or PLAYER_TIME)
     * @param value   the time value
     */
    void onTimeMessage(GameCommand command, String value);

    /**
     * Called when a player loses due to timeout.
     * 
     * @param player the losing player
     */
    void onTimeoutLoss(int player);
}
