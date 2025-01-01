package handler;

import ui.GameUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WithdrawHandler implements ActionListener{

    private final GameUI gameUI;

    public WithdrawHandler(GameUI gameUI) {
        this.gameUI = gameUI;
    }

    public void actionPerformed(ActionEvent e) {
        String count = this.gameUI.getWithdrawCount() != -1 ? String.valueOf(this.gameUI.getWithdrawCount()) : "∞";
        int res = JOptionPane.showConfirmDialog(null,
                "Do you want to withdraw? Withdraw Remaining: " + count,
                "Withdraw", JOptionPane.YES_NO_OPTION);

        if (res == JOptionPane.YES_OPTION) {
            System.out.println("Withdraw!");
            if (gameUI.gameBoardComponent.getCanPlay()) {
                JOptionPane.showMessageDialog(null, "Withdraw DENIED", "Withdraw",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (gameUI.getWithdrawCount() > 0 || gameUI.getWithdrawCount() == -1) {
                gameUI.controller.sendMessage(GameFlags.WITHDRAW);
                String player = gameUI.getPlayerFlag() == 1 ? "Black" : "White";
                gameUI.controller.addGameData(player + " REQUEST WITHDRAW");
            }
            else {
                JOptionPane.showMessageDialog(null, "No more withdrawal allowed.", "Withdraw",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }

}
