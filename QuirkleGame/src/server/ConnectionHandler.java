package server;

import java.net.Socket;

import game.Player;

public class ConnectionHandler extends networking.ConnectionHandler {
	
	private Player player;

	public ConnectionHandler(Socket socket, Player player) {
		super(socket);
		this.player = player;
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
