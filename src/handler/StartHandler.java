package handler;

import ui.GameUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartHandler implements ActionListener {

    public StartHandler() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("GameUI");
            GameUI gameUI = new GameUI();
            frame.setContentPane(gameUI);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(800, 800);
            frame.setLocationRelativeTo(null); // make center
            frame.setVisible(true);
        });
    }

}
