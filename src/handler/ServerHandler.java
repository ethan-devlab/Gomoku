package handler;

import ui.InitialUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerHandler implements ActionListener {

    private static InitialUI initUi;
    private static boolean isClientConnected = false;
    private static ServerSocket serverSocket;
    private int PORT;
    private static ServerThread serverThread;
    private JButton connectButton;

    public ServerHandler(InitialUI ui, String PORT, JButton connectButton) {
        initUi = ui;
        this.PORT = Integer.parseInt(PORT);
        this.connectButton = connectButton;
    }

    public ServerHandler(InitialUI ui) {
        initUi = ui;
        this.PORT = 8080;
    }

    public void setPORT(int PORT) {
        this.PORT = PORT;
    }

    public static class HandleClient extends Thread {
        private final Socket client;

        public HandleClient(Socket client) throws IOException {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter out = new PrintWriter(client.getOutputStream(), true);
//                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                out.println("Connect to server successfully");
//                out.newLine();
//                out.flush();

                String text;

                while ((text = in.readLine()) != null) {
                    System.out.println("Client sent: " + text);
                    // out.println("Server received: " + text);
                    if (text.equals("bye")) {
                        out.println("Server: bye!");
//                        out.newLine();
//                        out.flush();
                        break;
                    }

                    if (text.equalsIgnoreCase("disConnected")) {
                        initUi.setClientState(false);
                        initUi.setStatusText("Waiting player to join...");
                    }
                    out.println("Server received: " + text);
                }
                client.close();
            }
            catch(IOException e) {
                System.out.println(e.getMessage());
            }
            finally {
                try {
                    client.close();
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
                        new HandleClient(client).start();
                    }
                }
            } catch (IOException ie) {
                System.out.println(ie.getMessage());
            }
            finally {
                try {
                    serverSocket.close();

                } catch (IOException ie) {
                    System.out.println(ie.getMessage());
                }
            }
        }
    }

    private void setConnectButton() {
        connectButton.setEnabled(!initUi.getServerState());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton) e.getSource();

        if (button.getText().equals("Start Server")) {
            button.setText("Stop Server");
            initUi.setServerState(true);
            serverThread = new ServerThread(this.PORT);
            serverThread.start();
            initUi.setStatusText("Waiting player to join...");
        }
        else {
            button.setText("Start Server");
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
}
