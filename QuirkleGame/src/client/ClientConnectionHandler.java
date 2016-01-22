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

	Client client;

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
			client.unParseErrorMessage(args);
			break;
		case Protocol.Server.ADDTOHAND:
			client.addTilesToHand(args);
			break;
		case Protocol.Server.CHAT:
			client.pushChatMessage(args[0]);
			break;
		case Protocol.Server.DECLINEINVITE:
			client.pushErrorMessage("Sorry, the challenged player does not accept your invite.");
			break;
		case Protocol.Server.GAME_END:
			client.pushErrorMessage("The game is finished.");
			break;
		case Protocol.Server.HALLO:
			client.pushErrorMessage("The server says hello.");
			// TODO: arguments specifies which functionality is suitable
			break;
		case Protocol.Server.INVITE:
			// TODO
			break;
		case Protocol.Server.LEADERBOARD:
			// TODO
			break;
		case Protocol.Server.MOVE:
			// TODO: Only update if in game
			client.unParseTurn(args);
			break;
		case Protocol.Server.OKWAITFOR:
			client.pushErrorMessage("Waiting for more players to enter..");
			break;
		case Protocol.Server.STARTGAME:
			client.enterGame();
			client.pushMessage("Please select your first best move.");
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
