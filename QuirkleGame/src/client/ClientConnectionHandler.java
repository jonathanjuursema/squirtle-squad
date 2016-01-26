package client;

import java.io.IOException;
import java.net.Socket;

import application.Util;
import networking.ConnectionHandler;
import protocol.Protocol;

/**
 * The ClientConnectionHandler parses the commands received by the server to a
 * method call, called in the client.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
public class ClientConnectionHandler extends ConnectionHandler {

	Client client;

	/**
	 * Constructs a new Client connection handler, with a connection to the server and a Client object.
	 * @param socket The socket with the server connection.
	 * @param client The Client.
	 */
	public ClientConnectionHandler(Socket socket, Client client) {
		super(socket);
		this.client = client;
	}

	/**
	 * Threaded code parsing remote input.
	 */
	public void run() {
		super.run();
	}

	@Override
	public void parse(String command, String[] args) {
		switch (command) {
		case Protocol.Server.ERROR:
			switch (Integer.parseInt(args[0])) {
			case 4:
				this.client.getView().sendNotification("error", "This nickname already exists.");
				this.client.register();
				break;
			case 5:
				this.client.declineInviteFromServer();
				break;
			case 2:
			case 7:
				this.client.undoRemoveFromHand();
				break;
			default:
				this.client.getView().sendNotification("error", args[1]);
				break;
			}
			break;
		case Protocol.Server.ADDTOHAND:
			client.addToHand(args);
			break;
		case Protocol.Server.CHAT:
			client.chatFromServer(args);
			break;
		case Protocol.Server.DECLINEINVITE:
			client.declineInviteFromServer();
			break;
		case Protocol.Server.GAME_END:
			client.endGame(args);
			break;
		case Protocol.Server.HALLO:
			client.start();
			break;
		case Protocol.Server.INVITE:
			client.gotInvite(args[0]);
			break;
		case Protocol.Server.LEADERBOARD:
			client.leaderboard(args);
			break;
		case Protocol.Server.MOVE:
			client.registerTurn(args);
			break;
		case Protocol.Server.OKWAITFOR:
			client.getView().sendNotification(
							"Waiting for " + args[0] + " more players to enter..");
			client.status = Client.Status.WAITINGFORGAME;
			break;
		case Protocol.Server.STARTGAME:
			client.startGame();
			break;
		case Protocol.Server.STONESINBAG:
			client.getView().sendNotification("There are " + args[0] + " stones in the bag.");
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
