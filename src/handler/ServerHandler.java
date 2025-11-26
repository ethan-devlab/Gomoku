package handler;

import ui.InitialUI;
import ui.GameUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerHandler implements ActionListener {

    private static InitialUI initUi;
    private static GameUI gameUI;
    private static boolean isClientConnected = false;
    private static ServerSocket serverSocket;
    private int PORT;
    private static ServerThread serverThread;
    private static JButton connectButton;
    private static JButton startServerButton;

    private static Controller controller;

    public ServerHandler(InitialUI ui, String PORT, JButton _connectButton, GameUI gameUi) {
        initUi = ui;
        gameUI = gameUi;
        this.PORT = Integer.parseInt(PORT);
        connectButton = _connectButton;
    }

    public void setPORT(int PORT) {
        this.PORT = PORT;
    }

    private void setConnectButton() {
        connectButton.setEnabled(!initUi.getServerState());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        startServerButton = (JButton) e.getSource();

        if (startServerButton.getText().equals("Start Server")) {
            startServerButton.setText("Stop Server");
            initUi.setServerState(true);
            serverThread = new ServerThread(this.PORT);
            serverThread.start();
            initUi.setStatusText("Waiting player to join...");
        }
        else {
            startServerButton.setText("Start Server");
            if (serverThread.isAlive()) {
                serverThread.interrupt();
                serverThread = null;
            }
            System.out.println(serverSocket.isClosed() ? "Server is closed" : "Server is Opened");
            try {
                System.out.println("Stopping server...");
                serverSocket.close();
            } catch (IOException ie) {
                System.out.println(ie.getMessage());
            }
            System.out.println(serverSocket.isClosed() ? "Server is closed" : "Server is Opened");
            isClientConnected = false;
            initUi.setServerState(false);
            initUi.setStatusText("Disconnected and waiting for connection.");
        }
        setConnectButton();
    }

    public static class HandleClient extends Thread {
        private final Socket client;
        private final ServerSocket serverSocket;
        private final Thread serverThread;
        public HandleClient(Socket client, ServerSocket serverSocket, Thread serverThread) throws IOException {
            this.client = client;
            this.serverSocket = serverSocket;
            this.serverThread = serverThread;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                controller = new Controller(initUi, gameUI, client, true);

                GameData data = new GameData(
                        "2",
                        initUi.getPlayerName(),
                        initUi.getTurnTime(),
                        initUi.getPlayerTime(),
                        initUi.getFirstPlayer(),
                        initUi.getWithdrawCount()
                );

                controller.setGameData(data);

                controller.requestInit(GameFlags.SERVER_INIT);

                String text;

                while ((text = in.readLine()) != null) {
//                    System.out.println("Client sent: " + text);
                    if (text.equalsIgnoreCase("disConnected")) {
                        initUi.setClientState(false);
                        initUi.setStatusText("Waiting player to join...");
                        break;
                    }
                    controller.processMessage(text);
                }
            }
            catch (IOException e) {
                System.out.println(e.getMessage());
            }
            finally {
                try {
                    client.close();
                    serverSocket.close();
                    interrupt();
                    serverThread.interrupt();
                    startServerButton.setText("Start Server");
                    connectButton.setEnabled(true);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
                isClientConnected = false;
            }
        }
    }

    private static void rejectClient(Socket client) {
        try {
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            out.println("This server is already connected with other client");
            client.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static class ServerThread extends Thread {
        private final int PORT;
        public ServerThread(int PORT) {
            this.PORT = PORT;
        }

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(PORT, 1);
                System.out.println(serverSocket);
                System.out.println("Waiting for client connection......");
                while (true) {
                    Socket client = serverSocket.accept();

                    if (isClientConnected) {
                        System.out.println("Another client tried to connect, rejecting...");
                        rejectClient(client);
                    } else {
                        isClientConnected = true;
                        System.out.println("A new client is connected : " + client);
                        initUi.setClientState(true);
                        initUi.setStatusText("Player " + client + " is connected.");
                        new HandleClient(client, serverSocket, currentThread()).start();
                    }
                }
            } catch (IOException ie) {
                JOptionPane.showMessageDialog(null,
                        ie.getMessage(),
                        "Info",
                        JOptionPane.INFORMATION_MESSAGE);
                System.out.println(ie.getMessage());
            }
            finally {
                try {
                    serverSocket.close();
                    interrupt();
                } catch (IOException ie) {
                    System.out.println(ie.getMessage());
                }
            }
        }
    }

}
