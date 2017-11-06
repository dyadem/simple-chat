package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

import common.Auth;
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
    private ChatSettings chatSettings;

    private boolean waitingForPassword = false;

    private static final int port = 8888;
    private static final String name = "localhost";

    public ChatClient(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;

        openWindow();

        chatWindow.showStatus("Please select the chat settings from the right ->");
        chatWindow.chatInput.addActionListener(e -> {
            String text = chatWindow.chatInput.getText();

            // Do not run on listener thread
            SwingWorker sw = new SwingWorker() {
                @Override
                protected Object doInBackground() throws Exception {
                    if (waitingForPassword) {
                        chatWindow.clearInput();
                        if (Auth.userLogin(text)) {
                            waitingForPassword = false;
                            startConnecting();
                        } else {
                            chatWindow.showStatus("Password incorrect...");
                        }
                    } else {
                        sendMessage(text);
                    }
                    return null;
                }
            };
            sw.execute();
        });

        chatWindow.okayButton.addActionListener(e -> {
            chatWindow.lockChecks();

            // Do not run on event listener thread
            SwingWorker sw = new SwingWorker() {
                public Object doInBackground(){
                    chatSettings = new ChatSettings(
                        chatWindow.confedentialityCheck.isSelected(),
                        chatWindow.integrityCheck.isSelected(),
                        chatWindow.authenticationCheck.isSelected()
                    );
                    start();
                    return null;
                }
            };
            sw.execute();
        });
    }

    public void sendMessage(String message) {
        if (chat != null && chat.isConnected()) {
            chatWindow.showMessage("client", message);
            chatWindow.clearInput();

            chat.sendMessage(message);
        }
    }

    public void start() {
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

        startPasswordCheck();
    }

    public void startPasswordCheck() {
        if (chatSettings.isAuthentication()) {
            chatWindow.showStatus("\nPlease enter the password");
            waitingForPassword = true;
        } else {
            startConnecting();
        }
    }

    public void startConnecting() {
        chatWindow.showStatus("This is the chat client.");
        chatWindow.showStatus("Establishing connection...");

        try {
            socket = new Socket(serverName, serverPort);
            chat.initIO(socket);

            chatWindow.showStatus("Connected to server, initiating protocol.");
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
            chatWindow.showStatus("Disconnected");

            // TODO: Handle this error better
            chat = null;
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
