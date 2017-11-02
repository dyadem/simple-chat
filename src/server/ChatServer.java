package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.*;
import java.util.*;
import common.Chat;
import common.ChatInterface;

public class ChatServer {

    private Socket socket;
    private ServerSocket server;
    private PrintWriter out;
    private BufferedReader in;
    private BufferedReader stdIn;

    private static final int port = 8888;

    public ChatServer(int port) {
        try {
            System.out.println("Binding to port " + port);
            server = new ServerSocket(port);

            System.out.println("Waiting for client...");
            socket = server.accept();

            System.out.println("Connected to client...");
            open();

            boolean done = false;
            while(!done) {
                try {
                    String line = in.readLine();
                    System.out.println("[client] " + line);

                    System.out.print("> ");
                    line = stdIn.readLine();
                    out.println(line);
                } catch (IOException e) {
                    done = true;
                }
            }
        } catch(IOException e) {
            System.out.println(e);
        }
    }

    public void open() throws IOException {
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        stdIn = new BufferedReader(new InputStreamReader(System.in));
    }

    public void close() throws IOException {
        if (in != null) { in.close(); }
        if (out != null) { out.close(); }
        if (stdIn != null) {  stdIn.close(); }
        if (socket != null) { socket.close(); }
    }

    public static void main(String[] argv) {
        ChatServer server = new ChatServer(port);
    }
}

//public class ChatServer {
//    private static Scanner s;
//    private static final String prompt = "> ";
//
//    public static void main(String[] argv) {
//        System.setProperty("java.security.policy", "security.policy");
//
//        try {
//            System.setSecurityManager(new SecurityManager());
//
//            // Start RMI registry
//            try {
//                java.rmi.registry.LocateRegistry.createRegistry(1099);
//                System.out.println("RMI registry ready.");
//            } catch (Exception e) {
//                System.out.println("Exception starting RMI registry:");
//                e.printStackTrace();
//                System.exit(0);
//            }
//
//            s = new Scanner(System.in);
//            System.out.print("Enter Your name and press Enter: ");
//            String name = s.nextLine().trim();
//
//            Chat server = new Chat(name);
//            Naming.rebind("rmi://localhost/ABC", server);
//
//            System.out.println("[System] Chat Remote Object is ready:");
//
//            while (true) {
//                System.out.print(prompt);
//                String msg = s.nextLine().trim();
//
//                if (server.getClient() != null) {
//                    ChatInterface client = server.getClient();
//                    msg = "[" + server.getName() + "] " + msg;
//                    client.send(msg);
//                }
//            }
//
//        } catch (Exception e) {
//            System.out.println("[System] Server failed: " + e);
//        }
//    }
//}