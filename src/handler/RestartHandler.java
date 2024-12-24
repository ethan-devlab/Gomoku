package handler;

import ui.GameUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RestartHandler implements ActionListener{

    private final GameUI gameUI;

    public RestartHandler(GameUI gameUI) {
        this.gameUI = gameUI;
    }

    public void actionPerformed(ActionEvent e) {
        int res = JOptionPane.showConfirmDialog(null, "Your game will be restarted!",
                "Restart", JOptionPane.YES_NO_OPTION);

        if (res == JOptionPane.YES_OPTION) {
            System.out.println("Game Restart!");
            gameUI.controller.processMessage(GameFlags.RESTART_INIT);
            gameUI.controller.sendMessage(GameFlags.RESTART_INIT);
        }
    }

}
