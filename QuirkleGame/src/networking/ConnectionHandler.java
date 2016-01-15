package networking;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Arrays;

import protocol.Protocol;

/**
 * The abstract ConnectionHandler provides functionality for both the client and
 * server connection handlers. Both can then apply their own parse and shutdown
 * methods.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
public abstract class ConnectionHandler extends Thread {

	private BufferedReader rx;
	private BufferedWriter tx;

	private Socket socket;

	/**
	 * Constructs an abstract ConnectionHandler for a given socket.
	 * 
	 * @param socket
	 *            The socket.
	 */
	public ConnectionHandler(Socket socket) {
		this.socket = socket;
		try {
			this.rx = new BufferedReader(new InputStreamReader(this.socket.getInputStream(),
							Charset.forName(Protocol.Server.Settings.ENCODING)));
			this.tx = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
		} catch (IOException e) {
			ConnectionHandler.log("error",
							"IOException caught while setting up rx and tx: " + e.getMessage());
			this.shutdown("Unrecoverable IOException.");
		}
	}

	/**
	 * A general log method for connection related issues. Policy is to keep
	 * loggin to a minimal. Only critical logging should occur. Messages are
	 * displayed as follows: [type] message
	 * 
	 * @param type
	 *            The type of message. The developer is free to choose their own
	 *            type.
	 * @param message
	 *            The message.
	 */
	public static void log(String type, String message) {
		System.out.println("[" + type + "] " + message);
	}

	/**
	 * The threaded code for listening to server commands.
	 */
	public void run() {
		ConnectionHandler.log("info", "Ready to receive commands from server.");
		boolean running = true;
		while (running) {
			try {
				String[] command = this.rx.readLine()
								.split(String.valueOf(Protocol.Server.Settings.DELIMITER));
				this.parse(command[0], Arrays.copyOfRange(command, 1, command.length));
			} catch (IOException e) {
				ConnectionHandler.log("error",
								"IOException caught while reading commands: " + e.getMessage());
				this.shutdown("Unrecoverable IOException.");
			}

		}
	}

	/**
	 * Send a command to the server. This function takes care of all formatting
	 * and protocols.
	 * 
	 * @param command
	 *            The command.
	 * @param args
	 *            An array of arguments. Can be empty.
	 */
	public void send(String command, String[] args) {
		String message = "";
		message.concat(command).concat(String.valueOf(Protocol.Server.Settings.DELIMITER));
		for (String arg : args) {
			message.concat(arg).concat(String.valueOf(Protocol.Server.Settings.DELIMITER));
		}
		try {
			this.tx.write(message);
			this.tx.newLine();
			this.tx.flush();
		} catch (IOException e) {
			ConnectionHandler.log("error",
							"IOException caught while sending commands: " + e.getMessage());
			this.shutdown("Unrecoverable IOException.");
		}
	}

	/**
	 * Parses a given command with given arguments.
	 * 
	 * @param command
	 *            The command.
	 * @param args
	 *            Arguments to the command.
	 */
	public abstract void parse(String command, String[] args);

	/**
	 * Commences shutdown for an application for the specified reason.
	 * 
	 * @param reason
	 *            The reason.
	 */
	public abstract void shutdown(String reason);

}
