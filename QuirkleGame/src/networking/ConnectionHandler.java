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
			Util.log("error", "IOException caught while setting up rx and tx: " + e.getMessage());
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
				Util.log("rx", command);
				String[] args = command.split(String.valueOf(Protocol.Server.Settings.DELIMITER));
				this.parse(args[0], Arrays.copyOfRange(args, 1, args.length));
			} catch (IOException e) {
				Util.log("error", "IOException caught while reading commands: " + e.getMessage());
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
		message = message.concat(command)
						.concat(String.valueOf(Protocol.Server.Settings.DELIMITER));
		for (String arg : args) {
			message = message.concat(arg)
							.concat(String.valueOf(Protocol.Server.Settings.DELIMITER));
		}
		try {
			this.tx.write(message + System.lineSeparator());
			this.tx.flush();
			Util.log("tx", message);
		} catch (IOException e) {
			Util.log("error", "IOException caught while sending commands: " + e.getMessage());
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
