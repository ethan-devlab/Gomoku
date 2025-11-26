package handler;

import ui.GameUI;
import ui.LoggerUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Handles the play button click to start the game UI.
 * Refactored to use ResourceLoader for icon management.
 */
public class StartHandler implements ActionListener {

    private final GameUI gameUI;
    private final JButton startButton;
    private final ResourceLoader resourceLoader;

    public StartHandler(GameUI gameUI, JButton startButton) {
        this.gameUI = gameUI;
        this.startButton = startButton;
        this.resourceLoader = new ResourceLoader();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Gomoku Game");

            // Use ResourceLoader for icon loading
            Image appIcon = resourceLoader.getAppIcon();
            if (appIcon != null) {
                frame.setIconImage(appIcon);
            }

            gameUI.setCurrentFrame(frame);
            gameUI.setGameStarted(true);
            gameUI.gameBoardComponent.setCanPlay(gameUI.gameBoardComponent.getCanPlay());

            LoggerUI loggerUI = new LoggerUI();
            if (appIcon != null) {
                loggerUI.setIconImage(appIcon);
            }

            JMenuBar menuBar = new JMenuBar();
            JMenu menu = new JMenu("Menu");
            JMenuItem infoUI = new JMenuItem("Game Info");
            infoUI.addActionListener(evt -> loggerUI.setVisible(true));
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
