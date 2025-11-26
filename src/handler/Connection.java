package handler;

import java.io.*;
import java.net.Socket;

/**
 * Connection class wrapping socket I/O operations.
 * Implements SessionGateway for unified network access.
 */
public class Connection implements SessionGateway {

    private final Socket socket;
    private final PrintWriter out;
    private final BufferedReader in;
    private volatile boolean connected;

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.connected = true;
    }

    @Override
    public void sendMessage(String message) {
        if (connected && out != null) {
            out.println(message);
        }
    }

    @Override
    public void close() throws IOException {
        connected = false;
        if (out != null) {
            out.close();
        }
        if (in != null) {
            in.close();
        }
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    @Override
    public boolean isConnected() {
        return connected && socket != null && !socket.isClosed();
    }

    /**
     * Read the next line from the connection.
     * Blocks until a line is available or connection is closed.
     * 
     * @return the line read, or null if connection is closed
     */
    public String readLine() throws IOException {
        return in.readLine();
    }

    /**
     * Get the underlying socket (for compatibility during transition).
     */
    public Socket getSocket() {
        return socket;
    }

    @Override
    public String toString() {
        return socket != null ? socket.toString() : "Connection[closed]";
    }
}
