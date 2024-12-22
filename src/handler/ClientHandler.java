package handler;

import ui.InitialUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class ClientHandler implements ActionListener {
    private final InitialUI initUI;
    private String ADDRESS;//ip
    private int PORT;//port
    private static Socket client;
    private static ServerListener listener;
    private static ClientThread clientThread;
    private JButton startServerBtn;
    private JLabel statusField;
    private boolean isConnected;

    public ClientHandler(InitialUI initUI, String address, String port, JButton startServerBtn, JLabel statusField) {
        this.initUI = initUI;
        this.ADDRESS = address;
        this.PORT = Integer.parseInt(port);
        this.isConnected = isConnected;
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
            if (!isConnected && clientThread == null) {
                clientThread = new ClientThread(ADDRESS, PORT);
                clientThread.start();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                if (client != null && clientThread != null) {
                    button.setText("Disconnect");
                    this.initUI.setClientState(true);
                    statusField.setText("Connected to server: Address(" + ADDRESS + ") " + "Port(" + PORT + ")");
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
            this.initUI.setClientState(false);
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

                PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                out.println("isConnected");

                System.out.println("Connection: " + client);

                listener = new ServerListener(client);
                listener.start();

                Console console = System.console();
                String userInput;
                System.out.print("Me: ");
                while ((userInput = console.readLine()) != null) {
                    if (userInput.contains("This server is already connected with other client")) {
                        System.out.println(listener.isAlive());
                        listener.interrupt();
                        System.out.println(listener.isAlive());
                    }
                    out.println(userInput);

                    if (userInput.contains("bye")) {
                        out.println("disConnected");
                        break;
                    }
                }

            } catch(IOException ie) {
                System.out.println(ie.getMessage());
            }
            finally {
                try {
                    if (client != null) client.close();
                } catch (IOException ie) {
                    System.out.println(ie.getMessage());
                }
            }
        }

    }

    private static class ServerListener extends Thread{
        private final Socket client;
        public ServerListener(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String response;
                while ((response = in.readLine()) != null) {
                    System.out.println(response);
                }
            } catch(IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

}