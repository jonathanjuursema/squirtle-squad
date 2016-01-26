package client;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.Util;
import exceptions.HandLimitReachedExeption;
import exceptions.IllegalMoveException;
import exceptions.IllegalTurnException;
import exceptions.SquareOutOfBoundsException;
import exceptions.TileNotInBagException;
import exceptions.TileNotInHandException;
import exceptions.TooFewTilesInBagException;
import exceptions.TooManyTilesInBag;
import game.Bag;
import game.Board;
import game.Hand;
import game.Move;
import game.Tile;
import game.Turn;
import players.ClientPlayer;
import players.HumanPlayer;
import players.Player;
import protocol.Protocol;
import views.TextView;
import views.View;

/**
 * The client will take the responsibility to inform the player about the
 * commands send by the server. The client also contains a copy of the board and
 * update this board according to the moves by all the players. Each player, no
 * matter if the player is a computer player or a human player has its own
 * client. This player is of type ClientPlayer.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
public class Client {

	private View view;

	private Socket socket;
	private ClientConnectionHandler server;

	private Board boardCopy;
	private ClientPlayer player;
	private Turn turn;

	private String name;

	List<Tile> usedInPrevious;

	public static enum Status {
		INITIALIZING, IN_LOBBY, IN_GAME, IN_TURN, WAITINGFORGAME, IN_GAME_INITIAL
	};

	public static final String[] FUNCTIONS = { "CHAT", "CHALLENGE", "LEADERBOARD" };

	public Status status;

	private Map<String, Integer> scores;

	public Client() throws IOException {

		this.status = Client.Status.INITIALIZING;

		this.view = new TextView(this);

		this.socket = new Socket(view.askForHost(), view.askForPort());

		this.server = new ClientConnectionHandler(this.socket, this);
		this.server.start();

		this.usedInPrevious = new ArrayList<Tile>();

		this.scores = new HashMap<String, Integer>();

		register();

	}

	/**
	 * Register a client to the server
	 */
	public void register() {

		this.name = this.view.requestNickname();

		String[] resp = new String[Client.FUNCTIONS.length + 1];
		resp[0] = this.name;
		for (int i = 1; i <= Client.FUNCTIONS.length; i++) {
			resp[i] = Client.FUNCTIONS[i - 1];
		}

		this.server.send(Protocol.Client.HALLO, resp);

	}

	public void start() {

		this.getView().connected();
		this.status = Client.Status.IN_LOBBY;

	}

	public void requestGame(int no) {
		if (this.status != Client.Status.IN_LOBBY) {
			this.view.sendNotification("error", "You are not in the lobby.");
			return;
		}

		this.preparePlayer();

		this.status = Client.Status.WAITINGFORGAME;
		this.server.send(Protocol.Client.REQUESTGAME, new String[] { "" + no });
	}

	private void preparePlayer() {
		String playerType = this.view.askForPlayerType();

		while (!playerType.equals("human") && !playerType.equals("computer")) {
			playerType = this.view.askForPlayerType();
		}

		if (playerType.equals("human")) {
			this.player = new HumanPlayer(this.name, this);
		} else if (playerType.equals("computer")) {
			this.player = new HumanPlayer(this.name, this);
		}

		Hand hand = new Hand();
		this.player.assignHand(hand);

		this.player.getHand().hardResetHand();

		this.getPlayerHand().addObserver(this.getView());
	}

	public void startGame() {

		if (this.status == Client.Status.WAITINGFORGAME) {
			if (this.getPlayerHand().getTilesInHand().size() > 0) {
				this.status = Client.Status.IN_GAME_INITIAL;
				this.boardCopy = new Board();
				this.boardCopy.addObserver(getView());
				this.turn = new Turn(boardCopy, this.player);
				this.turn.addObserver(getView());
				this.turn.getBoardCopy().addObserver(getView());
				this.player.giveTurn();
				this.getView().startGame();
			} else {
				Util.log("debug", "We wait for the hand to be filled.");
			}
		}

	}

	public void addToHand(String[] tiles) {

		List<Tile> addList = new ArrayList<Tile>();

		for (String tile : tiles) {
			addList.add(new Tile(tile.charAt(0), tile.charAt(1)));
		}

		try {
			this.getPlayerHand().addTohand(addList);
		} catch (HandLimitReachedExeption e) {
			Util.log(e);
		}

		if (this.status == Client.Status.WAITINGFORGAME) {
			this.startGame();
		}

	}

	public void addMove(int tileInHand, int x, int y) {

		if (this.status == Client.Status.IN_TURN || this.status == Client.Status.IN_GAME_INITIAL) {

			if (tileInHand <= this.getPlayerHand().getTilesInHand().size()) {
				Tile t = this.getPlayerHand().getTilesInHand().get(tileInHand - 1);
				try {
					this.turn.addMove(new Move(t, this.boardCopy.getSquare(x, y)));
				} catch (SquareOutOfBoundsException | IllegalMoveException
								| IllegalTurnException e) {
					this.getView().sendNotification("Cannot do this move: " + e.getMessage());
					return;
				}

				try {
					this.getPlayerHand().removeFromHand(t);
				} catch (TileNotInHandException e) {
					Util.log(e);
				}

				this.usedInPrevious.add(t);
			} else {
				this.getView().sendNotification("This stone is not in your hand!");
			}

		} else {

			this.getView().sendNotification("You cannot do that now.");

		}

	}

	public void requestSwap(String tile) {

		if (this.status == Client.Status.IN_TURN) {

			int no = Integer.parseInt(tile);
			if (no <= this.getPlayerHand().getTilesInHand().size()) {
				Tile t = this.getPlayerHand().getTilesInHand().get(no - 1);
				try {
					this.turn.addSwapRequest(t);
				} catch (IllegalTurnException e) {
					this.getView().sendNotification("This swap is illegal: " + e.getMessage());
					Util.log(e);
					this.turn.getSwap().clear();
					return;
				}
				try {
					this.getPlayerHand().removeFromHand(t);
				} catch (TileNotInHandException e) {
					Util.log(e);
				}

				this.usedInPrevious.add(t);
			}

			Util.log("debug", "Registered swap request.");

		} else {

			this.getView().sendNotification("You cannot do that now.");

		}

	}

	public void submitTurn() {
		if (this.turn.isMoveRequest()) {
			String[] args = new String[this.turn.getMoves().size()];
			for (int i = 0; i < this.turn.getMoves().size(); i++) {
				Move m = this.turn.getMoves().get(i);
				args[i] = m.getTile().toProtocol() + Protocol.Server.Settings.DELIMITER2
								+ m.getPosition().getX() + Protocol.Server.Settings.DELIMITER2
								+ m.getPosition().getY();
			}
			this.server.send(Protocol.Client.MAKEMOVE, args);
		} else if (this.turn.isSwapRequest()) {
			String[] args = new String[this.turn.getSwap().size()];
			for (int i = 0; i < this.turn.getSwap().size(); i++) {
				args[i] = this.turn.getSwap().get(i).toProtocol();
			}
			this.server.send(Protocol.Client.CHANGESTONE, args);
		} else {
			this.getView().sendNotification("You have not specified a turn yet!");
			return;
		}
		this.getView().sendNotification("Waiting for server confirmation...");
		this.status = Client.Status.IN_GAME;
	}

	public synchronized void registerTurn(String[] args) {

		this.status = Client.Status.IN_GAME;

		Player tempPlayer = new HumanPlayer("Temp", this);
		tempPlayer.assignHand(new Hand());
		Turn turn = new Turn(boardCopy, tempPlayer);

		if (args.length > 2) {

			for (int i = 2; i < args.length; i++) {

				String arg = args[i];

				String[] move = arg
								.split("\\" + String.valueOf(Protocol.Server.Settings.DELIMITER2));

				Tile tile = new Tile(move[0].charAt(0), move[0].charAt(1));

				try {
					tempPlayer.getHand().addToHand(tile);
				} catch (HandLimitReachedExeption e) {
					Util.log("error", "Could not parse server move.");
					Util.log(e);
				}

				int x = Integer.parseInt(move[1]);
				int y = Integer.parseInt(move[2]);

				try {
					turn.addMove(new Move(tile, this.boardCopy.getSquare(x, y)));
				} catch (SquareOutOfBoundsException | IllegalMoveException
								| IllegalTurnException e) {
					Util.log("error", "Could not parse server move.");
					Util.log(e);
				}
			}

			Bag bag = new Bag();
			bag.fill();

			try {
				turn.applyTurn(boardCopy, bag);
			} catch (TooFewTilesInBagException | TileNotInBagException | TooManyTilesInBag
							| TileNotInHandException | IllegalTurnException
							| SquareOutOfBoundsException | HandLimitReachedExeption e) {
				Util.log("error", "Could not parse server move.");
				Util.log(e);
			}

			try {
				this.getView().sendNotification(args[0] + " played " + (args.length - 2)
								+ " tiles for " + turn.calculateScore() + " points.");
			} catch (SquareOutOfBoundsException e) {
				Util.log(e);
			}

			boolean noScoresYet = true;
			for (String p : scores.keySet()) {
				if (p.equals(args[0])) {
					noScoresYet = false;
					Integer score;
					try {
						score = scores.get(p) + turn.calculateScore();
						scores.put(p, score);
					} catch (SquareOutOfBoundsException e) {
						Util.log(e);
					}
				}
			}
			if (noScoresYet) {
				try {
					scores.put(args[0], turn.calculateScore());
				} catch (SquareOutOfBoundsException e) {
					Util.log(e);
				}
			}

		} else {

			this.getView().sendNotification(args[0] + " swapped tiles.");

		}

		if (!args[0].equals(this.name)) {
			try {
				this.getPlayerHand().addTohand(usedInPrevious);
			} catch (HandLimitReachedExeption e) {
				Util.log(e);
			}
		}

		if (args[1].equals(this.name)) {
			this.turn = new Turn(boardCopy, this.player);
			this.turn.addObserver(getView());
			this.turn.getBoardCopy().addObserver(getView());
			this.player.giveTurn();
		} else if (args[0].equals(this.name)) {
			usedInPrevious.clear();
		}

	}

	public void endGame(String[] args) {
		switch (args[0]) {
		case "WIN":
			this.getView().sendNotification("The game is over. The winner is: "
							+ (args[1].equals(this.name) ? "YOU!" : args[1]));
			break;
		case "DRAW":
			this.getView().sendNotification("The game is over and ended in a draw.");
			break;
		case "DISCONNECT":
			this.getView().sendNotification(
							"The game has been ended by the server: " + args[1] + ".");
			break;
		}
		this.status = Client.Status.IN_LOBBY;
	}

	public void chatFromClient(String[] args) {
		String message = "";
		for (String arg : args) {
			message = message.concat(" " + arg);
		}
		this.server.send(Protocol.Client.CHAT, new String[] { message });
	}

	public void chatFromServer(String[] args) {
		String message = "";
		for (String arg : args) {
			message = message.concat(arg);
		}
		this.getView().showChat(message);
	}

	public void invite(String string) {
		if (this.status == Client.Status.IN_LOBBY) {
			this.status = Client.Status.WAITINGFORGAME;
			this.preparePlayer();
			this.server.send(Protocol.Client.INVITE, new String[] { string });
		} else {
			this.getView().sendNotification("You can only invite if you are in the lobby.");
		}
	}

	public void declineInvite() {
		this.server.send(Protocol.Client.DECLINEINVITE, new String[] {});
	}

	public void acceptInvite() {
		if (this.status == Client.Status.IN_LOBBY) {
			this.status = Client.Status.WAITINGFORGAME;
			this.preparePlayer();
			this.server.send(Protocol.Client.ACCEPTINVITE, new String[] {});
		} else {
			this.getView().sendNotification("You can only accept invites if you are in the lobby.");
		}
	}

	public void leaderboard(String[] args) {
		this.getView().sendLeaderboard(args);
	}

	public void requestLeaderboard() {
		this.server.send(Protocol.Client.GETLEADERBOARD, new String[] {});
	}

	public View getView() {
		return this.view;
	}

	public Board getBoard() {
		return this.boardCopy;
	}

	public Hand getPlayerHand() {
		if (this.player == null) {
			return null;
		} else {
			return this.player.getHand();
		}
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	public Player getPlayer() {
		return this.player;
	}

	/**
	 * @return the turn
	 */
	public Turn getTurn() {
		return turn;
	}

	public void gotInvite(String string) {
		this.getView().gotInvite(string);
	}

	public void undoRemoveFromHand() {
		try {
			this.getPlayerHand().addTohand(usedInPrevious);
		} catch (HandLimitReachedExeption e) {
			Util.log(e);
		}
		usedInPrevious.clear();
		this.turn = new Turn(boardCopy, this.player);
		this.player.giveTurn();
	}

	public void declineInviteFromServer() {
		this.status = Client.Status.IN_LOBBY;
		this.getView().sendNotification("Your challenge has been refused.");
	}

	public void revertTurn() {
		this.turn = new Turn(boardCopy, this.player);
		this.turn.addObserver(getView());
		this.turn.getBoardCopy().addObserver(getView());
		try {
			this.getPlayerHand().addTohand(usedInPrevious);
		} catch (HandLimitReachedExeption e) {
			Util.log(e);
		}
		this.usedInPrevious.clear();
	}

}
