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
		case Protocol.Server.ERROR:
			switch (Integer.parseInt(args[0])) {
			case 1: // not your turn
				// TODO
				break;
			case 2: // not your stone
				// TODO
				break;
			case 3: // not that many stones available
				// TODO
				break;
			case 4: // name exists
				this.client.register();
				break;
			case 5: // not challengable
				// TODO
				break;
			case 6: // challenge refused
				// TODO
				break;
			case 7: // invalid move
				// TODO
				break;
			default:
				Util.log("protocol", "Recevied an generic error from the server: " + args[1]);
				break;
			}
			break;
		case Protocol.Server.ADDTOHAND:
			// TODO
			break;
		case Protocol.Server.CHAT:
			// TODO
			break;
		case Protocol.Server.DECLINEINVITE:
			// TODO
			break;
		case Protocol.Server.GAME_END:
			// TODO
			break;
		case Protocol.Server.HALLO:
			// TODO
			break;
		case Protocol.Server.INVITE:
			// TODO
			break;
		case Protocol.Server.LEADERBOARD:
			// TODO
			break;
		case Protocol.Server.MOVE:
			// TODO
			break;
		case Protocol.Server.OKWAITFOR:
			// TODO
			break;
		case Protocol.Server.STARTGAME:
			// TODO
			break;
		case Protocol.Server.STONESINBAG:
			// TODO
			break;
		default:
			Util.log("protocol", "Received an unknown command from the server: " + command);
			break;
		}

	}

	@Override
	public void shutdown(String reason) {
		Util.log("debug", "Server socket closed: " + reason);
		try {
			this.getSocket().close();
		} catch (IOException e) {
			Util.log(e);
		}

	}

}
