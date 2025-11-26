package ui;

import handler.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Random;
import java.text.DecimalFormat;
import java.time.ZonedDateTime;



public class InitialUI extends JPanel {
    private JPanel panel1;
    private JPanel ServerPanel;
    private JLabel serverPortLabel;
    private JPanel ClientPanel;
    private JLabel serverAddressLabel;
    private JTextField serverAddrTextF;
    private JTextField serverPortTextF_C;
    private JButton connectBtn;
    private JLabel serverPortLabel_C;
    private JPanel StatusPanel;
    private JLabel statusfield;
    private JButton playButton;
    private JPanel ControlPanel;
    private JPanel InitialPanel;
    private JLabel pLabel;
    private JTextField pTextF;
    private JLabel timeTurnLabel;
    private JLabel timePlayerLabel;
    private JComboBox<String> timeTurnComB;
    private JComboBox<String> timePlayerComB;
    private JComboBox<String> firstPlayComB;
    private JCheckBox randomPlayer;
    private JFormattedTextField serverPortTextF;
    private JButton startServerBtn;
    private JLabel firstPlayerLabel;
    private JComboBox<String> withdrawComB;
    private JLabel withdrawLabel;
    String[] playerItems = new String[]{"Black", "White"};

    private final String DEFAULT_PORT = "8888";

    private boolean isServer;
    private boolean isConnected;
    private final boolean DEMO = false;

    private TextChangeListener textChangeListener;
    private PortChangeListener portChangeListener;
    private ClientInfoListener clientInfoListener;
    private ServerHandler serverHandler;
    private ClientHandler clientHandler;
    private StartHandler startHandler;

    protected GameUI gameUI;


    public InitialUI() {
        isConnected = false;
        isServer = false;
        if (DEMO) {
            pTextF.setText("testing");
            serverAddrTextF.setText("localhost");
        } else {
            pTextF.setText("Player");
            serverAddrTextF.setText("");
            connectBtn.setEnabled(false);
        }

        URL imageUrl = Objects.requireNonNull(getClass().getResource("/resources/image/right-arrow.png"));
        ImageIcon imgIcon = new ImageIcon(imageUrl);
        Image img = imgIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        this.playButton.setIcon(new ImageIcon(img));
        this.playButton.setText("");
        this.playButton.setEnabled(false);

        gameUI = new GameUI();

        initListener();
        initClient();
        initServer();
        initComboBox();

        ZonedDateTime dateTime = ZonedDateTime.now();
        System.out.println(dateTime);

        Path currentRelativePath = Path.of("");
        String s = currentRelativePath.toAbsolutePath().toString();
        System.out.println(s);

        setLayout(new BorderLayout());
        add(panel1, BorderLayout.CENTER);

    }

//    private Object makeObj(final String item) {
//        return new Object() {
//            public String toString() {
//                return item;
//            }
//        };
//    }

    private void initListener() {
        this.randomPlayer.addActionListener(_ -> changeListener());

        textChangeListener = new TextChangeListener(this, this.playButton, this.pTextF);
        pTextF.getDocument().addDocumentListener(textChangeListener);

        statusfield.setText("Disconnected and waiting for connection.");

        statusfield.addPropertyChangeListener(_ -> playButton.setEnabled(isConnected && !pTextF.getText().isEmpty()));

        startHandler = new StartHandler(gameUI, playButton);
        playButton.addActionListener(startHandler);
    }

    private void initClient() {
        serverPortTextF_C.setText(DEFAULT_PORT);
        clientHandler = new ClientHandler(
                this,
                serverAddrTextF.getText().trim(),
                serverPortTextF_C.getText().trim(),
                startServerBtn,
                statusfield,
                gameUI
        );
        connectBtn.addActionListener(clientHandler);
        clientInfoListener = new ClientInfoListener(this.serverAddrTextF, this.serverPortTextF_C,
                this.connectBtn, this.clientHandler);
        serverAddrTextF.getDocument().addDocumentListener(clientInfoListener);
        serverPortTextF_C.getDocument().addDocumentListener(clientInfoListener);
    }

