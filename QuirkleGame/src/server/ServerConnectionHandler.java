package server;

import java.io.IOException;
import java.net.Socket;

import application.App;
import application.Util;
import exceptions.AlreadyChallengedSomeoneException;
import exceptions.IllegalMoveException;
import exceptions.IllegalTurnException;
import exceptions.NotInGameException;
import exceptions.NotYourTurnException;
import exceptions.PlayerAlreadyInGameException;
import exceptions.PlayerCannotBeChallengedException;
import exceptions.PlayerIsNoChallengeeException;
import exceptions.SquareOutOfBoundsException;
import exceptions.TileNotInHandException;
import exceptions.TooManyPlayersException;
import networking.ConnectionHandler;
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

		if (!command.equals(Protocol.Client.HALLO) && this.player == null) {
			this.send(Protocol.Server.ERROR, new String[] { "8", "RegisterFirst" });
			return;
		}

		switch (command) {

		case Protocol.Client.ERROR:
			switch (args[1]) {
			default:
				Util.log("protocol", "Recevied an generic error from the client: " + args[2]);
				break;
			}

		case Protocol.Client.ACCEPTINVITE:
			try {
				this.server.acceptInvite(this.player);
			} catch (PlayerIsNoChallengeeException e1) {
				this.send(Protocol.Server.ERROR, new String[] { "8", "YouHaveNotBeenChallenged" });
			} catch (PlayerAlreadyInGameException e) {
				this.send(Protocol.Server.ERROR, new String[] { "8", "PlayerIsAlreadyInGame" });
			}
			break;

		case Protocol.Client.CHANGESTONE:
			if (args.length < 1) {
				this.send(Protocol.Server.ERROR, new String[] { "8", "TooFewArguments" });
			} else {
				try {
					this.getPlayer().playSwap(args);
				} catch (NotYourTurnException e) {
					this.send(Protocol.Server.ERROR, new String[] { "1", "NotYourTurn" });
				} catch (NotInGameException e) {
					this.send(Protocol.Server.ERROR, new String[] { "1", "YourNotInAGame" });
				} catch (IllegalTurnException e) {
					this.send(Protocol.Server.ERROR, new String[] { "7", "InvalidSwap" });
				} catch (TileNotInHandException e) {
					this.send(Protocol.Server.ERROR, new String[] { "2", "NotYourStone" });
				}
			}
			break;

		case Protocol.Client.CHAT:
			if (args.length < 1) {
				this.send(Protocol.Server.ERROR, new String[] { "8", "TooFewArguments" });
			} else if (!this.player.canChat()) {
				this.send(Protocol.Server.ERROR, new String[] { "8", "#MixedSignals" });
			} else {
				this.server.chat(this.getPlayer(),
								Util.joinStringArray(args, Protocol.Server.Settings.DELIMITER));
			}
			break;

		case Protocol.Client.DECLINEINVITE:
			try {
				this.server.declineInvite(this.player);
			} catch (PlayerIsNoChallengeeException e1) {
				this.send(Protocol.Server.ERROR, new String[] { "8", "YouHaveNotBeenChallenged" });
			}
			break;

		case Protocol.Client.GETLEADERBOARD:
			if (!this.player.canLeaderBoard()) {
				this.send(Protocol.Server.ERROR, new String[] { "8", "#MixedSignals" });
			} else {
				this.send(Protocol.Server.LEADERBOARD, this.server.leaderboardToProtocol());
			}
			break;

		case Protocol.Client.GETSTONESINBAG:
			this.send(Protocol.Server.STONESINBAG,
							new String[] { "" + this.getPlayer().getGame().getTilesInBag() });
			break;

		case Protocol.Client.HALLO:
			if (args.length < 1) {
				this.send(Protocol.Server.ERROR, new String[] { "8", "TooFewArguments" });
			} else {
				registerClient(args);
			}
			break;

		case Protocol.Client.INVITE:
			if (args.length < 1) {
				this.send(Protocol.Server.ERROR, new String[] { "8", "TooFewArguments" });
			} else if (!this.player.canInvite()) {
				this.send(Protocol.Server.ERROR, new String[] { "8", "#MixedSignals" });
			} else {
				try {
					this.server.challenge(this.player, args[0]);
				} catch (PlayerCannotBeChallengedException e) {
					this.send(Protocol.Server.ERROR, new String[] { "5", "CannotChallengeThisPlayer" });
				} catch (AlreadyChallengedSomeoneException e) {
					this.send(Protocol.Server.ERROR, new String[] { "8", "AlreadyChallenging" });
				}
			}
			break;

		case Protocol.Client.MAKEMOVE:
			if (args.length < 1) {
				this.send(Protocol.Server.ERROR, new String[] { "8", "TooFewArguments" });
			} else {
				try {
					this.getPlayer().placeMove(args);
				} catch (NotYourTurnException e) {
					this.send(Protocol.Server.ERROR, new String[] { "1", "NotYourTurn" });
				} catch (NotInGameException e) {
					this.send(Protocol.Server.ERROR, new String[] { "1", "YourNotInAGame" });
				} catch (NumberFormatException e) {
					this.send(Protocol.Server.ERROR, new String[] { "8", "NotANumber" });
				} catch (SquareOutOfBoundsException e) {
					this.send(Protocol.Server.ERROR,
									new String[] { "7", "CoordinatesOutOfBounds" });
				} catch (IllegalMoveException e) {
					this.send(Protocol.Server.ERROR, new String[] { "7", "IllegalMove" });
				} catch (IllegalTurnException e) {
					this.send(Protocol.Server.ERROR, new String[] { "7", "IllegalTurn" });
				}
			}
			break;

		case Protocol.Client.QUIT:
			this.shutdown("On client request.");
			break;

		case Protocol.Client.REQUESTGAME:
			if (args.length < 1) {
				this.send(Protocol.Server.ERROR, new String[] { "8", "TooFewArguments" });
			}
			try {
				try {
					this.server.findGameFor(this.getPlayer(), Integer.parseInt(args[0]));
				} catch (PlayerAlreadyInGameException e) {
					this.send(Protocol.Server.ERROR, new String[] { "8", "AlreadyInGame" });
				}
			} catch (NumberFormatException e) {
				this.send(Protocol.Server.ERROR, new String[] { "8", "InvalidNumberOfPlayers" });
			} catch (TooManyPlayersException e) {
				this.send(Protocol.Server.ERROR, new String[] { "8", "NotSoManyPlayersAllowed" });
			}
			break;

		default:
			this.send(Protocol.Server.ERROR, new String[] { "8", "UnknownCommand" });
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
			this.send(Protocol.Server.ERROR, new String[] { "4", "AlreadyExists" });
			return;
		}

		if (args[0].length() > 15) {
			this.send(Protocol.Server.ERROR, new String[] { "4", "TooLong" });
			return;
		}

		String[] resp = new String[Server.FUNCTIONS.length + 1];
		resp[0] = App.name;
		for (int i = 1; i <= Server.FUNCTIONS.length; i++) {
			resp[i] = Server.FUNCTIONS[i - 1];
		}

		this.send(Protocol.Server.HALLO, resp);

		this.player = new ServerPlayer(args[0], this);

		for (int i = 1; i < args.length; i++) {
			switch (args[i]) {
			case "CHALLENGE":
				this.getPlayer().canInvite(true);
				break;
			case "CHAT":
				this.getPlayer().canChat(true);
				break;
			case "LEADERBOARD":
				this.getPlayer().canLeaderBoard(true);
				break;
			}
		}

		this.server.addPlayer(this.getPlayer());
		this.server.playerToLobby(this.getPlayer());
		Util.log("info", "New player connected: " + this.getPlayer().getName());

	}

	@Override
	public void shutdown(String reason) {
		Util.log("debug", "Client socket closed: " + reason);
		if (this.getPlayer() != null) {
			if (this.server.isInGame(this.getPlayer())) {
				this.getPlayer().getGame().disqualify(this.getPlayer());
			}
			this.server.removePlayer(this.getPlayer());
		}
		try {
			this.getSocket().close();
		} catch (IOException e) {
			Util.log(e);
		}
	}

	/**
	 * @return the player
	 */
	public ServerPlayer getPlayer() {
		return player;
	}

}
