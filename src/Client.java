import java.net.Socket;
import java.io.*;

public class Client {
	private static final String ADDRESS = "localhost";//ip
	private static final int PORT = 8080;//port
	private static Socket client;
	private static ServerListener listener;

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

	public static void main(String args[]) {
		try {
			client = new Socket(ADDRESS, PORT);

			// PrintWriter out = new PrintWriter(server.getOutputStream(), true);
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

			System.out.println("Connection: " + client);

			listener = new ServerListener(client);
			listener.start();

			Console console = System.console();
			String userInput;
			System.out.print("Me: ");
			while ((userInput = console.readLine()) != null) {
				// out.println(userInput);
				if (userInput.contains("This server is already connected with other client")) {
					System.out.println(listener.isAlive());
					listener.interrupt();
					System.out.println(listener.isAlive());
				}

				out.write(userInput);
				out.newLine();
				out.flush();

				if (userInput.contains("bye")) break;
			}

		} catch(IOException e) {
			System.out.println(e.getMessage());
        } finally {
			try {
				client.close();
				listener.interrupt();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
	}
}