    private void initServer() {
        DecimalFormat decimalFormat = new DecimalFormat("#");
        NumberFormatter formatter = new NumberFormatter(decimalFormat);
        DefaultFormatterFactory formatterFactory = new DefaultFormatterFactory(formatter);
        serverPortTextF.setFormatterFactory(formatterFactory);
        serverPortTextF.setText(DEFAULT_PORT);

        serverHandler = new ServerHandler(this, serverPortTextF.getText().trim(), connectBtn, gameUI);
        startServerBtn.addActionListener(serverHandler);

        portChangeListener = new PortChangeListener(this.serverPortTextF, this.startServerBtn, serverHandler);
        serverPortTextF.getDocument().addDocumentListener(portChangeListener);
    }

    private void initComboBox() {
        String[] turnItems = new String[]{"10s", "20s", "30s", "40s", "50s", "60s", "∞"};
        for (String item : turnItems) {
//            this.timeTurnComB.addItem(makeObj(item));
            this.timeTurnComB.addItem(item);
        }

        String[] playerTimeItems = new String[]{"5min", "10min", "15min", "20min", "∞"};
        for (String item : playerTimeItems) {
            this.timePlayerComB.addItem(item);
        }

        for (String item : playerItems) {
            this.firstPlayComB.addItem(item);
        }

        String[] withdrawItems = new String[]{"0", "1", "2", "3", "4", "5", "∞"};
        for (String item : withdrawItems) {
            this.withdrawComB.addItem(item);
        }
    }

    private void changeListener() {
        if (this.randomPlayer.isSelected()) {
            Random rand = new Random();
            rand.setSeed(System.currentTimeMillis());
            int choice = rand.nextInt(2);
            this.firstPlayComB.setSelectedIndex(choice);
            System.out.println(choice);
            this.firstPlayComB.setEnabled(false);
        } else {
            this.firstPlayComB.setEnabled(true);
        }
    }

    public void setPlayButtonEnable(boolean enable) {
        this.playButton.setEnabled(enable);
    }

    public String getPlayerName() {
        return this.pTextF.getText().trim();
    }

    public String getTurnTime() {
        return (String) this.timeTurnComB.getSelectedItem();
    }

    public String getPlayerTime() {
        return (String) this.timePlayerComB.getSelectedItem();
    }

    public int getFirstPlayer() {
        return this.firstPlayComB.getSelectedIndex() == 0 ? 1 : 2;
    }

    public String getWithdrawCount() {
        return (String) this.withdrawComB.getSelectedItem();
    }

    public void setServerState(boolean state) {
        this.isServer = state;
    }

    public void setClientState(boolean state) {
        this.isConnected = state;
    }

    public boolean getServerState() {
        return isServer;
    }

    public boolean getClientState() {
        return isConnected;
    }

    public JButton getConnectBtn() {
        return this.connectBtn;
    }

    public JButton getStartServerBtn() {
        return this.startServerBtn;
    }

    public void setStatusText(String status) {
        statusfield.setText(status);
    }

