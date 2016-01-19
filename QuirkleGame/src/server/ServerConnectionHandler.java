package server;

import java.net.Socket;

public class ServerConnectionHandler extends networking.ConnectionHandler {
	
	private Player player = null;

	public ServerConnectionHandler(Socket socket) {
		super(socket);
		this.run();
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
