package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

import common.Chat;
import common.ChatSettings;
import common.ChatWindow;

import javax.swing.*;

public class ChatClient {
    private Socket socket;
    private Chat chat;
    private ChatWindow chatWindow;
    private String serverName;
    private int serverPort;

    private static final int port = 8888;
    private static final String name = "localhost";

    public ChatClient(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;

        openWindow();

        chatWindow.showStatus("Please select the chat settings from the right ->");
        chatWindow.chatInput.addActionListener(e -> sendMessage(chatWindow.chatInput.getText()));
        chatWindow.okayButton.addActionListener(e -> {
            chatWindow.lockChecks();

            SwingWorker sw = new SwingWorker() {
                public Object doInBackground(){
                    start(new ChatSettings(
                            chatWindow.confedentialityCheck.isSelected(),
                            chatWindow.integrityCheck.isSelected(),
                            chatWindow.authenticationCheck.isSelected()));
                    return null;
                }
            };
            sw.execute();
        });
    }

    public void sendMessage(String message) {
        chatWindow.showMessage("client", message);
        chatWindow.chatInput.setText("");

        chat.sendMessage(message);
    }

    public void start(ChatSettings chatSettings) {
        chatWindow.showStatus("This is the chat client.");
        chatWindow.showStatus("Establishing connection...");

        chat = new Chat("client", chatSettings, new Chat.ChatService() {
            @Override
            public void showMessage(String message) {
                chatWindow.showMessage("server", message);
            }

            @Override
            public void showStatus(String message) {
                chatWindow.showStatus(message);
            }
        });

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
            chat.initProtocol();
            chat.startProtocol();
            chat.listen();
        } catch (IOException e) {
            chatWindow.showStatus("Sending error: " + e.getMessage());
        }
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