    public static void main(String[] args) {
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

            InitialUI initialUI = new InitialUI();
            frame.setContentPane(initialUI);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(550, 650);
            frame.setResizable(false);
            frame.setLocationRelativeTo(null); // make center
            frame.setVisible(true);
        });
    }

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
        panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        panel1.setOpaque(false);
        ServerPanel = new JPanel();
        ServerPanel.setLayout(new GridBagLayout());
        ServerPanel.setOpaque(false);
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipady = 10;
        panel1.add(ServerPanel, gbc);
        ServerPanel.setBorder(BorderFactory.createTitledBorder(null, "Server", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        serverPortLabel = new JLabel();
        serverPortLabel.setText("Server Port");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        ServerPanel.add(serverPortLabel, gbc);
        serverPortTextF = new JFormattedTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        ServerPanel.add(serverPortTextF, gbc);
        startServerBtn = new JButton();
        startServerBtn.setFocusPainted(false);
        startServerBtn.setMargin(new Insets(0, 0, 0, 0));
        startServerBtn.setText("Start Server");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.ipadx = 10;
        gbc.ipady = 5;
        ServerPanel.add(startServerBtn, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.VERTICAL;
        ServerPanel.add(spacer1, gbc);
        ClientPanel = new JPanel();
        ClientPanel.setLayout(new GridBagLayout());
        ClientPanel.setAutoscrolls(false);
        ClientPanel.setOpaque(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipady = 10;
        panel1.add(ClientPanel, gbc);
        ClientPanel.setBorder(BorderFactory.createTitledBorder(null, "Client", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        serverAddressLabel = new JLabel();
        serverAddressLabel.setText("Server Address");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        ClientPanel.add(serverAddressLabel, gbc);
        serverAddrTextF = new JTextField();
        serverAddrTextF.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        ClientPanel.add(serverAddrTextF, gbc);
        connectBtn = new JButton();
        connectBtn.setFocusPainted(false);
        connectBtn.setText("Connect");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        ClientPanel.add(connectBtn, gbc);
        serverPortLabel_C = new JLabel();
        serverPortLabel_C.setText("Server Port");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        ClientPanel.add(serverPortLabel_C, gbc);
        serverPortTextF_C = new JTextField();
        serverPortTextF_C.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        ClientPanel.add(serverPortTextF_C, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.VERTICAL;
        ClientPanel.add(spacer2, gbc);
        StatusPanel = new JPanel();
        StatusPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipady = 10;
        panel1.add(StatusPanel, gbc);
        StatusPanel.setBorder(BorderFactory.createTitledBorder(null, "Status", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        statusfield = new JLabel();
        statusfield.setText("Status");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        StatusPanel.add(statusfield, gbc);
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        StatusPanel.add(spacer3, gbc);
        ControlPanel = new JPanel();
        ControlPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(ControlPanel, gbc);
        playButton = new JButton();
        playButton.setBorderPainted(false);
        playButton.setContentAreaFilled(false);
        playButton.setFocusPainted(false);
        playButton.setHorizontalTextPosition(11);
        playButton.setText("Play");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        ControlPanel.add(playButton, gbc);
        final JPanel spacer4 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        ControlPanel.add(spacer4, gbc);
        final JPanel spacer5 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.VERTICAL;
        ControlPanel.add(spacer5, gbc);
        InitialPanel = new JPanel();
        InitialPanel.setLayout(new GridBagLayout());
        InitialPanel.setMinimumSize(new Dimension(200, 187));
        InitialPanel.setOpaque(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipady = 20;
        panel1.add(InitialPanel, gbc);
        InitialPanel.setBorder(BorderFactory.createTitledBorder(null, "Initial", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        pLabel = new JLabel();
        pLabel.setText("Player's Name");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        InitialPanel.add(pLabel, gbc);
        pTextF = new JTextField();
        pTextF.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        InitialPanel.add(pTextF, gbc);
        timeTurnLabel = new JLabel();
        timeTurnLabel.setText("Time Per Turn (Opt)");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipady = 5;
        InitialPanel.add(timeTurnLabel, gbc);
        timePlayerLabel = new JLabel();
        timePlayerLabel.setText("Time Per Player (Opt)");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipady = 5;
        InitialPanel.add(timePlayerLabel, gbc);
        timeTurnComB = new JComboBox<String>();
        final DefaultComboBoxModel<String> defaultComboBoxModel1 = new DefaultComboBoxModel<String>();
        timeTurnComB.setModel(defaultComboBoxModel1);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        InitialPanel.add(timeTurnComB, gbc);
        timePlayerComB = new JComboBox<String>();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        InitialPanel.add(timePlayerComB, gbc);
        firstPlayerLabel = new JLabel();
        firstPlayerLabel.setText("First Player");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipady = 5;
        InitialPanel.add(firstPlayerLabel, gbc);
        firstPlayComB = new JComboBox<String>();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        InitialPanel.add(firstPlayComB, gbc);
        randomPlayer = new JCheckBox();
        randomPlayer.setText("Random");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        InitialPanel.add(randomPlayer, gbc);
        final JPanel spacer6 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.VERTICAL;
        InitialPanel.add(spacer6, gbc);
        withdrawLabel = new JLabel();
        withdrawLabel.setText("Withdraw");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.WEST;
        InitialPanel.add(withdrawLabel, gbc);
        final JPanel spacer7 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.fill = GridBagConstraints.VERTICAL;
        InitialPanel.add(spacer7, gbc);
        withdrawComB = new JComboBox<String>();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        InitialPanel.add(withdrawComB, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

}
