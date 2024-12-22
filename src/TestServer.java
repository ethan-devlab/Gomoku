import java.io.*;
import java.net.*;

public class TestServer {
    private static boolean isClientConnected = false;  // Track if a client is connected

    public static void main(String[] args) {
        int port = 12345;  // Port to listen for incoming connections

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is waiting for client connections...");

            while (true) {
                // Handle client connection in a new thread
                Socket clientSocket = serverSocket.accept();

                // If a client is already connected, reject the new connection
                if (isClientConnected) {
                    System.out.println("Another client tried to connect, rejecting...");
                    rejectClient(clientSocket);
                } else {
                    // Handle the first client connection
                    isClientConnected = true;
                    System.out.println("Client connected.");
                    new ClientHandler(clientSocket).start();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void rejectClient(Socket clientSocket) {
        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            out.println("Another client connected to server.");
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // A separate thread to handle the client connection
    private static class ClientHandler extends Thread {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true)) {

                output.println("You are connected to the server.");
                String clientMessage;
                while ((clientMessage = input.readLine()) != null) {
                    System.out.println("Client says: " + clientMessage);
                    if ("exit".equalsIgnoreCase(clientMessage)) {
                        break;
                    }
                    output.println("Server echoes: " + clientMessage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                isClientConnected = false;  // Reset when the client disconnects
            }
        }
    }
}
