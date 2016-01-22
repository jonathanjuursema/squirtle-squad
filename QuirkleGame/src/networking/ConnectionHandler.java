package networking;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Arrays;

import application.Util;
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
		Util.log("debug", "A new connection has been established to "
						+ this.socket.getRemoteSocketAddress() + ".");
		try {
			this.rx = new BufferedReader(new InputStreamReader(this.socket.getInputStream(),
							Charset.forName(Protocol.Server.Settings.ENCODING)));
			this.tx = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream(),
							Charset.forName(Protocol.Server.Settings.ENCODING)));
		} catch (IOException e) {
			Util.log(e);
			this.shutdown("Unrecoverable IOException.");
		}
	}

	/**
	 * The threaded code for listening to remote host commands.
	 */
	public void run() {
		Util.log("debug", "Ready to receive commands from remote.");
		boolean running = true;
		while (running) {
			try {
				String command = this.rx.readLine();
				if (command.length() > 2) { // WHY ON EARTH DO WE SEPERATE
											// COMMANDS
					// WITH TWO NEWLINES???
					Util.log("rx", command);
					// Split command in arguments
					String[] args = command
									.split(String.valueOf(Protocol.Server.Settings.DELIMITER));
					// Send command and arguments to the parser
					this.parse(args[0], Arrays.copyOfRange(args, 1, args.length));
				}
			} catch (IOException e) {
				Util.log(e);
				this.shutdown("Unrecoverable IOException.");
				running = false;
			}

		}
		Util.log("debug", "Socket closed.");
	}

	/**
	 * Send a command to the remote host. This function takes care of all
	 * formatting and protocols.
	 * 
	 * @param command
	 *            The command.
	 * @param args
	 *            An array of arguments. Can be empty.
	 */
	public void send(String command, String[] args) {
		String message = "";
		// Initialise message
		message = message.concat(command)
						.concat(String.valueOf(Protocol.Server.Settings.DELIMITER));
		// Append arguments to command
		for (String arg : args) {
			message = message.concat(arg)
							.concat(String.valueOf(Protocol.Server.Settings.DELIMITER));
		}
		// Trim last seperator
		message = message.substring(0, message.length() - 1);
		// Write
		try {
			this.tx.write(message + Protocol.Server.Settings.COMMAND_END);
			this.tx.flush();
			Util.log("tx", message);
		} catch (IOException e) {
			Util.log(e);
			this.shutdown("Unrecoverable IOException.");
		}
	}

	/**
	 * Send a command to the remote host. This function takes one String as an
	 * argument, and will convert it to an array of one argument to be passed to
	 * the normal send command.
	 * 
	 * @param command
	 *            The command.
	 * @param arg
	 *            One String, will be converted to an array of one item.
	 */
	public void send(String command, String arg) {
		this.send(command, new String[] { arg });
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
	 * @throws UnrecoverableErrorException
	 */
	public abstract void shutdown(String reason);

	/**
	 * @return the socket
	 */
	public Socket getSocket() {
		return socket;
	}

}
