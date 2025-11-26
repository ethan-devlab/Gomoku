package ui;

import handler.Controller;
import handler.LeaveHandler;
import handler.RestartHandler;
import handler.WithdrawHandler;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;


public class GameUI extends JPanel {
    private JPanel MainPanel;
    public GameBoardComponent gameBoardComponent;
    private LoggerUI loggerUI;
    private JButton withdrawButton;
    private JPanel GameBoardPanel;
    private JButton restartButton;
    private JButton leaveButton;
    private JLabel p1WinRound;
    private JLabel p2WinRound;
    private JLabel blackIcon;
    private JLabel whiteIcon;
    private JLabel p2NameLabel;
    private JLabel p1NameLabel;
    private JLabel pIcon1;
    private JLabel pIcon2;
    private JLabel p1PlayerTime;
    private JLabel p2PlayerTime;
    private JLabel p1Flag;
    private JLabel p2Flag;
    private JLabel p1TurnTime;
    private JLabel p2TurnTime;
    private JLabel hourGlassIcon;
    private JLabel clockIcon;
    private JPanel ControlPanel;
    private JPanel InfoPanel;
    private JLabel trophyIcon;

    // private Image backgroundImage;

    private JFrame currentFrame;

    private final String B_ICON_URL = "/resources/image/black.png";
    private final String W_ICON_URL = "/resources/image/white.png";
    private final String PLAYER_ICON_URL = "/resources/image/user.png";
    private final String HOURGLASS_URL = "/resources/image/hourglass.png";
    private final String CLOCK_URL = "/resources/image/clock.png";
    private final String TROPHY = "/resources/image/trophy-star.png";
    private final int ICON_WIDTH = 30;
    private final int ICON_HEIGHT = 30;
    private final int SMALL_ICON_WIDTH = 20;
    private final int SMALL_ICON_HEIGHT = 20;

    private RestartHandler restartHandler;
    private WithdrawHandler withdrawHandler;
    private LeaveHandler leaveHandler;

    private int playerFlag = 1;

    private int withdrawCount = -1;
    private int firstPlayer = 1;
    private int player1WinRound = 0;
    private int player2WinRound = 0;

    private boolean isGameStarted = false;

    public Controller controller;
    private ArrayList<String> gameDataList;

    public GameUI() {
        initInfoPanel();
        initUI();
        initHandler();
    }

