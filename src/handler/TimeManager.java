package handler;

import ui.GameBoardComponent;
import ui.GameUI;

import javax.swing.*;

public class TimeManager {
    private final GameUI gameUI;
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
    
    public TimeManager(GameBoardComponent gameBoardComponent, JLabel p1TurnTime, JLabel p2TurnTime,
                       JLabel p1PlayerTime, JLabel p2PlayerTime, GameUI gameUI) {
        this.gameUI = gameUI;
        this.gameBoardComponent = gameBoardComponent;
        this.p1TurnTime = p1TurnTime;
        this.p2TurnTime = p2TurnTime;
        this.p1PlayerTime = p1PlayerTime;
        this.p2PlayerTime = p2PlayerTime;

        turnTimer = new Timer(1000, _ -> {
            if (turnTimeLeft > 0) {
                turnTimeLeft--;
                String message = GameFlags.TURN_TIME + ":" + turnTimeLeft;
                gameBoardComponent.controller.sendMessage(message);
                updateTurnTimeLabel();
            } else {
                gameBoardComponent.setCanPlay(false);
                gameBoardComponent.controller.sendMessage(GameFlags.LOSE + ":" + gameBoardComponent.getPlayer());
                showTimeout("TimeOut! You lose!");
                gameUI.setWinRound(gameBoardComponent.getPlayer() == 1 ? 2 : 1);
                stopAll();
            }
        });

        playerTimer = new Timer(1000, _ -> {
            if (playerTimeLeft > 0) {
                gameBoardComponent.setPlayerTime(--playerTimeLeft);
//                double time = playerTimeLeft / 60.0 + playerTimeLeft % 60.0 / 10.0;
                String message = GameFlags.PLAYER_TIME + ":" + playerTimeLeft;
                gameBoardComponent.controller.sendMessage(message);
                updatePlayerTimeLabel();
            } else {
                gameBoardComponent.setCanPlay(false);
                gameBoardComponent.controller.sendMessage(GameFlags.LOSE + ":" + gameBoardComponent.getPlayer());
                showTimeout("TimeOut! You lose!");

                stopAll();
            }
        });
    }

    private String formatTime(double timeInSeconds) {
        return String.format("%dm %ds", (int) timeInSeconds / 60, (int) timeInSeconds % 60);
    }

    private void updateTurnTimeLabel() {
        JLabel activeLabel = (gameBoardComponent.getPlayer() == 1) ?
                (gameBoardComponent.getCanPlay() ? p1TurnTime : p2TurnTime) :
                (gameBoardComponent.getCanPlay() ? p2TurnTime : p1TurnTime);
        activeLabel.setText(String.format("%ds", turnTimeLeft));
    }

    private void updatePlayerTimeLabel() {
        JLabel activeLabel = (gameBoardComponent.getPlayer() == 1) ? 
            (gameBoardComponent.getCanPlay() ? p1PlayerTime : p2PlayerTime) :
            (gameBoardComponent.getCanPlay() ? p2PlayerTime : p1PlayerTime);
            
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

}