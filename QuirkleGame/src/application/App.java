package application;

import java.io.IOException;

import client.Client;
import server.Server;

/**
 * The main class of the application is the App.Java class. This class is the
 * only class that contains the main method and can be run by the user. The app
 * defines if debug messages, raw communication information and self-defined
 * messages of exceptions should be printed in the console. The class starts the
 * client or the server according to the input of the user.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
public class App {

	public static final boolean DEBUG = false;
	public static final boolean VERBOSE = false;
	public static final boolean EXCEPTION = false;

	public static final String name = "SquirtleSquad";

	/**
	 * The main method of the application. This method starts the application
	 * and asks the user if the client or server needs to be started.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		Util.println("Welcome to the " + name + " Qwirkle application!" + System.lineSeparator());

		String appType = "";

		while (!appType.equals("server") && !appType.equals("client")) {
			appType = Util.readString("Would you like to run a client or a server?" + System.lineSeparator() + "> ");
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

	/**
	 * Starting the server application.
	 */
	public static void server() {

		int port = 0;

		while (port < 2000 || port > 3000) {
			port = Util.readInt("What port should the server run on? (2000..3000)" + System.lineSeparator() + "> ");
		}

		try {
			new Server(port);
		} catch (IOException e) {
			Util.println("Cannot assign this port number. See below for the reason.");
			Util.log(e);
			App.server();
		}

	}

	/**
	 * Starting the client application.
	 */
	public static void client() {

		try {
			new Client();
		} catch (IOException e) {
			Util.println("Cannot connect to the server: " + e.getMessage());
			Util.log(e);
			App.client();
		}

	}

}