    private void initInfoPanel() {
        ImageIcon blackIcon, whiteIcon, playerIcon, clockIcon, hourGlassIcon, trophyIcon;
        blackIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource(B_ICON_URL)));
        whiteIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource(W_ICON_URL)));
        playerIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource(PLAYER_ICON_URL)));
        hourGlassIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource(HOURGLASS_URL)));
        clockIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource(CLOCK_URL)));
        trophyIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource(TROPHY)));
        Image blackImg = blackIcon.getImage().getScaledInstance(ICON_WIDTH, ICON_HEIGHT, Image.SCALE_SMOOTH);
        Image whiteImg = whiteIcon.getImage().getScaledInstance(ICON_WIDTH, ICON_HEIGHT, Image.SCALE_SMOOTH);
        Image playerImg = playerIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        Image hourGlassImg = hourGlassIcon.getImage().getScaledInstance(SMALL_ICON_WIDTH, SMALL_ICON_HEIGHT, Image.SCALE_SMOOTH);
        Image clockImg = clockIcon.getImage().getScaledInstance(SMALL_ICON_WIDTH, SMALL_ICON_HEIGHT, Image.SCALE_SMOOTH);
        Image trophyImg = trophyIcon.getImage().getScaledInstance(SMALL_ICON_WIDTH, SMALL_ICON_HEIGHT, Image.SCALE_SMOOTH);
        this.blackIcon.setIcon(new ImageIcon(blackImg));
        this.whiteIcon.setIcon(new ImageIcon(whiteImg));
        this.pIcon1.setIcon(new ImageIcon(playerImg));
        this.pIcon2.setIcon(new ImageIcon(playerImg));
        this.hourGlassIcon.setIcon(new ImageIcon(hourGlassImg));
        this.clockIcon.setIcon(new ImageIcon(clockImg));
        this.trophyIcon.setIcon(new ImageIcon(trophyImg));
        this.blackIcon.setText("");
        this.whiteIcon.setText("");
        this.pIcon1.setText("");
        this.pIcon2.setText("");
        this.p1Flag.setText("");
        this.p2Flag.setText("");
        this.hourGlassIcon.setText("");
        this.clockIcon.setText("");
        this.trophyIcon.setText("");
        this.p1WinRound.setText(String.valueOf(player1WinRound));
        this.p2WinRound.setText(String.valueOf(player2WinRound));
    }

    private void initHandler() {
        restartHandler = new RestartHandler(this);
        restartButton.addActionListener(restartHandler);
        withdrawHandler = new WithdrawHandler(this);
        withdrawButton.addActionListener(withdrawHandler);
        leaveHandler = new LeaveHandler(this);
        leaveButton.addActionListener(leaveHandler);
    }

    private void initUI() {
        setLayout(new BorderLayout());
        add(MainPanel, BorderLayout.CENTER);
        GameBoardPanel.setLayout(new GridLayout());
        gameBoardComponent = new GameBoardComponent(this, this.p1Flag, this.p2Flag, this.p1TurnTime, this.p2TurnTime,
                this.p1PlayerTime, this.p2PlayerTime);
        GameBoardPanel.add(gameBoardComponent);
    }

    public void setCurrentFrame(JFrame frame) {
        this.currentFrame = frame;
    }

    public JFrame getCurrentFrame() {
        return this.currentFrame;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setLoggerUI(LoggerUI logger) {
        this.loggerUI = logger;
    }

    public void setDataList(ArrayList<String> dataList) {
        gameDataList = dataList;
        this.loggerUI.clear();
        this.loggerUI.appendText(gameDataList);
    }

    public void setPlayerName(String flag, String playerName) {
        if (flag.equals("1")) {
            this.p1NameLabel.setText(playerName);
        } else {
            this.p2NameLabel.setText(playerName);
        }
    }

    public void setPlayerFlag(String flag) {
        this.playerFlag = Integer.parseInt(flag);
    }

    public int getPlayerFlag() {
        return this.playerFlag;
    }

    public void setFirstPlayer(int firstPlayer) {
        if (firstPlayer == 1) {  // means the first player is BLACK
            gameBoardComponent.setIsBlack(true);
            gameBoardComponent.setFlagIcon(1);
        } else {
            gameBoardComponent.setIsBlack(false);
            gameBoardComponent.setFlagIcon(2);
        }
        this.firstPlayer = firstPlayer;
    }

    public int getFirstPlayer() {
        return this.firstPlayer;
    }

    public void setTurnTime(String turnTime) {
        int turn = Integer.parseInt(turnTime);
        if (turn != -1) {
            this.p1TurnTime.setText(String.format("%ss", turnTime));
            this.p2TurnTime.setText(String.format("%ss", turnTime));
            return;
        }
        this.p1TurnTime.setText("∞");
        this.p2TurnTime.setText("∞");
    }

    public void setPlayerTime(String playerTime) {
        int playerT = Integer.parseInt(playerTime);
        if (playerT != -1) {
            this.p1PlayerTime.setText(String.format("%sm 0s", playerTime));
            this.p2PlayerTime.setText(String.format("%sm 0s", playerTime));
            return;
        }
        this.p1PlayerTime.setText("∞");
        this.p2PlayerTime.setText("∞");
    }


    public void setGameStarted(boolean isStarted) {
        this.isGameStarted = isStarted;
    }

    public boolean getIsGameStarted() {
        return this.isGameStarted;
    }

    public void setWithdrawCount(String withdrawCount) {
        this.setWithdrawCount(Integer.parseInt(withdrawCount));
    }

    public void setWithdrawCount(int withdrawCount) {
        this.withdrawCount = withdrawCount;
    }

    public int getWithdrawCount() {
        return this.withdrawCount;
    }

    public void setWinRound(int player) {
        if (player == 1) p1WinRound.setText(String.valueOf(++player1WinRound));
        else if (player == 2) p2WinRound.setText(String.valueOf(++player2WinRound));
        else {
            if (this.player1WinRound != 0) p1WinRound.setText(String.valueOf(this.player1WinRound));
            if (this.player2WinRound != 0) p2WinRound.setText(String.valueOf(this.player2WinRound));
        }
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    //    @Override
//    protected void paintComponent(Graphics g) {
//        super.paintComponent(g);
//        if (backgroundImage != null) {
//            Graphics2D g2d = (Graphics2D) g;
//            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
//                                 RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//
//            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
//        }
//    }

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            JFrame frame = new JFrame("GameUI");
//            GameUI gameUI = new GameUI();
//            frame.setContentPane(gameUI);
//            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            frame.setSize(800, 800);
//            frame.setLocationRelativeTo(null); // make center
//            frame.setVisible(true);
//        });
//    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        MainPanel = new JPanel();
        MainPanel.setLayout(new BorderLayout(0, 0));
        MainPanel.setOpaque(false);
        ControlPanel = new JPanel();
        ControlPanel.setLayout(new GridBagLayout());
        MainPanel.add(ControlPanel, BorderLayout.SOUTH);
        withdrawButton = new JButton();
        withdrawButton.setFocusPainted(false);
        withdrawButton.setText("Withdraw");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        ControlPanel.add(withdrawButton, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        ControlPanel.add(spacer1, gbc);
        restartButton = new JButton();
        restartButton.setFocusPainted(false);
        restartButton.setText("Restart");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        ControlPanel.add(restartButton, gbc);
        leaveButton = new JButton();
        leaveButton.setFocusPainted(false);
        leaveButton.setText("Leave");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        ControlPanel.add(leaveButton, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        ControlPanel.add(spacer2, gbc);
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        ControlPanel.add(spacer3, gbc);
        GameBoardPanel = new JPanel();
        GameBoardPanel.setLayout(new BorderLayout(0, 0));
        MainPanel.add(GameBoardPanel, BorderLayout.CENTER);
        InfoPanel = new JPanel();
        InfoPanel.setLayout(new GridBagLayout());
        MainPanel.add(InfoPanel, BorderLayout.NORTH);
        p1WinRound = new JLabel();
        p1WinRound.setText("p1winround");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 0, 0, 5);
        InfoPanel.add(p1WinRound, gbc);
        p2WinRound = new JLabel();
        p2WinRound.setText("p2winround");
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 5, 0, 0);
        InfoPanel.add(p2WinRound, gbc);
        trophyIcon = new JLabel();
        trophyIcon.setText(":");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 1;
        InfoPanel.add(trophyIcon, gbc);
        final JPanel spacer4 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        InfoPanel.add(spacer4, gbc);
        final JPanel spacer5 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 50;
        InfoPanel.add(spacer5, gbc);
        p1NameLabel = new JLabel();
        p1NameLabel.setText("p1 name");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        InfoPanel.add(p1NameLabel, gbc);
        final JPanel spacer6 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 7;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 50;
        InfoPanel.add(spacer6, gbc);
        pIcon1 = new JLabel();
        pIcon1.setHorizontalAlignment(10);
        pIcon1.setHorizontalTextPosition(11);
        pIcon1.setText("p icon");
        pIcon1.setVerifyInputWhenFocusTarget(true);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        InfoPanel.add(pIcon1, gbc);
        pIcon2 = new JLabel();
        pIcon2.setHorizontalAlignment(10);
        pIcon2.setHorizontalTextPosition(11);
        pIcon2.setText("p icon");
        pIcon2.setVerifyInputWhenFocusTarget(true);
        gbc = new GridBagConstraints();
        gbc.gridx = 10;
        gbc.gridy = 1;
        InfoPanel.add(pIcon2, gbc);
        p2NameLabel = new JLabel();
        p2NameLabel.setText("p2 name");
        gbc = new GridBagConstraints();
        gbc.gridx = 10;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        InfoPanel.add(p2NameLabel, gbc);
        blackIcon = new JLabel();
        blackIcon.setText("black");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        InfoPanel.add(blackIcon, gbc);
        final JPanel spacer7 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        InfoPanel.add(spacer7, gbc);
        whiteIcon = new JLabel();
        whiteIcon.setText("white");
        gbc = new GridBagConstraints();
        gbc.gridx = 8;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        InfoPanel.add(whiteIcon, gbc);
        final JPanel spacer8 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        InfoPanel.add(spacer8, gbc);
        p1PlayerTime = new JLabel();
        p1PlayerTime.setText("Time");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 0, 0, 5);
        InfoPanel.add(p1PlayerTime, gbc);
        p2PlayerTime = new JLabel();
        p2PlayerTime.setText("Time");
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 5, 0, 0);
        InfoPanel.add(p2PlayerTime, gbc);
        p1Flag = new JLabel();
        p1Flag.setText("flag");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        InfoPanel.add(p1Flag, gbc);
        p2Flag = new JLabel();
        p2Flag.setHorizontalAlignment(10);
        p2Flag.setHorizontalTextPosition(11);
        p2Flag.setText("flag");
        gbc = new GridBagConstraints();
        gbc.gridx = 10;
        gbc.gridy = 4;
        InfoPanel.add(p2Flag, gbc);
        final JPanel spacer9 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.VERTICAL;
        InfoPanel.add(spacer9, gbc);
        p1TurnTime = new JLabel();
        p1TurnTime.setText("turn time");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 0, 0, 5);
        InfoPanel.add(p1TurnTime, gbc);
        p2TurnTime = new JLabel();
        p2TurnTime.setText("turn time");
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 5, 0, 0);
        InfoPanel.add(p2TurnTime, gbc);
        hourGlassIcon = new JLabel();
        hourGlassIcon.setText("clock");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        InfoPanel.add(hourGlassIcon, gbc);
        clockIcon = new JLabel();
        clockIcon.setText("clock");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        InfoPanel.add(clockIcon, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return MainPanel;
    }

}
