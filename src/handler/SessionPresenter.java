package handler;

import ui.GameUI;
import ui.InitialUI;

import javax.swing.*;

/**
 * SessionPresenter translates game session events into UI updates.
 * Acts as a bridge between the game logic and the Swing UI components.
 */
public class SessionPresenter implements GameEventListener {

    private final InitialUI initUI;
    private final GameUI gameUI;
    private final DialogService dialogService;

    public SessionPresenter(InitialUI initUI, GameUI gameUI) {
        this(initUI, gameUI, new SwingDialogService());
    }

    public SessionPresenter(InitialUI initUI, GameUI gameUI, DialogService dialogService) {
        this.initUI = initUI;
        this.gameUI = gameUI;
        this.dialogService = dialogService;
    }

    @Override
    public void onGameInitialized(GameConfig config) {
        SwingUtilities.invokeLater(() -> {
            gameUI.setPlayerFlag(config.playerFlag());
            gameUI.setPlayerName(config.playerFlag(), config.playerName());
            gameUI.setPlayerName(config.opponentFlag(), config.opponentName());
            gameUI.setTurnTime(String.valueOf(config.turnTime()));
            gameUI.setPlayerTime(String.valueOf(config.playerTime()));
            gameUI.setFirstPlayer(config.firstPlayer());
            gameUI.setWithdrawCount(config.withdrawCount());
            gameUI.setWinRound(-1);

            gameUI.gameBoardComponent.setIsBlack(config.isBlack());
            gameUI.gameBoardComponent.setPlayer();
            gameUI.gameBoardComponent.setTurnTime(config.turnTime());
            gameUI.gameBoardComponent.setPlayerTime(config.getPlayerTimeSeconds());
            gameUI.gameBoardComponent.setConstantTime(config.turnTime());
        });
    }

    @Override
    public void onGameStarted() {
        SwingUtilities.invokeLater(() -> {
            gameUI.setGameStarted(true);
        });
    }

    @Override
    public void onMoveMade(int player, int row, int col) {
        SwingUtilities.invokeLater(() -> {
            gameUI.gameBoardComponent.updateGameBoard(player, row, col);
            gameUI.gameBoardComponent.setCanPlay(false);
        });
    }

    @Override
    public void onGameWon(int player, String playerColor) {
        SwingUtilities.invokeLater(() -> {
            gameUI.setWinRound(player);
            gameUI.gameBoardComponent.setCanPlay(false);
            dialogService.showInfo("Info", playerColor + " wins!");
        });
    }

    @Override
    public void onGameTied() {
        SwingUtilities.invokeLater(() -> {
            gameUI.gameBoardComponent.setCanPlay(false);
            dialogService.showInfo("Info", "Tie Game!");
        });
    }

    @Override
    public void onGameLost(int player, String reason) {
        SwingUtilities.invokeLater(() -> {
            String loser = (player == 1) ? "Black" : "White";
            gameUI.gameBoardComponent.setCanPlay(false);
            dialogService.showInfo("Info", loser + " lose! You win!");
        });
    }

    @Override
    public void onPatternDetected(String pattern) {
        SwingUtilities.invokeLater(() -> {
            dialogService.showInfo("Info", pattern);
        });
    }

    @Override
    public void onWithdrawRequested(int player) {
        // No direct UI update needed, handled by game flow
    }

    @Override
    public void onWithdrawApproved(int row, int col) {
        SwingUtilities.invokeLater(() -> {
            if (row != -1 && col != -1) {
                gameUI.gameBoardComponent.clearPosition(row, col);
            }
        });
    }

    @Override
    public void onWithdrawDenied() {
        SwingUtilities.invokeLater(() -> {
            dialogService.showInfo("Info", "Withdraw Denied");
        });
    }

    @Override
    public void onGameRestarting() {
        SwingUtilities.invokeLater(() -> {
            gameUI.gameBoardComponent.stopTimer();
            gameUI.gameBoardComponent.setCanPlay(false);
        });
    }

    @Override
    public void onGameRestarted() {
        SwingUtilities.invokeLater(() -> {
            gameUI.gameBoardComponent.clearButtonIcons();
        });
    }

    @Override
    public void onTurnTimeUpdated(int timeLeft) {
        SwingUtilities.invokeLater(() -> {
            gameUI.gameBoardComponent.updateTurnTime(timeLeft);
        });
    }

    @Override
    public void onPlayerTimeUpdated(double timeLeft) {
        SwingUtilities.invokeLater(() -> {
            gameUI.gameBoardComponent.updatePlayerTime(timeLeft);
        });
    }

    @Override
    public void onCanPlay(boolean canPlay) {
        SwingUtilities.invokeLater(() -> {
            gameUI.gameBoardComponent.setCanPlay(canPlay);
        });
    }

    @Override
    public void onConnectionClosed() {
        SwingUtilities.invokeLater(() -> {
            gameUI.gameBoardComponent.clearButtonIcons();
            gameUI.gameBoardComponent.setCanPlay(false);
            gameUI.setGameStarted(false);

            if (gameUI.getCurrentFrame() != null) {
                initUI.setPlayButtonEnable(false);
                initUI.setClientState(false);
                gameUI.getCurrentFrame().dispose();
            }
        });
    }

    @Override
    public void onMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            dialogService.showInfo("Info", message);
        });
    }

    @Override
    public void onLogEntry(String entry) {
        // Log entries are managed by GameLog, no direct UI action needed here
    }

    /**
     * Update the lobby UI for server disconnect.
     */
    public void onServerDisconnected() {
        SwingUtilities.invokeLater(() -> {
            initUI.getStartServerBtn().setText("Start Server");
            initUI.getConnectBtn().setEnabled(true);
            initUI.setStatusText("Disconnected and waiting for connection.");
        });
    }

    /**
     * Update the lobby UI for client disconnect.
     */
    public void onClientDisconnected() {
        SwingUtilities.invokeLater(() -> {
            initUI.getConnectBtn().setText("Connect");
            initUI.getStartServerBtn().setEnabled(true);
            initUI.setStatusText("Disconnected and waiting for connection.");
        });
    }

    // Getters for components (for handlers that need direct access)

    public InitialUI getInitUI() {
        return initUI;
    }

    public GameUI getGameUI() {
        return gameUI;
    }
}
