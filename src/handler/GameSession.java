package handler;

import java.time.ZonedDateTime;

/**
 * GameSession manages the core game logic and state.
 * This class is independent of UI concerns and can be tested in isolation.
 */
public class GameSession {

    private final GameState gameState;
    private final GameLog gameLog;
    private final GameEventListener eventListener;

    private GameConfig config;
    private boolean gameStarted;
    private boolean gameEnded;
    private int lastMoveRow = -1;
    private int lastMoveCol = -1;
    private int withdrawsRemaining;

    public GameSession(GameEventListener eventListener) {
        this.gameState = new GameState();
        this.gameLog = new GameLog();
        this.eventListener = eventListener;
        this.gameStarted = false;
        this.gameEnded = false;
        this.withdrawsRemaining = -1; // unlimited by default
    }

    /**
     * Initialize the session with game configuration.
     */
    public void initialize(GameConfig config, String connectionInfo) {
        this.config = config;
        this.withdrawsRemaining = config.withdrawCount();

        gameLog.add("GAME INITIALIZED AT " + ZonedDateTime.now());
        gameLog.add(connectionInfo);

        if (eventListener != null) {
            eventListener.onGameInitialized(config);
        }
    }

    /**
     * Start the game.
     */
    public void startGame() {
        if (!gameStarted) {
            gameStarted = true;
            gameLog.add("START");

            if (eventListener != null) {
                eventListener.onGameStarted();
            }
        }
    }

    /**
     * Process a move on the board.
     * 
     * @return true if the move was valid and processed
     */
    public boolean makeMove(int player, int row, int col) {
        if (gameEnded || !gameState.isValidMove(row, col)) {
            return false;
        }

        gameState.setCurrentPlayer(player);
        gameState.makeMove(row, col);
        lastMoveRow = row;
        lastMoveCol = col;

        String playerColor = (player == 1) ? "Black" : "White";
        String message = playerColor + " placed stone at " + Character.toString(col + 'A') + (row + 1);
        gameLog.add(message);

        if (eventListener != null) {
            eventListener.onMoveMade(player, row, col);
        }

        return true;
    }

    /**
     * Check for win condition after a move.
     * 
     * @return true if the game has ended (win or tie)
     */
    public boolean checkGameEnd(int row, int col) {
        if (gameEnded) {
            return true;
        }

        int player = gameState.getCurrentPlayer() == 1 ? 2 : 1; // Player who just moved
        String playerColor = (player == 1) ? "Black" : "White";

        if (gameState.checkWin(row, col)) {
            gameEnded = true;
            String message = playerColor + " wins!";
            gameLog.add(message);

            if (eventListener != null) {
                eventListener.onGameWon(player, playerColor);
            }
            return true;
        }

        if (gameState.isTieGame()) {
            gameEnded = true;
            gameLog.add("TIE GAME");

            if (eventListener != null) {
                eventListener.onGameTied();
            }
            return true;
        }

        // Check for patterns
        String pattern = gameState.checkPatterns(row, col);
        if (!pattern.isEmpty() && eventListener != null) {
            gameLog.add(pattern);
            eventListener.onPatternDetected(pattern);
        }

        return false;
    }

    /**
     * Handle a lose condition (e.g., timeout).
     */
    public void handleLose(int losingPlayer, String reason) {
        if (!gameEnded) {
            gameEnded = true;
            String loser = (losingPlayer == 1) ? "Black" : "White";
            String winner = loser.equals("Black") ? "White" : "Black";
            String message = loser + " lose! " + winner + " win!";
            gameLog.add(message);

            if (eventListener != null) {
                eventListener.onGameLost(losingPlayer, reason);
            }
        }
    }

    /**
     * Attempt to withdraw the last move.
     * 
     * @return true if withdraw was successful
     */
    public boolean withdrawMove() {
        if (withdrawsRemaining == 0) {
            return false;
        }

        if (gameState.withdrawMove()) {
            if (withdrawsRemaining > 0) {
                withdrawsRemaining--;
            }

            int row = lastMoveRow;
            int col = lastMoveCol;
            lastMoveRow = -1;
            lastMoveCol = -1;

            if (eventListener != null) {
                eventListener.onWithdrawApproved(row, col);
            }
            return true;
        }

        return false;
    }

    /**
     * Log a withdraw request.
     */
    public void logWithdrawRequest(int player) {
        String playerColor = player == 1 ? "Black" : "White";
        gameLog.add(playerColor + " REQUEST WITHDRAW");
    }

    /**
     * Log withdraw result.
     */
    public void logWithdrawResult(boolean approved) {
        gameLog.add("REQUEST RESULT: " + (approved ? "OK" : "DENIED"));
    }

    /**
     * Reset the session for a new game.
     */
    public void restart() {
        gameLog.addBlank();
        gameLog.add("RESTART AT " + ZonedDateTime.now());

        gameStarted = false;
        gameEnded = false;
        lastMoveRow = -1;
        lastMoveCol = -1;

        // Reset game state
        // Note: GameState needs a reset method, or we create a new instance
        // For now, we keep reference to the same GameState

        if (eventListener != null) {
            eventListener.onGameRestarting();
        }
    }

    /**
     * Complete the restart process.
     */
    public void completeRestart() {
        if (eventListener != null) {
            eventListener.onGameRestarted();
        }
    }

    // Getters

    public GameState getGameState() {
        return gameState;
    }

    public GameLog getGameLog() {
        return gameLog;
    }

    public GameConfig getConfig() {
        return config;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public boolean isGameEnded() {
        return gameEnded;
    }

    public int getLastMoveRow() {
        return lastMoveRow;
    }

    public int getLastMoveCol() {
        return lastMoveCol;
    }

    public int getWithdrawsRemaining() {
        return withdrawsRemaining;
    }

    public int getCurrentPlayer() {
        return gameState.getCurrentPlayer();
    }
}
