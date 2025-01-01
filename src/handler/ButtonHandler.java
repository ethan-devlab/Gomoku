package handler;
import ui.GameBoardComponent;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonHandler implements ActionListener {
    private final GameBoardComponent gameBoardComponent;
    private final String MOVE_PREFIX = "MOVE:";

    public ButtonHandler(GameBoardComponent gameBoardComponent) {
        this.gameBoardComponent = gameBoardComponent;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton) e.getSource();
        int[] loc = (int []) button.getClientProperty("loc");
        System.out.println(loc[0] + " " + loc[1]);
        if (gameBoardComponent.canPlay) {
            if (button.getIcon() == null) {
                String message = MOVE_PREFIX + gameBoardComponent.getPlayer() + "|" + loc[0] + "|" + loc[1];
                gameBoardComponent.controller.sendMessage(message);
                gameBoardComponent.controller.processMessage(message);
            }
        }
    }
}
