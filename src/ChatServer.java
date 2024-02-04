import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatServer {
    private static final int PORT = 5555;
    private Map<String, ClientHandler> clients = new HashMap<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    public static void main(String[] args) {
        new ChatServer().startServer();
    }

    public void startServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server is running on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcastMessage(String sender, String message) {
        String timestamp = dateFormat.format(new Date());
        String formattedMessage = sender + " " + timestamp + ": " + message;

        for (ClientHandler client : clients.values()) {
            client.sendMessage(formattedMessage);
        }
    }

    public void addClient(String clientId, ClientHandler clientHandler) {
        clients.put(clientId, clientHandler);
    }

    public void removeClient(String clientId) {
        clients.remove(clientId);
    }
}
