package handler;

import ui.GameUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Handles restart button clicks.
 * Refactored to use DialogService for testability.
 */
public class RestartHandler implements ActionListener {

    private final GameUI gameUI;
    private final DialogService dialogService;

    public RestartHandler(GameUI gameUI) {
        this(gameUI, new SwingDialogService());
    }

    public RestartHandler(GameUI gameUI, DialogService dialogService) {
        this.gameUI = gameUI;
        this.dialogService = dialogService;
    }

    public void actionPerformed(ActionEvent e) {
        boolean confirmed = dialogService.showConfirm("Restart", "Your game will be restarted!");

        if (confirmed) {
            System.out.println("Game Restart!");
            gameUI.controller.processMessage(GameFlags.RESTART_INIT);
            gameUI.controller.sendMessage(GameFlags.RESTART_INIT);
            gameUI.controller.processMessage(GameFlags.RESTART);
            gameUI.controller.sendMessage(GameFlags.RESTART);
        }
    }

}
