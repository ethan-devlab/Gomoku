package handler;

import ui.GameUI;
import ui.InitialUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class ClientHandler implements ActionListener {
    private static InitialUI initUI;
    private static GameUI gameUI;
    private String ADDRESS;//ip
    private int PORT;//port
    private static Socket client;
    private static ClientThread clientThread;
    private final JButton startServerBtn;
    private final JLabel statusField;
    private static boolean isConnected;

    private static Controller controller;

    public ClientHandler(InitialUI initUi, String address, String port, JButton startServerBtn, JLabel statusField,
                         GameUI gameUi) {
        initUI = initUi;
        gameUI = gameUi;
        this.ADDRESS = address;
        this.PORT = Integer.parseInt(port);
        this.startServerBtn = startServerBtn;
        this.statusField = statusField;
    }

    public void setADDRESS(String address) {
        this.ADDRESS = address;
    }

    public void setPORT(int port) {
        this.PORT = port;
    }

    private void setStartServerBtn() {
        startServerBtn.setEnabled(!initUI.getClientState());
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton) e.getSource();

        if (button.getText().equals("Connect")) {
            if (clientThread != null) clientThread = null;
            if (!isConnected) {
                clientThread = new ClientThread(ADDRESS, PORT);
                clientThread.start();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                if (client != null && clientThread != null) {
                    button.setText("Disconnect");
                    initUI.setClientState(true);
                    statusField.setText("Connected to server: " + client);
                    JOptionPane.showMessageDialog(null, "Connect to the server.",
                            "Connection Success", JOptionPane.INFORMATION_MESSAGE);
                }
                else {
                    JOptionPane.showMessageDialog(null, "Failed to connect to the server.",
                                                  "Connection Failed", JOptionPane.WARNING_MESSAGE);
                    clientThread.interrupt();
                    clientThread = null;
                }
            }
        }
        else {
            button.setText("Connect");
            if (clientThread != null && clientThread.isAlive()) {
                clientThread.interrupt();
                clientThread = null;
            }
            if (client != null && !client.isClosed()) {
                System.out.println(client.isClosed() ? "Client is closed" : "Client is Opened");
                System.out.println("Closing client...");
                try {
                    PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                    out.println("disConnected");
                    client.close();
                    client = null;
                } catch (IOException ie) {
                    System.out.println(ie.getMessage());
                }
            }
            statusField.setText("Disconnected and waiting for connection.");
            initUI.setClientState(false);
        }
        setStartServerBtn();
    }

    private static class ClientThread extends Thread {
        private final String ADDRESS;
        private final int PORT;
        public ClientThread(String ADDRESS, int PORT) {
            this.ADDRESS = ADDRESS;
            this.PORT = PORT;
        }

        @Override
        public void run() {
            try {
                client = new Socket(ADDRESS, PORT);

                controller = new Controller(initUI, gameUI, client, false);

                System.out.println("Connection: " + client);

                GameData data = new GameData(
                        "1",
                        initUI.getPlayerName(),
                        initUI.getTurnTime(),
                        initUI.getPlayerTime(),
                        initUI.getFirstPlayer(),
                        initUI.getWithdrawCount()
                );

                controller.setGameData(data);

                controller.requestInit(GameFlags.CLIENT_INIT);

                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String response;
                while ((response = in.readLine()) != null) {
//                    System.out.println("Response:" + response);
                    controller.processMessage(response);
                    if (response.contains("This server is already connected with other client")) {
                        interrupt();
                        break;
                    }
                }

            } catch(IOException ie) {
                System.out.println(ie.getMessage());
            } finally {
                try {
                    if (client != null) {
                        client.close();
                        client = null;
                    }
                    isConnected = false;
                    interrupt();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }

        }

    }
}