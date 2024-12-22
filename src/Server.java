import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;


public class Server {
	private static boolean isClientConnected = false;

	public static class handleClient extends Thread {
		private Socket client;

		public handleClient(Socket client) throws IOException {
            this.client = client;
        }

		@Override
		public void run() {
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				// PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
				out.write("Connect to server successfully");
				out.newLine();
				out.flush();

				String text;

				while ((text = in.readLine()) != null) {
					System.out.println("Client sent: " + text);
					// out.println("Server received: " + text);
					if (text.equals("bye")) {
						out.write("Server: bye!");
						out.newLine();
						out.flush();
						break;
					}
					out.write("Server received: " + text);
					out.newLine();
					out.flush();
				}
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

	public static void main(String[] args) {
		int portNumber = 5555;
		ServerSocket serverSocket = null;

			try {
				serverSocket = new ServerSocket(portNumber, 1);
				while (true) {
					System.out.println("Waiting for client connection......");
					Socket client = serverSocket.accept();

					if (isClientConnected) {
						System.out.println("Another client tried to connect, rejecting...");
						rejectClient(client);
					}
					else {
						isClientConnected = true;
						System.out.println("A new client is connected : " + client);
						new handleClient(client).start();
					}
				}
				
			} catch(IOException e) {
				System.out.println(e);
			} finally {
				try {
                    assert serverSocket != null;
                    serverSocket.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
			}

		
	}
}