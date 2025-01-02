package handler;

import ui.GameUI;
import ui.LoggerUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Path;

public class StartHandler implements ActionListener {

    private final GameUI gameUI;
    private final JButton startButton;
    public StartHandler(GameUI gameUI, JButton startButton) {
        this.gameUI = gameUI;
        this.startButton = startButton;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Gomoku Game");
            Path currentRelativePath = Path.of("");
            String s = currentRelativePath.toAbsolutePath().toString();
            String imageUrl = s + "\\src\\resources\\image\\icon.png";
            File file = new File(imageUrl);
            if (!file.exists()) {
                imageUrl = s + "\\resources\\image\\icon.png";
            }
            frame.setIconImage(Toolkit.getDefaultToolkit().getImage(imageUrl));
            gameUI.setCurrentFrame(frame);
            gameUI.setGameStarted(true);
            gameUI.gameBoardComponent.setCanPlay(gameUI.gameBoardComponent.getCanPlay());
            LoggerUI loggerUI = new LoggerUI();
            loggerUI.setIconImage(Toolkit.getDefaultToolkit().getImage(imageUrl));
            JMenuBar menuBar = new JMenuBar();
            JMenu menu = new JMenu("Menu");
            JMenuItem infoUI = new JMenuItem("Game Info");
            infoUI.addActionListener(_ -> loggerUI.setVisible(true));
            gameUI.setLoggerUI(loggerUI);
            gameUI.controller.setIsLoggerInit(true);
            gameUI.setDataList(gameUI.controller.getGameDataList());
            frame.setContentPane(gameUI);
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.setSize(800, 800);
            menu.add(infoUI);
            menuBar.add(menu);
            frame.setJMenuBar(menuBar);
            frame.setLocationRelativeTo(null); // make center
            frame.setVisible(true);
            startButton.setEnabled(false);
        });
    }

}
