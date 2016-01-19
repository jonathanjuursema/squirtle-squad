package server;

import java.net.Socket;

import application.Util;
import protocol.Protocol;

public class ServerConnectionHandler extends networking.ConnectionHandler {

	private Server server;
	private Player player = null;

	public ServerConnectionHandler(Server server, Socket socket) {
		super(socket);
		this.server = server;
		this.run();
	}

	@Override
	public void parse(String command, String[] args) {
		switch (command) {
		case Protocol.Client.HALLO:
			if (args.length < 1) {
				this.send(Protocol.Server.ERROR, new String[] { "8", "Too few arguments." });
			}
			registerClient(args[0]);
			// TODO Implement server functions argument..
		}
	}

	@Override
	public void shutdown(String reason) {
		// TODO Auto-generated method stub

	}

	private void registerClient(String name) {
		if (!this.server.isUniqueName(name)) {
			this.send(Protocol.Server.ERROR, new String[] { "4", "Name already exists." });
			return;
		}
		this.send(Protocol.Server.HALLO, new String[] { "SquirtleSquade", "" });
		this.player = new Player(name);
		Util.log("info", "New player connected: " + this.player.getName());
	}

}
