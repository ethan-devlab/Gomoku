package handler;

import ui.GameBoardComponent;

import javax.swing.*;
import java.awt.event.ActionListener;

public class TimeManager {
    private final GameBoardComponent gameBoardComponent;
    private Timer turnTimer;
    private Timer playerTimer;
    private int TURNTIME;
    private int turnTimeLeft;
    private int playerTimeLeft;
    private final JLabel p1TurnTime;
    private final JLabel p2TurnTime;
    private final JLabel p1PlayerTime;
    private final JLabel p2PlayerTime;
    
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
                String message = GameFlags.TURN_TIME + ":" + turnTimeLeft;
                gameBoardComponent.controller.sendMessage(message);
                updateTurnTimeLabel();
            } else {
                gameBoardComponent.setCanPlay(false);
                gameBoardComponent.controller.sendMessage(GameFlags.LOSE + ":" + gameBoardComponent.getPlayer());
                showTimeout("TimeOut! You lose!");
                stopTimer();
            }
        });

        playerTimer = new Timer(1000, e -> {
            if (playerTimeLeft > 0) {
                this.gameBoardComponent.setPlayerTime(--playerTimeLeft / 60 + playerTimeLeft % 60);
                String message = GameFlags.PLAYER_TIME + ":" + playerTimeLeft;
                gameBoardComponent.controller.sendMessage(message);
                updatePlayerTimeLabel();
            } else {
                gameBoardComponent.setCanPlay(false);
                gameBoardComponent.controller.sendMessage(GameFlags.LOSE + ":" + gameBoardComponent.getPlayer());
                showTimeout("TimeOut! You lose!");
                stopTimer();
            }
        });
    }

    private void updateTurnTimeLabel() {
        if (this.gameBoardComponent.getPlayer() == 1) {
            p1TurnTime.setText(String.format("%ds", turnTimeLeft));
        }
        else {
            p2TurnTime.setText(String.format("%ds", turnTimeLeft));
        }
    }

    private void updatePlayerTimeLabel() {
        if (this.gameBoardComponent.getPlayer() == 1) {
            p1PlayerTime.setText(String.format("%dm %ds", playerTimeLeft / 60, playerTimeLeft % 60));
        }
        else {
            p2PlayerTime.setText(String.format("%dm %ds", playerTimeLeft / 60, playerTimeLeft % 60));
        }
    }

    public void resetTurnTime() {
        turnTimeLeft = TURNTIME;
        updateTurnTimeLabel();
    }

    public void startTimer(int turnTime, int playerTime) {
        this.turnTimeLeft = turnTime;
        this.playerTimeLeft = playerTime;
        TURNTIME = turnTime;
        updateTurnTimeLabel();
        updatePlayerTimeLabel();
        turnTimer.start();
        playerTimer.start();
    }

    public void stopTimer() {
        turnTimer.stop();
        playerTimer.stop();
    }

    public void setTurnTime(int turnTime) {
        this.turnTimeLeft = turnTime;
        updateTurnTimeLabel();
    }

    public void setPlayerTime(int playerTime) {
        this.playerTimeLeft = playerTime;
        updatePlayerTimeLabel();
    }

    public void showTimeout(String message) {
        JOptionPane.showMessageDialog(null, message, "Timeout", JOptionPane.WARNING_MESSAGE);
    }
}