import java.io.*;
import java.net.*;

public class TestClient {
    public static void main(String[] args) {
        String serverAddress = "localhost";  // Server address
        int serverPort = 12345;  // Server port

        try (Socket socket = new Socket(serverAddress, serverPort)) {
            // Receive initial message from server
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String serverMessage = input.readLine();
            System.out.println("Server: " + serverMessage);

            // Communicate with the server
            if (!serverMessage.contains("Another client connected to server")) {
                BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

                String userMessage;
                while (true) {
                    System.out.print("Enter message (type 'exit' to quit): ");
                    userMessage = userInput.readLine();
                    output.println(userMessage);

                    String response = input.readLine();
                    System.out.println("Server: " + response);

                    if ("exit".equalsIgnoreCase(userMessage)) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Server is already occupied or unreachable. " + e.getMessage());
        }
    }
}
