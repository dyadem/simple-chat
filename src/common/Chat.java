package common;

import java.io.*;
import java.net.Socket;

public class Chat  {

    private static final long serialVersionUID = 1L;
    private String name;

//    private PrintWriter out;
//    private BufferedReader in;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ChatService chatService;
    private Socket socket;
    private ChatProtocol chatProtocol;
    private ChatProtocol.Status protocolStatus;

    private ChatSettings chatSettings;

    public interface ChatService {
        void showMessage(String message);
        void showInfo(String message);
        void showImportant(String message);
        void showError(String message);
    }

    public Chat(String name, ChatSettings chatSettings, ChatService chatService) {
        this.name = name;
        this.chatSettings = chatSettings;
        this.chatService = chatService;
        this.chatProtocol = new ChatProtocol(chatSettings);
        this.protocolStatus = ChatProtocol.Status.OKAY;

        chatService.showInfo("Chat settings you've selected: " + chatSettings.getSettingsString());
    }

    public String getName() {
        return name;
    }

    // Create input and output socket readers
    public void initIO(Socket socket) throws IOException {
        this.socket = socket;
//        out = new PrintWriter(socket.getOutputStream(), true);
//        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }

    // Reset protocol settings
    public void initProtocol() {
        protocolStatus = ChatProtocol.Status.OKAY;
        chatProtocol.init();
    }

    // Send the first message in the protocol (Normally the client does this)
    public void startProtocol() throws IOException {
        out.writeObject(new Message(chatProtocol.startProtocol()));
    }

    public void stop() throws IOException {
        if (in != null) in.close();
        if (out != null) out.close();
        if (socket != null) socket.close();
    }

    // Start listening on the socket (infinite loop)
    public void listen() throws IOException, ClassNotFoundException {
        Message message;
        while(true) {
            message = (Message)in.readObject();
            if (message == null) {
                break;
            }
            receiveMessage(message);
        }
        stop();
    }

    // Handle the sending of a message
    public void sendMessage(String text) throws IOException {
        if (out == null) return;

        Message message = new Message(text);

        if (chatSettings.isConfedentiality()) {
            message = Auth.encryptMessage(message);
        }
        if (chatSettings.isIntegrity()) {
            message = Auth.signMessageWithPublicKey(message);
        }

        if (name.compareTo("client") == 0) {
            message.bigolfuck = "shitballs";
        }
        out.writeObject(message);
    }

    // Handle the receiving of a message
    public void receiveMessage(Message message) throws IOException {
        // If the initial protocol has not succeeded, send the message to it
        if (protocolStatus != ChatProtocol.Status.SUCCEED) {
            ChatProtocol.ProtocolResult result = chatProtocol.nextMessage(message.getText());
            protocolStatus = result.newStatus;

            if (!result.newMessage.isEmpty()) sendMessage(result.newMessage);
            if (protocolStatus == ChatProtocol.Status.SUCCEED) {
                chatService.showImportant("Handshake successful. Chat away!\n");
            }

            if (protocolStatus == ChatProtocol.Status.REFUSE) {
                disconnect();
                return;
            }
        } else {

            // Do this in opposite order of sending a message
            if (chatSettings.isIntegrity()) {
                message = Auth.verifyMessageWithPrivateKey(message);
            }
            if (chatSettings.isConfedentiality()) {
                message = Auth.decryptMessage(message);
            }

            chatService.showMessage(message.getText());
        }
    }

    // Can the user send messages
    public boolean isConnected() {
        return  socket != null
                && socket.isConnected()
                && (protocolStatus == ChatProtocol.Status.SUCCEED);
    }

    // Disconnect because of a difference in the chat settings
    private void disconnect() {
        chatService.showError("Chat requirements not the same, closing connection");

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}