package handler;

import ui.GameUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartHandler implements ActionListener {

    private final GameUI gameUI;
    public StartHandler(GameUI gameUI) {
        this.gameUI = gameUI;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Gomoku Game");
            gameUI.setCurrentFrame(frame);
            gameUI.setGameStarted(true);
            gameUI.gameBoardComponent.setCanPlay(gameUI.gameBoardComponent.getCanPlay());
            frame.setContentPane(gameUI);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(800, 800);
            frame.setLocationRelativeTo(null); // make center
            frame.setVisible(true);
        });
    }

}
