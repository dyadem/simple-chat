import java.rmi.*;
import java.util.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ChatServer {
	private static Scanner s;
	private static final String prompt = "> ";

	public static void main(String[] argv) {
		System.setProperty("java.security.policy", "./security.policy");

		try {
			System.setSecurityManager(new SecurityManager());

			// Start RMI registry
			try {
				java.rmi.registry.LocateRegistry.createRegistry(1099);
				System.out.println("RMI registry ready.");
			} catch (Exception e) {
				System.out.println("Exception starting RMI registry:");
				e.printStackTrace();
			}

			s = new Scanner(System.in);
			System.out.print("Enter Your name and press Enter: ");
			String name = s.nextLine().trim();

			Chat server = new Chat(name);
			Naming.rebind("rmi://localhost/ABC", server);

			System.out.println("[System] Chat Remote Object is ready:");

			while (true) {
				System.out.print(prompt);
				String msg = s.nextLine().trim();

				if (server.getClient() != null) {
					ChatInterface client = server.getClient();
					msg = "[" + server.getName() + "] " + msg;
					client.send(msg);
				}
			}

		} catch (Exception e) {
			System.out.println("[System] Server failed: " + e);
		}
	}
}