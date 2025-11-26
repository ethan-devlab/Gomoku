package handler;

import ui.GameUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Handles withdraw button clicks.
 * Refactored to use typed GameCommand for cleaner protocol handling.
 */
public class WithdrawHandler implements ActionListener {

    private final GameUI gameUI;
    private final DialogService dialogService;

    public WithdrawHandler(GameUI gameUI) {
        this(gameUI, new SwingDialogService());
    }

    public WithdrawHandler(GameUI gameUI, DialogService dialogService) {
        this.gameUI = gameUI;
        this.dialogService = dialogService;
    }

    public void actionPerformed(ActionEvent e) {
        String count = this.gameUI.getWithdrawCount() != -1 ? String.valueOf(this.gameUI.getWithdrawCount()) : "∞";
        boolean confirmed = dialogService.showConfirm("Withdraw",
                "Do you want to withdraw? Withdraw Remaining: " + count);

        if (confirmed) {
            System.out.println("Withdraw!");
            if (gameUI.gameBoardComponent.getCanPlay()) {
                dialogService.showWarning("Withdraw", "Withdraw DENIED");
                return;
            }
            if (gameUI.getWithdrawCount() > 0 || gameUI.getWithdrawCount() == -1) {
                gameUI.controller.sendMessage(GameFlags.WITHDRAW);
                String player = gameUI.getPlayerFlag() == 1 ? "Black" : "White";
                gameUI.controller.addGameData(player + " REQUEST WITHDRAW");
            } else {
                dialogService.showWarning("Withdraw", "No more withdrawal allowed.");
            }
        }
    }

}
