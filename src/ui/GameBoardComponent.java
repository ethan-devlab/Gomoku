package ui;

import handler.ButtonHandler;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class GameBoardComponent extends JPanel {
    private final int ROW = 15;
    private final int COL = 15;
    private final String RED_FLAG_URL = "/resources/image/redflag.png";
    protected JButton[][] buttons;
    public boolean isBlackTurn;
    public JLabel p1Flag, p2Flag;
    private ImageIcon flagIcon;
    private Image flagImg;
    private static final int ICON_WIDTH = 30;
    private static final int ICON_HEIGHT = 30;
    private final ButtonHandler buttonHandler;


    public GameBoardComponent(JLabel p1Flag, JLabel p2Flag) {
        setLayout(null);
        isBlackTurn = true;
        buttons = new JButton[ROW][COL];
        this.p1Flag = p1Flag;
        this.p2Flag = p2Flag;
        buttonHandler = new ButtonHandler(this);
        initializeButtons();
        initializeIcon();
    }

    private void initializeIcon() {
        flagIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource(RED_FLAG_URL)));
        flagImg = flagIcon.getImage().getScaledInstance(ICON_WIDTH, ICON_HEIGHT, Image.SCALE_SMOOTH);
        this.p2Flag.setIcon(null);
        this.p1Flag.setIcon(null);
        this.p1Flag.setIcon(new ImageIcon(flagImg));
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

                button.putClientProperty("loc", new int[] {i, j});

//                button.addActionListener(e -> handleButtonClick(row, col));
                button.addActionListener(buttonHandler);

                buttons[i][j] = button;
                add(button);
            }
        }
    }

    @Override
    public void doLayout() {
        super.doLayout();
        int size = Math.min(getWidth(), getHeight()) / (ROW + 1);
        int x0 = (getWidth() - (COL - 1) * size) / 2;
        int y0 = (getHeight() - (ROW - 1) * size) / 2;

        // Position buttons on intersections
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                JButton button = buttons[i][j];
                button.setLocation(
                x0 + j * size - button.getWidth() / 2,
                y0 + i * size - button.getHeight() / 2
                );
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int size = Math.min(getWidth(), getHeight()) / (ROW + 1);
        int x0 = (getWidth() - (COL - 1) * size) / 2;
        int y0 = (getHeight() - (ROW - 1) * size) / 2;

        g.setColor(Color.BLACK);
        // Draw horizontal lines
        for (int i = 0; i < ROW; i++) {
            g.drawLine(x0, y0 + i * size, x0 + (COL - 1) * size, y0 + i * size);
        }
        // Draw vertical lines
        for (int i = 0; i < COL; i++) {
            g.drawLine(x0 + i * size, y0, x0 + i * size, y0 + (ROW - 1) * size);
        }
    }
}
