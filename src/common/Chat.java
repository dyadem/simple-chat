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
    private ChatProtocol chatProtocol;
    private ChatProtocol.Status protocolStatus;

    private ChatSettings chatSettings;

    public Chat(String name, ChatSettings chatSettings, ChatService chatService) {
        this.name = name;
        this.chatSettings = chatSettings;
        this.chatService = chatService;
        this.chatProtocol = new ChatProtocol(chatSettings);
        this.protocolStatus = ChatProtocol.Status.OKAY;

        chatService.showStatus("Chat settings you've selected are " + chatSettings.getSettingsString());
    }

    public String getName() {
        return name;
    }

    public void initIO(Socket socket) throws IOException {
        this.socket = socket;
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void initProtocol() {
        chatProtocol.init();
    }

    public void startProtocol() {
        out.println(chatProtocol.startProtocol());
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
        out.println(message);
    }

    public void receiveMessage(String message) {

        // If the initial protocol has not succeeded, send the message to it
        if (protocolStatus != ChatProtocol.Status.SUCCEED) {

            chatService.showStatus("[ IN PROTOCOL ] " + message);

            ChatProtocol.ProtocolResult result = chatProtocol.nextMessage(message);
            protocolStatus = result.newStatus;

            chatService.showStatus("[ OUT PROTOCOL ] " + result.newMessage);
            chatService.showStatus("");

            if (!result.newMessage.isEmpty()) sendMessage(result.newMessage);
            if (protocolStatus == ChatProtocol.Status.REFUSE) {
                disconnect();
                return;
            }


        } else {
            chatService.showMessage(message);
        }
    }

    public interface ChatService {
        void showMessage(String message);
        void showStatus(String message);
    }

    private String encryptMessage(String message) {
        return message;
    }

    private String decryptMessage(String message) {
        return message;
    }

    private String signMessageWithPublicKey(String message) {
        return message;
    }

    private String unSignMessageWithPrivateKey(String message) {
        return message;
    }

    private boolean userLogin(String password) {
        return password == "password";
    }

    private void disconnect() {
        chatService.showStatus("Chat requirements not the same, closing connection");

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}