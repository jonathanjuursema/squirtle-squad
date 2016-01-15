package client;

import java.net.Socket;

/**
 * TODO File header.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
public class ConnectionHandler extends networking.ConnectionHandler {
	
	private Client client;

	public ConnectionHandler(Socket socket, Client client) {
		super(socket);
		this.client = client;
	}

	@Override
	public void parse(String command, String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public void shutdown(String reason) {
		// TODO Auto-generated method stub

	}

}
