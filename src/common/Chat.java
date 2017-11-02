package common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Chat  {

    private static final long serialVersionUID = 1L;
    private String name;

    private PrintWriter out;
    private BufferedReader in;
    private ChatService chatService;
    private Socket socket;

    public Chat(String name, ChatService chatService) {
        this.name = name;
        this.chatService = chatService;
    }

    public String getName() {
        return name;
    }

    public void initIO(Socket socket) throws IOException {
        this.socket = socket;
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void stop() throws IOException {
        if (in != null) in.close();
        if (out != null) out.close();
        if (socket != null) socket.close();
    }

    public void listen() throws IOException {
        String message;
        while(true) {
            message = in.readLine();
            if (message == null) {
                break;
            }
            receiveMessage(message);
        }
        stop();
    }

    public void sendMessage(String message) {
        // Encrypt message here
        out.println(message);
    }

    public void receiveMessage(String message) {
        // Decrypt message here
        chatService.messageIn(message);
    }

    public interface ChatService {
        void messageIn(String message);
    }
}