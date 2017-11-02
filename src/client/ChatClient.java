package client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

import common.Chat;
import common.ChatWindow;

import javax.swing.*;

public class ChatClient {
    private Socket socket;
    private Chat chat;
    private ChatWindow chatWindow;

    private static final int port = 8888;
    private static final String name = "localhost";

    public ChatClient(String serverName, int serverPort) {
        openWindow();

        chatWindow.showStatus("This is the chat client.");
        chatWindow.showStatus("Establishing connection...");
        chatWindow.chatInput.addActionListener(e -> sendMessage(chatWindow.chatInput.getText()));
        chat = new Chat("client", message -> chatWindow.showMessage("server", message));

        try {
            socket = new Socket(serverName, serverPort);
            chat.initIO(socket);

            chatWindow.showStatus("Connected!");
        } catch (UnknownHostException e) {
            System.out.println("Host unknown: " + e.getMessage());
        } catch (IOException e) {
            System.out.println(e);
        }

        try {
            chat.listen();
        } catch (IOException e) {
            chatWindow.showStatus("Sending error: " + e.getMessage());
        }
    }

    public void sendMessage(String message) {
        chatWindow.showMessage("client", message);
        chatWindow.chatInput.setText("");

        chat.sendMessage(message);
    }

    public void openWindow() {
        chatWindow = new ChatWindow("Client");

        JFrame frame = new JFrame("Client Chat");
        frame.setContentPane(chatWindow.view);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String args[]) {
        ChatClient client = new ChatClient(name, port);
    }
}
