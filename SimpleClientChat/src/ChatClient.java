import java.rmi.*;
import java.rmi.server.*;
import java.util.*;

public class ChatClient {
	private static Scanner s;
	private static final String prompt = "> ";

	public static void main(String[] argv) {
		System.setProperty("java.security.policy", "./security.policy");

		try {
			System.setSecurityManager(new SecurityManager());
			s = new Scanner(System.in);
			System.out.print("Enter Your name and press Enter: ");

			String name = s.nextLine().trim();
			ChatInterface client = new Chat(name);

			ChatInterface server = (ChatInterface) Naming.lookup("rmi://localhost/ABC");
			String msg = "[" + client.getName() + "] got connected";

			server.send(msg);
			System.out.println("[System] Chat Remote Object is ready:");
			server.setClient(client);

			while (true) {
				System.out.print(prompt);
				msg = s.nextLine().trim();
				msg = "[" + client.getName() + "] " + msg;
				server.send(msg);
			}

		} catch (Exception e) {
			System.out.println("[System] Server failed: " + e);
		}
	}
}