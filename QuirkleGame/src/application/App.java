package application;

import java.io.IOException;

import client.Client;
import server.Server;

/**
 * TODO Write file header.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
public class App {
	
	public static final boolean DEBUG = true;
	public static final boolean VERBOSE = true;

	public static final String name = "SquirtleSquad";

	public static void main(String[] args) {

		Util.println("Welcome to the " + name + " Qwirkle application!"
						+ System.lineSeparator());

		String appType = "";

		while (!appType.equals("server") && !appType.equals("client")) {
			appType = Util.readString("Would you like to run a client or a server?"
							+ System.lineSeparator() + "> ");
		}

		// Util.clearScreen();

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

		while (port < 2000 || port > 3000) {
			port = Util.readInt("What port should the server run on? (2000..3000)"
							+ System.lineSeparator() + "> ");
		}

		try {
			new Server(port);
		} catch (IOException e) {
			Util.println("Cannot assign this port number. See below for the reason.");
			Util.log(e);
			App.server();
		}

	}

	public static void client() {

		try {
			new Client();
		} catch (IOException e) {
			Util.println("Cannot connect to the server. See below for the reason.");
			Util.log(e);
			App.client();
		}

	}

}
