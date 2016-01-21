package client;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import application.Console;
import application.Util;
import exceptions.HandLimitReachedExeption;
import exceptions.SquareOutOfBoundsException;
import game.BoardSquare;
import game.Hand;
import game.Tile;
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
				client.pushErrorMessage("It is not your turn at the moment, wait for your turn please.");
				break;
			case 2: // not your stone
				client.pushErrorMessage("This tile is not your tile, please pick a stone of your own hand.");
				break;
			case 3: // not that many stones available
				client.pushErrorMessage("The bag doesn't have enough tiles to complete this action.");
				break;
			case 4: // name exists
				this.client.register();
				break;
			case 5: // not challengable
				client.pushErrorMessage("This person cannot be challenged at the moment.");
				break;
			case 6: // challenge refused
				client.pushErrorMessage("The person has refused your challenge.");
				break;
			case 7: // invalid move
				client.pushErrorMessage("This move is invalid.");
				break;
			default:
				client.pushErrorMessage(args[1]);
				Util.log("protocol", "Recevied an generic error from the server: " + args[1]);
				break;
			}
			break;
		case Protocol.Server.ADDTOHAND:
			List<Tile> addList = new ArrayList<Tile>();
			for (String tile : args) {
				addList.add(new Tile(tile.charAt(0), tile.charAt(1)));
			}
			try {
				client.getHand().addTohand(addList);
			} catch (HandLimitReachedExeption e) {
				client.pushErrorMessage(
						"Server and Client are out of sync, waiting for server to disqualify you. Sorry :)");
				Util.log("debug", e.getMessage() + ", waiting for server to disqualify.");
			}
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
			client.updateBoard(args);
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
