package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import common.Chat;
import common.ChatWindow;

import javax.swing.*;

public class ChatServer {

    private ServerSocket server;
    private Chat chat;
    private ChatWindow chatWindow;

    private static final int port = 8888;

    public ChatServer(int port) {
        openWindow();

        chatWindow.showStatus("This is the chat server.");
        chatWindow.showStatus("Binding to port " + port);
        chatWindow.chatInput.addActionListener(e -> sendMessage(chatWindow.chatInput.getText()));
        chat = new Chat("server", message -> chatWindow.showMessage("client", message));

        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println(e);
        }

        // If client disconnects we just start accepting connections again
        while (true) {
            try {
                Socket socket = waitForClient();
                chat.initIO(socket);
            } catch (IOException e) {
                chatWindow.showStatus("Error connecting to client");
                continue;
            }

            try {
                chat.listen();

                chatWindow.showStatus("Client disconnected\n");
            } catch(IOException e) {
                chatWindow.showStatus("IO Error: " + e.getMessage());
            }
        }

    }

    public Socket waitForClient() throws IOException {
        chatWindow.showStatus("Waiting for client...");
        Socket socket = server.accept();
        chatWindow.showStatus("Connected to client!");
        return socket;
    }

    public void sendMessage(String message) {
        chatWindow.showMessage("server", message);
        chatWindow.chatInput.setText("");

        chat.sendMessage(message);
    }

    public void openWindow() {
        chatWindow = new ChatWindow(("Server"));

        JFrame frame = new JFrame("Server Chat");
        frame.setContentPane(chatWindow.view);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] argv) {
        ChatServer server = new ChatServer(port);
    }
}