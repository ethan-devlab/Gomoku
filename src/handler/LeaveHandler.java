package handler;

import ui.GameUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LeaveHandler implements ActionListener{

    private final GameUI gameUI;

    public LeaveHandler(GameUI gameUI) {
        this.gameUI = gameUI;
    }

    public void actionPerformed(ActionEvent e) {
        int res = JOptionPane.showConfirmDialog(null, "Now leaving will make disconnected to server.",
                "Leave", JOptionPane.YES_NO_OPTION);

        if (res == JOptionPane.YES_OPTION) {
            System.out.println("Leave!");
            gameUI.controller.sendMessage(GameFlags.BYE);
            gameUI.controller.processMessage(GameFlags.BYE);
        }
    }

}
