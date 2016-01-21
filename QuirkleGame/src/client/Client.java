package client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import application.Console;
import protocol.Protocol;

/**
 * TODO Write file header.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
public class Client {

	private Socket socket;
	private ClientConnectionHandler server;

	String nickname = "";

	public Client(InetAddress host, int port) throws IOException {

		this.socket = new Socket(host, port);
		this.server = new ClientConnectionHandler(this.socket, this);
		this.server.start();
		this.register();

	}

	/**
	 * Register a client with the server.
	 */
	public void register() {

		nickname = Console.readString(
						"What nickname would you like to use?" + System.lineSeparator() + "> ");
		this.server.send(Protocol.Client.HALLO, new String[] { nickname, "" });

	}

	/**
	 * Main functionality of the client.
	 */
	public void start() {

	}

}
