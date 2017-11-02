package client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.Buffer;
import java.rmi.*;
import java.util.*;
import common.ChatInterface;
import common.Chat;

import javax.swing.*;

public class ChatClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private BufferedReader stdIn;

    private static final int port = 8888;
    private static final String name = "localhost";

    public ChatClient(String serverName, int serverPort) {
        System.out.println("Establishing connection...");

        try {
            socket = new Socket(serverName, serverPort);
            System.out.println("Connected: " + socket);

            start();
        } catch (UnknownHostException e) {
            System.out.println("Host unknown: " + e.getMessage());
        } catch (IOException e) {
            System.out.println(e);
        }

        String line = "";
        while (!line.equals("bye")) {
            try {
                System.out.print("> ");
                line = stdIn.readLine();
                out.println(line);

                System.out.println("[server] " + in.readLine());
            } catch (IOException e) {
                System.out.println("Sending error: " + e.getMessage());
            }
        }
    }

    public void start() throws IOException {
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        stdIn = new BufferedReader(new InputStreamReader(System.in));
    }

    public void stop() {
        try {
            if (in != null) { in.close(); }
            if (out != null) { out.close(); }
            if (stdIn != null) { stdIn.close(); }
            if (socket != null) { socket.close(); }
        } catch (IOException e) {
            System.out.println("Error closing...");
        }
    }

    public static void main(String args[]) {
        JFrame frame = new JFrame("ChatWindow");
        frame.setContentPane(new ChatWindow().view);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
//        frame.setContentPane(new Window().getContentPane());

        ChatClient client = new ChatClient(name, port);
    }
}

//public class ChatClient {
//    private static Scanner s;
//    private static final String prompt = "> ";
//
//    public static void main(String[] argv) {
//        System.setProperty("java.security.policy", "security.policy");
//
//        try {
//            System.setSecurityManager(new SecurityManager());
//            s = new Scanner(System.in);
//            System.out.print("Enter Your name and press Enter: ");
//
//            String name = s.nextLine().trim();
//            ChatInterface client = new Chat(name);
//
//            ChatInterface server = (ChatInterface) Naming.lookup("rmi://localhost/ABC");
//            String msg = "[" + client.getName() + "] got connected";
//
//            server.send(msg);
//            System.out.println("[System] Chat Remote Object is ready:");
//            server.setClient(client);
//
//            while (true) {
//                System.out.print(prompt);
//                msg = s.nextLine().trim();
//                msg = "[" + client.getName() + "] " + msg;
//                server.send(msg);
//            }
//
//        } catch (Exception e) {
//            System.out.println("[System] Server failed: " + e);
//        }
//    }
//}

