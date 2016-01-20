package application;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import client.Client;
import server.Server;

/**
 * TODO Write file header.
 * @author Jonathan Juursema & Peter Wessels
 *
 */
public class App {

	public static void main(String[] args) {
		
		Console.println("Welcome to the SquirtleSquad Qwirkle application!" + System.lineSeparator());
		
		String appType = "";
		
		while (!appType.equals("server") && !appType.equals("client")) {
			appType = Console.readString("Would you like to run a client or a server?" + System.lineSeparator() + "> ");
		}
		
		//Util.clearScreen();

		if (appType.equals("server")) {
			App.server();
		}
		
		if (appType.equals("client")) {
			App.client();
		}
		
		Util.log("debug", "Threads started and main program finished.");

	}
	
	public static void server() {
	
		int port = 0;
		
		while(port < 2000 || port > 3000) {
			port = Console.readInt("What port should the server run on? (2000..3000)" + System.lineSeparator() + "> ");
		}
		
		try {
			new Server(port);
		} catch (IOException e) {
			Console.println("Cannot assign this port number. See below for the reason.");
			Util.log("exception", e.getMessage());
			App.server();
		}
		
	}
	
	public static void client() {
		
		InetAddress host = null;
		
		while (host == null) {
			try {
				host = InetAddress.getByName(Console.readString("What hostname should we connect to?" + System.lineSeparator() + "> "));
			} catch (UnknownHostException e) {
				Console.println("This hostname cannot be resolved. See below for the reason.");
				Util.log("exception", e.getMessage());
			}
		}
		
		int port = 0;
		
		while(port < 2000 || port > 3000) {
			port = Console.readInt("What port should we connect to? (2000..3000)" + System.lineSeparator() + "> ");
		}
		
		try {
			new Client(host, port);
		} catch (IOException e) {
			Console.println("Cannot connect to the server. See below for the reason.");
			Util.log("exception", e.getMessage());
			App.client();
		}	
		
	}

}
