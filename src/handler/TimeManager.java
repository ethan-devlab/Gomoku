package handler;

import ui.GameBoardComponent;

import javax.swing.*;

/**
 * Manages turn and player timers for the game.
 * Refactored to emit events via TimeEventListener instead of directly
 * manipulating UI labels or sending network messages.
 */
public class TimeManager {
    private final GameBoardComponent gameBoardComponent;
    private Timer turnTimer;
    private Timer playerTimer;
    private int TURNTIME;
    private int turnTimeLeft;
    private double playerTimeLeft;
    private final JLabel p1TurnTime;
    private final JLabel p2TurnTime;
    private final JLabel p1PlayerTime;
    private final JLabel p2PlayerTime;

    // Event listener for decoupled notification
    private TimeEventListener timeEventListener;

    public TimeManager(GameBoardComponent gameBoardComponent, JLabel p1TurnTime, JLabel p2TurnTime,
            JLabel p1PlayerTime, JLabel p2PlayerTime) {
        this.gameBoardComponent = gameBoardComponent;
        this.p1TurnTime = p1TurnTime;
        this.p2TurnTime = p2TurnTime;
        this.p1PlayerTime = p1PlayerTime;
        this.p2PlayerTime = p2PlayerTime;

        turnTimer = new Timer(1000, e -> {
            if (turnTimeLeft > 0) {
                turnTimeLeft--;

                // Notify listener (if set) instead of directly using controller
                if (timeEventListener != null) {
                    timeEventListener.onTurnTimeUpdate(turnTimeLeft);
                    timeEventListener.onTimeMessage(GameCommand.TURN_TIME, String.valueOf(turnTimeLeft));
                } else {
                    // Fallback to legacy behavior
                    String message = GameFlags.TURN_TIME + ":" + turnTimeLeft;
                    gameBoardComponent.controller.sendMessage(message);
                }
                updateTurnTimeLabel();
            } else {
                gameBoardComponent.setCanPlay(false);
                int player = gameBoardComponent.getPlayer();

                if (timeEventListener != null) {
                    timeEventListener.onTurnTimeExpired(player);
                    timeEventListener.onTimeoutLoss(player);
                } else {
                    // Fallback to legacy behavior
                    gameBoardComponent.controller.sendMessage(GameFlags.LOSE + ":" + player);
                }
                showTimeout("TimeOut! You lose!");
                stopAll();
            }
        });

        playerTimer = new Timer(1000, e -> {
            if (playerTimeLeft > 0) {
                gameBoardComponent.setPlayerTime(--playerTimeLeft);

                // Notify listener (if set) instead of directly using controller
                if (timeEventListener != null) {
                    timeEventListener.onPlayerTimeUpdate(playerTimeLeft);
                    timeEventListener.onTimeMessage(GameCommand.PLAYER_TIME, String.valueOf(playerTimeLeft));
                } else {
                    // Fallback to legacy behavior
                    String message = GameFlags.PLAYER_TIME + ":" + playerTimeLeft;
                    gameBoardComponent.controller.sendMessage(message);
                }
                updatePlayerTimeLabel();
            } else {
                gameBoardComponent.setCanPlay(false);
                int player = gameBoardComponent.getPlayer();

                if (timeEventListener != null) {
                    timeEventListener.onPlayerTimeExpired(player);
                    timeEventListener.onTimeoutLoss(player);
                } else {
                    // Fallback to legacy behavior
                    gameBoardComponent.controller.sendMessage(GameFlags.LOSE + ":" + player);
                }
                showTimeout("TimeOut! You lose!");
                stopAll();
            }
        });
    }

    /**
     * Set the event listener for timer events.
     * When set, events will be emitted instead of directly calling controller
     * methods.
     */
    public void setTimeEventListener(TimeEventListener listener) {
        this.timeEventListener = listener;
    }

    private String formatTime(double timeInSeconds) {
        return String.format("%dm %ds", (int) timeInSeconds / 60, (int) timeInSeconds % 60);
    }

    private void updateTurnTimeLabel() {
        JLabel activeLabel = (gameBoardComponent.getPlayer() == 1)
                ? (gameBoardComponent.getCanPlay() ? p1TurnTime : p2TurnTime)
                : (gameBoardComponent.getCanPlay() ? p2TurnTime : p1TurnTime);
        activeLabel.setText(String.format("%ds", turnTimeLeft));
    }

    private void updatePlayerTimeLabel() {
        JLabel activeLabel = (gameBoardComponent.getPlayer() == 1)
                ? (gameBoardComponent.getCanPlay() ? p1PlayerTime : p2PlayerTime)
                : (gameBoardComponent.getCanPlay() ? p2PlayerTime : p1PlayerTime);

        activeLabel.setText(formatTime(playerTimeLeft));
    }

    public void resetTurnTime(boolean isSelf) {
        turnTimeLeft = TURNTIME;
        JLabel activeLabel = (gameBoardComponent.getPlayer() == 1) ? p1TurnTime : p2TurnTime;
        activeLabel.setText(String.format("%ds", turnTimeLeft));
        if (isSelf) { // reset opponent turn time
            activeLabel = (gameBoardComponent.getPlayer() == 1) ? p2TurnTime : p1TurnTime;
            activeLabel.setText(String.format("%ds", turnTimeLeft));
        }
    }

    public void startTurnTimer(int turnTime) {
        this.turnTimeLeft = turnTime;
        updateTurnTimeLabel();
        turnTimer.start();
    }

    public void startPlayerTimer(double playerTime) {
        this.playerTimeLeft = playerTime;
        updatePlayerTimeLabel();
        playerTimer.start();
    }

    public void stopTurnTimer() {
        turnTimer.stop();
    }

    public void stopPlayerTimer() {
        playerTimer.stop();
    }

    public void stopAll() {
        stopTurnTimer();
        stopPlayerTimer();
    }

    public void setTurnTime(int turnTime) {
        this.turnTimeLeft = turnTime;
        updateTurnTimeLabel();
    }

    public void setPlayerTime(double playerTime) {
        this.playerTimeLeft = playerTime;
        updatePlayerTimeLabel();
    }

    public void setConstantTime(int constantTime) {
        TURNTIME = constantTime;
    }

    public void showTimeout(String message) {
        JOptionPane.showMessageDialog(null, message, "Timeout", JOptionPane.WARNING_MESSAGE);
    }

    // Getters for current time values

    public int getTurnTimeLeft() {
        return turnTimeLeft;
    }

    public double getPlayerTimeLeft() {
        return playerTimeLeft;
    }

    public int getConstantTime() {
        return TURNTIME;
    }
}