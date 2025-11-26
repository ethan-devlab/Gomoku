package handler;

/**
 * Configuration data for a game session.
 * Immutable record containing all settings exchanged during initialization.
 */
public record GameConfig(
        String playerFlag,
        String opponentFlag,
        String playerName,
        String opponentName,
        int turnTime,
        int playerTime,
        int firstPlayer,
        int withdrawCount,
        boolean isBlack) {
    /**
     * Check if turn time is enabled (not infinite).
     */
    public boolean hasTurnTime() {
        return turnTime != -1;
    }

    /**
     * Check if player time is enabled (not infinite).
     */
    public boolean hasPlayerTime() {
        return playerTime != -1;
    }

    /**
     * Check if withdraw limit is enabled (not infinite).
     */
    public boolean hasWithdrawLimit() {
        return withdrawCount != -1;
    }

    /**
     * Get player time in seconds.
     */
    public double getPlayerTimeSeconds() {
        return hasPlayerTime() ? playerTime * 60.0 : -1;
    }
}
