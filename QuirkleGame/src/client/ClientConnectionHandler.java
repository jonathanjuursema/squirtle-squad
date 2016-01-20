package client;

import java.io.IOException;
import java.net.Socket;

import application.Util;
import networking.ConnectionHandler;
import protocol.Protocol;

/**
 * TODO File header.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
public class ClientConnectionHandler extends ConnectionHandler {
	
	private Client client;

	public ClientConnectionHandler(Socket socket, Client client) {
		super(socket);
		this.client = client;
	}
	
	public void run() {
		super.run();
	}

	@Override
	public void parse(String command, String[] args) {
		switch (command) {
		case Protocol.Client.ERROR:
			Util.log("protocol", "Recevied an error message from the client.");
		default:
			Util.log("protocol", "Received an unknown command from the server: " + command);
		}

	}

	@Override
	public void shutdown(String reason) {
		Util.log("debug", "Server socket closed: " + reason);
		try {
			this.getSocket().close();
		} catch (IOException e) {
			Util.log("exception",
							"An IOException was thrown while closing socket: " + e.getMessage());
		}
		
	}

}
