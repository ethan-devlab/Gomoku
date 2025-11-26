package ui;

import handler.ButtonHandler;
import handler.Controller;
import handler.MoveRequestListener;
import handler.TimeManager;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * The game board component that displays the 15x15 Gomoku grid.
 * Refactored to support MoveRequestListener for decoupled move handling.
 */
public class GameBoardComponent extends JPanel {
    private final GameUI gameUI;
    private final int ROW = 15;
    private final int COL = 15;
    private final String RED_FLAG_URL = "/resources/image/redflag.png";
    protected JButton[][] buttons;
    public boolean isBlack;
    public boolean canPlay;
    public JLabel p1Flag, p2Flag;
    private ImageIcon flagIcon;
    private Image flagImg;
    private static final int ICON_WIDTH = 30;
    private static final int ICON_HEIGHT = 30;
    private final ButtonHandler buttonHandler;

    private int player;
    private final TimeManager timeManager;
    private int turnTime;
    private double playerTime;

    // Legacy controller reference for backward compatibility
    public Controller controller;

    // New decoupled listener for move requests
    private MoveRequestListener moveRequestListener;

    public GameBoardComponent(GameUI gameUI, JLabel p1Flag, JLabel p2Flag, JLabel p1TurnTime, JLabel p2TurnTime,
            JLabel p1PlayerTime, JLabel p2PlayerTime) {
        setLayout(null);
        isBlack = true;
        buttons = new JButton[ROW][COL];
        this.gameUI = gameUI;
        this.p1Flag = p1Flag;
        this.p2Flag = p2Flag;
        buttonHandler = new ButtonHandler(this);
        this.timeManager = new TimeManager(this, p1TurnTime, p2TurnTime, p1PlayerTime, p2PlayerTime);
        initializeButtons();
        initializeIcon();
    }

    private void initializeIcon() {
        flagIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource(RED_FLAG_URL)));
        flagImg = flagIcon.getImage().getScaledInstance(ICON_WIDTH, ICON_HEIGHT, Image.SCALE_SMOOTH);
        setFlagIcon(1);
    }

    private void initializeButtons() {
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                JButton button = new JButton();
                button.setSize(40, 40);
                button.setOpaque(false);
                button.setContentAreaFilled(false);
                button.setBorderPainted(false);
                button.setFocusPainted(false);

                button.putClientProperty("loc", new int[] { i, j });

                // int finalJ = j;
                // int finalI = i;
                // button.addActionListener(e -> handleButtonClick(finalI, finalJ));
                button.addActionListener(buttonHandler);

                buttons[i][j] = button;
                add(button);
            }
        }
    }

    public void clearButtonIcons() {
        for (JButton[] row : buttons) {
            for (JButton button : row) {
                button.setIcon(null);
            }
        }
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    /**
     * Set the move request listener for decoupled move handling.
     * When set, move requests will be routed through this listener.
     */
    public void setMoveRequestListener(MoveRequestListener listener) {
        this.moveRequestListener = listener;
    }

    /**
     * Get the move request listener.
     */
    public MoveRequestListener getMoveRequestListener() {
        return moveRequestListener;
    }

    /**
     * Request a move at the given position.
     * Routes through MoveRequestListener if set, otherwise uses controller
     * directly.
     */
    public void requestMove(int row, int col) {
        if (canPlay) {
            JButton button = buttons[row][col];
            if (button.getIcon() == null) {
                if (moveRequestListener != null) {
                    moveRequestListener.onMoveRequested(player, row, col);
                } else if (controller != null) {
                    // Legacy behavior
                    String message = "MOVE:" + player + "|" + row + "|" + col;
                    controller.sendMessage(message);
                    controller.processMessage(message);
                }
            }
        }
    }

    public void setCanPlay(boolean canPlay) {
        this.canPlay = canPlay;

        if (!gameUI.getIsGameStarted()) {
            return;
        }

        boolean hasTurnTime = turnTime != -1;
        boolean hasPlayerTime = playerTime != -1;

        if (canPlay) {
            if (hasTurnTime) {
                timeManager.startTurnTimer(this.turnTime);
                timeManager.resetTurnTime(true);
            }
            if (hasPlayerTime) {
                timeManager.startPlayerTimer(this.playerTime);
            }
        } else {
            if (hasTurnTime) {
                timeManager.stopTurnTimer();
                timeManager.resetTurnTime(false);
            }
            if (hasPlayerTime) {
                timeManager.stopPlayerTimer();
            }
        }
    }

    public boolean getCanPlay() {
        return this.canPlay;
    }

    public void setIsBlack(boolean isBlack) {
        this.isBlack = isBlack;
    }

    public void setPlayer() {
        this.player = this.isBlack ? 1 : 2;
    }

    public int getPlayer() {
        return this.player;
    }

    public void setTurnTime(int turnTime) {
        this.turnTime = turnTime;
    }

    public void setPlayerTime(double playerTime) {
        this.playerTime = playerTime;
    }

    public void setConstantTime(int constantTime) {
        timeManager.setConstantTime(constantTime);
    }

    public void setFlagIcon(int player) {
        if (player == 1) {
            this.p2Flag.setIcon(null);
            this.p1Flag.setIcon(new ImageIcon(flagImg));
        } else {
            this.p1Flag.setIcon(null);
            this.p2Flag.setIcon(new ImageIcon(flagImg));
        }
    }

    public void clearPosition(int row, int col) {
        if (row != -1 && col != -1) {
            JButton button = this.buttons[row][col];
            button.setIcon(null);
        }
    }

    public void updateTurnTime(int turnTime) {
        timeManager.setTurnTime(turnTime);
    }

    public void updatePlayerTime(double playerTime) {
        timeManager.setPlayerTime(playerTime);
    }

    public void stopTimer() {
        timeManager.stopAll();
    }

    public void updateGameBoard(int player, int row, int col) {
        JButton button = buttons[row][col];

        if (button.getIcon() == null) {
            ImageIcon icon;
            if (player == 1) {
                icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/resources/image/black.png")));
            } else {
                icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/resources/image/white.png")));
            }
            Image img = icon.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(img));
            setFlagIcon(player == 1 ? 2 : 1);
        }
    }

    @Override
    public void doLayout() {
        super.doLayout();
        int size = Math.min(getWidth(), getHeight()) / (ROW + 2);
        int x0 = (getWidth() - (COL - 1) * size) / 2;
        int y0 = (getHeight() - (ROW - 1) * size) / 2 - size / 3;

        // Position buttons on intersections
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                JButton button = buttons[i][j];
                button.setLocation(
                        x0 + j * size - button.getWidth() / 2,
                        y0 + i * size - button.getHeight() / 2);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int size = Math.min(getWidth(), getHeight()) / (ROW + 2);
        int x0 = (getWidth() - (COL - 1) * size) / 2;
        int y0 = (getHeight() - (ROW - 1) * size) / 2 - size / 3;

        g.setColor(Color.BLACK);

        // Draw horizontal lines
        for (int i = 0; i < ROW; i++) {
            g.drawLine(x0, y0 + i * size, x0 + (COL - 1) * size, y0 + i * size);
            // Draw numbers 1-15 on the left side
            g.drawString(String.valueOf(i + 1), x0 - 40, y0 + i * size + 5);
        }

        // Draw vertical lines
        for (int i = 0; i < COL; i++) {
            g.drawLine(x0 + i * size, y0, x0 + i * size, y0 + (ROW - 1) * size);
            // Draw letters A-O at the bottom
            g.drawString(Character.toString((char) ('A' + i)), x0 + i * size - 5, y0 + ROW * size + 5);
        }
    }
}
