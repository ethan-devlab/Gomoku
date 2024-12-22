package handler;
import ui.GameBoardComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class ButtonHandler implements ActionListener {
    private final GameBoardComponent gameBoardComponent;
    private final String RED_FLAG_URL = "/resources/image/redflag.png";
    private final int ICON_WIDTH = 30;
    private final int ICON_HEIGHT = 30;
    private boolean isBlackTurn;
    private final Image flagImg;
    private final ImageIcon flagIcon;
    private final JLabel p1Flag, p2Flag;
    private boolean isWIN;


    public ButtonHandler(GameBoardComponent gameBoardComponent) {
        this.gameBoardComponent = gameBoardComponent;
        isBlackTurn = gameBoardComponent.isBlackTurn;
        flagIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource(RED_FLAG_URL)));
        flagImg = flagIcon.getImage().getScaledInstance(ICON_WIDTH, ICON_HEIGHT, Image.SCALE_SMOOTH);
        p1Flag = gameBoardComponent.p1Flag;
        p2Flag = gameBoardComponent.p2Flag;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton) e.getSource();
        int[] loc = (int []) button.getClientProperty("loc");
        System.out.println(loc[0] + " " + loc[1]);
        if (button.getIcon() == null) {
            ImageIcon icon;
            if (isBlackTurn) {
                icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/resources/image/black.png")));
                p2Flag.setIcon(new ImageIcon(flagImg));
                p1Flag.setIcon(null);
            }
            else {
                icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/resources/image/white.png")));
                p1Flag.setIcon(new ImageIcon(flagImg));
                p2Flag.setIcon(null);
            }
            Image img = icon.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(img));
            isBlackTurn = !isBlackTurn;
        }
    }

    private boolean checkWin() {

        return true;
    }
}
