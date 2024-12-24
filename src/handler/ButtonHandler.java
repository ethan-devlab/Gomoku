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
    private final String MOVE_PREFIX = "MOVE:";
    private final int ICON_WIDTH = 30;
    private final int ICON_HEIGHT = 30;
    private final Image flagImg;
    private final ImageIcon flagIcon;
    private final JLabel p1Flag, p2Flag;
    private boolean isWIN;
    private boolean isYourTurn;

    public ButtonHandler(GameBoardComponent gameBoardComponent) {
        this.gameBoardComponent = gameBoardComponent;
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
        if (gameBoardComponent.canPlay) {
            if (button.getIcon() == null) {
//                ImageIcon icon;
//                if (gameBoardComponent.isBlack) {
//                    icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/resources/image/black.png")));
//                } else {
//                    icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/resources/image/white.png")));
//                }
//                Image img = icon.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH);
//                button.setIcon(new ImageIcon(img));
//                gameBoardComponent.setFlagIcon();
//                gameBoardComponent.setCanPlay(false);
                String message = MOVE_PREFIX + gameBoardComponent.getPlayer() + "|" + loc[0] + "|" + loc[1];
                gameBoardComponent.controller.sendMessage(message);
                gameBoardComponent.controller.processMessage(message);
//                gameBoardComponent.controller.sendMessage(GameFlags.CAN_PLAY);
            }
        }
    }
}
