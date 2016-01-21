package server;

import java.io.IOException;
import java.net.Socket;

import application.App;
import application.Util;
import client.Client;
import exceptions.NotYourTurnException;
import exceptions.PlayerAlreadyInGameException;
import exceptions.TooManyPlayersException;
import networking.ConnectionHandler;
import players.Player;
import players.ServerPlayer;
import protocol.Protocol;

public class ServerConnectionHandler extends ConnectionHandler {

	private Server server;
	private ServerPlayer player = null;

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
		case Protocol.Client.ACCEPTINVITE:
			// TODO
			break;
		case Protocol.Client.CHANGESTONE:
			try {
				this.player.playSwap(args);
			} catch (NotYourTurnException e) {
				this.send(Protocol.Server.ERROR, new String[] { "1", "NOT_YOUR_TURN" });
			}
			break;
		case Protocol.Client.CHAT:
			// TODO
			break;
		case Protocol.Client.DECLINEINVITE:
			// TODO
			break;
		case Protocol.Client.GETLEADERBOARD:
			// TODO
			break;
		case Protocol.Client.GETSTONESINBAG:
			this.send(Protocol.Server.STONESINBAG,
							new String[] { "" + this.player.getGame().getTilesInBag() });
			break;
		case Protocol.Client.HALLO:
			if (args.length < 1) {
				this.send(Protocol.Server.ERROR, new String[] { "8", "TOO_FEW_ARGUMENTS" });
			}
			registerClient(args);
			break;
		case Protocol.Client.INVITE:
			// TODO
			break;
		case Protocol.Client.MAKEMOVE:
			try {
				this.player.playMoves(args);
			} catch (NotYourTurnException e) {
				this.send(Protocol.Server.ERROR, new String[] { "1", "NOT_YOUR_TURN" });
			}
			break;
		case Protocol.Client.QUIT:
			this.shutdown("On client request.");
			break;
		case Protocol.Client.REQUESTGAME:
			if (args.length < 1) {
				this.send(Protocol.Server.ERROR, new String[] { "8", "TOO_FEW_ARGUMENTS" });
			}
			try {
				try {
					this.createGameFor(this.player, Integer.parseInt(args[0]));
				} catch (PlayerAlreadyInGameException e) {
					// TODO Auto-generated catch block
				}
			} catch (NumberFormatException e) {
				this.send(Protocol.Server.ERROR, new String[] { "8", "INVALID_NUMBER_OF_PLAYERS" });
			} catch (TooManyPlayersException e) {
				this.send(Protocol.Server.ERROR,
								new String[] { "8", "NOT_SO_MANY_PLAYERS_ALLOWED" });
			}
			break;
		default:
			this.send(Protocol.Server.ERROR, new String[] { "8", "UNKNOWN_COMMAND" });
			Util.log("protocol", "Received an unknown command from the client: " + command);
			break;
		}
	}

	/**
	 * Register a client by making a new Player object and assigning that player
	 * to the lobby.
	 * 
	 * @param args
	 *            The username.
	 */
	private void registerClient(String[] args) {

		if (!this.server.isUniqueName(args[0])) {
			this.send(Protocol.Server.ERROR, new String[] { "4", "ALREADY_EXISTS" });
			return;
		}

		if (args[0].length() > 15) {
			this.send(Protocol.Server.ERROR, new String[] { "4", "TOO_LONG" });
			return;
		}

		String[] resp = new String[Server.FUNCTIONS.length + 1];
		resp[0] = App.name;
		for (int i = 1; i <= Server.FUNCTIONS.length; i++) {
			resp[i] = Server.FUNCTIONS[i - 1];
		}

		this.send(Protocol.Server.HALLO, resp);

		this.player = new ServerPlayer(args[0]);

		for (int i = 1; i < args.length; i++) {
			switch (args[i]) {
			case "CHALLENGE":
				this.player.setCanInvite(true);
				break;
			case "CHAT":
				this.player.setCanChat(true);
				break;
			case "LEADERBOARD":
				this.player.setCanLeaderBoard(true);
				break;
			}
		}

		this.server.addPlayer(this.player);
		this.server.playerToLobby(this.player);
		Util.log("info", "New player connected: " + this.player.getName());

	}

	private void createGameFor(ServerPlayer player, int noOfPlayers) throws TooManyPlayersException, PlayerAlreadyInGameException {
		// TODO Support computer player.
		for (Game game : this.server.getGames()) {
			if (game.getGameState() == Game.GameState.NOTSTARTED
							&& game.getNoOfPlayers() == noOfPlayers) {
				game.addPlayer(player);
			}
		}
		Game game = new Game(this.server, noOfPlayers);
		game.addPlayer(player);
		this.server.addGame(game);
	}

	@Override
	public void shutdown(String reason) {
		Util.log("debug", "Client socket closed: " + reason);
		if (this.player != null) {
			if (this.player.getStatus() == ServerPlayer.Status.IN_GAME) {
				this.player.getGame().disqualify(this.player);
			}
			this.server.removePlayer(this.player);
		}
		try {
			this.getSocket().close();
		} catch (IOException e) {
			Util.log(e);
		}
	}

}
