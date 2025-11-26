package handler;

import ui.GameUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Handles leave button clicks.
 * Refactored to use DialogService for testability.
 */
public class LeaveHandler implements ActionListener {

    private final GameUI gameUI;
    private final DialogService dialogService;

    public LeaveHandler(GameUI gameUI) {
        this(gameUI, new SwingDialogService());
    }

    public LeaveHandler(GameUI gameUI, DialogService dialogService) {
        this.gameUI = gameUI;
        this.dialogService = dialogService;
    }

    public void actionPerformed(ActionEvent e) {
        boolean confirmed = dialogService.showConfirm("Leave",
                "Leaving now will cause data lost and disconnection.");

        if (confirmed) {
            System.out.println("Leave!");
            gameUI.controller.sendMessage(GameFlags.BYE);
            gameUI.controller.processMessage(GameFlags.BYE);
        }
    }

}
