package handler;

import ui.GameBoardComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RestartHandler implements ActionListener{

    private JPanel gameBoardPanel;
    private JLabel p1Flag, p2Flag;

    public RestartHandler(JPanel GameBoardPanel, JLabel p1Flag, JLabel p2Flag) {
        gameBoardPanel = GameBoardPanel;
        this.p1Flag = p1Flag;
        this.p2Flag = p2Flag;
    }

    public void actionPerformed(ActionEvent e) {
        int res = JOptionPane.showConfirmDialog(null, "Your game will be restarted!",
                "Restart", JOptionPane.YES_NO_OPTION);

        if (res == JOptionPane.YES_OPTION) {
            System.out.println("Game Restart!");
            gameBoardPanel.removeAll();
            gameBoardPanel.setLayout(new GridLayout());
            gameBoardPanel.add(new GameBoardComponent(p1Flag, p2Flag));
            gameBoardPanel.updateUI();
        }
    }

}
