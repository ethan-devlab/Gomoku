package handler;

import ui.GameBoardComponent;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Handles button click events on the game board.
 * Refactored to use GameBoardComponent.requestMove() for decoupled handling.
 */
public class ButtonHandler implements ActionListener {
    private final GameBoardComponent gameBoardComponent;

    public ButtonHandler(GameBoardComponent gameBoardComponent) {
        this.gameBoardComponent = gameBoardComponent;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton) e.getSource();
        int[] loc = (int[]) button.getClientProperty("loc");
        System.out.println(loc[0] + " " + loc[1]);

        // Use the new requestMove method which handles routing
        gameBoardComponent.requestMove(loc[0], loc[1]);
    }
}
