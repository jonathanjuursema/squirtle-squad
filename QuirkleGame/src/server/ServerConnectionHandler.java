package server;

import java.io.IOException;
import java.net.Socket;

import application.Util;
import networking.ConnectionHandler;
import protocol.Protocol;

public class ServerConnectionHandler extends ConnectionHandler {

	private Server server;
	private Player player = null;

	/**
	 * Initialize a new ConnectionHandler for the server.
	 * 
	 * @param server
	 *            The server object.
	 * @param socket
	 *            The socket for this handler.
	 */
	public ServerConnectionHandler(Server server, Socket socket) {
		super(socket);
		this.server = server;
	}
	
	public void run() {
		super.run();
	}

	@Override
	public void parse(String command, String[] args) {
		switch (command) {
		case Protocol.Client.ERROR:
			switch (args[1]) {
			default:
				Util.log("protocol", "Recevied an generic error from the server: " + args[2]);
				break;
			}
		case Protocol.Client.HALLO:
			if (args.length < 1) {
				this.send(Protocol.Server.ERROR, new String[] { "8", "Too few arguments." });
			}
			registerClient(args[0]);
			break;
		default:
			Util.log("protocol", "Received an unknown command from the client: " + command);
			break;
		}
	}

	/**
	 * Register a client by making a new Player object and assigning that player
	 * to the lobby.
	 * 
	 * @param name
	 *            The username.
	 */
	private void registerClient(String name) {
		if (!this.server.isUniqueName(name)) {
			this.send(Protocol.Server.ERROR, new String[] { "4", "Name already exists." });
			return;
		}
		this.send(Protocol.Server.HALLO, new String[] { "SquirtleSquade", Server.FUNCTIONS });
		this.player = new Player(name);
		this.server.addPlayer(this.player);
		this.server.playerToLobby(this.player);
		Util.log("info", "New player connected: " + this.player.getName());
	}

	@Override
	public void shutdown(String reason) {
		Util.log("debug", "Client socket closed: " + reason);
		if (this.player != null) {
			if (this.player.getStatus() == Player.Status.IN_GAME) {
				this.player.getGame().disqualify(this.player);
			}
			this.server.removePlayer(this.player);
		}
		try {
			this.getSocket().close();
		} catch (IOException e) {
			Util.log("exception",
							"An IOException was thrown while closing socket: " + e.getMessage());
		}
	}

}
