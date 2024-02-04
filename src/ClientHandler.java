import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private ChatServer chatServer;
    private BufferedReader reader;
    private PrintWriter writer;
    private String clientId;

    public ClientHandler(Socket clientSocket, ChatServer chatServer) {
        this.clientSocket = clientSocket;
        this.chatServer = chatServer;
        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(clientSocket.getOutputStream(), true);
            clientId = reader.readLine();
            chatServer.addClient(clientId, this);
            System.out.println(clientId + " connected to the chat.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String message;
            while ((message = reader.readLine()) != null) {
                if (message.equals("/exit")) {
                    break;
                }
                chatServer.broadcastMessage(clientId, message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            chatServer.removeClient(clientId);
            System.out.println(clientId + " disconnected from the chat.");
            close();
        }
    }

    public void sendMessage(String message) {
        writer.println(message);
    }

    private void close() {
        try {
            reader.close();
            writer.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
