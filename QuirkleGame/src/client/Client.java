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
import players.ComputerPlayer;
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
 * client. This player is of type ClientPlayer. A client can only host one human
 * player at a time.
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

	public List<Tile> usedInPrevious;

	public static enum Status {
		INITIALIZING, IN_LOBBY, IN_GAME, IN_TURN, WAITINGFORGAME, IN_GAME_INITIAL
	};

	public static final String[] FUNCTIONS = { "CHAT", "CHALLENGE", "LEADERBOARD" };

	public Status status;

	private Map<String, Integer> scores;

	/**
	 * Constructs a new client and initializes all applicable variables.
	 * 
	 * @throws IOException
	 *             If the connection with the server cannot be established.
	 */
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

	/**
	 * 'Start' the client when it has successfully connected and perform game
	 * related methods.
	 */
	public void start() {

		this.getView().connected();
		this.status = Client.Status.IN_LOBBY;

	}

	/**
	 * Request a game from the server.
	 * 
	 * @param no
	 *            The number of players for the game.
	 */
	public void requestGame(int no) {
		if (this.status != Client.Status.IN_LOBBY) {
			this.view.sendNotification("error", "You are not in the lobby.");
			return;
		}

		this.preparePlayer();

		this.status = Client.Status.WAITINGFORGAME;
		this.server.send(Protocol.Client.REQUESTGAME, new String[] { "" + no });
	}

	/**
	 * Prepare the player object prior to a game. Contains shared code between
	 * challenges and regular game starts.
	 */
	private void preparePlayer() {
		String playerType = this.view.askForPlayerType();

		while (!playerType.equals("human") && !playerType.equals("computer")) {
			playerType = this.view.askForPlayerType();
		}

		if (playerType.equals("human")) {
			this.player = new HumanPlayer(this.name, this);
		} else if (playerType.equals("computer")) {
			this.player = new ComputerPlayer(this);
		}

		Hand hand = new Hand();
		this.player.assignHand(hand);

		this.player.getHand().hardResetHand();

		this.getPlayerHand().addObserver(this.getView());
	}

	/**
	 * Start a game. Should only be called after the hand has been filled, but
	 * could also be called before depending on server implementation.
	 * 
	 * @param args
	 *            The server arguments.
	 */
	public void startGame(String[] args) {
		
		for (String pname : args) {
			this.scores.put(pname, 0);
		}

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

	/**
	 * Register a server add-to-hand command.
	 * 
	 * @param tiles
	 *            The server arguments.
	 */
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
			this.startGame(this.scores.keySet().toArray(new String[0]));
		}

	}

	/**
	 * Register a view's request to put a specified stone on a specified
	 * position.
	 * 
	 * @param tileInHand
	 *            The number of the tile in the hand. Note: it should be taken
	 *            into account that these numbers change as stones are removed
	 *            from the hand in the course of a turn.
	 * @param x
	 *            The x-coordinate.
	 * @param y
	 *            The y-coordinate.
	 */
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
	
	/**
	 * Bypass for requestSwap and addMove for the computer to use.
	 * @param turn The new turn.
	 */
	public void setTurn(Turn turn) {
		this.turn = turn;
	}

	/**
	 * The view requests to swap a tile from the hand.
	 * 
	 * @param tile
	 *            The number of the tile in the hand. Note: it should be taken
	 *            into account that these numbers change as stones are removed
	 *            from the hand in the course of a turn.
	 */
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

	/**
	 * Submit the turn to the server.
	 */
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

	/**
	 * Register an incoming turn from the server. The turn is checked with the
	 * Turn object, but we assume the turn contains no faults, because the
	 * server has checked it before. Or at least, should have.
	 * 
	 * @param args
	 *            The server arguments.
	 */
	public synchronized void registerTurn(String[] args) {

		this.status = Client.Status.IN_GAME;

		// We construct a temporary player to perform the turn.
		Player tempPlayer = new HumanPlayer("Temp", this);
		tempPlayer.assignHand(new Hand());
		Turn turn = new Turn(boardCopy, tempPlayer);

		// There is more than two arguments, meaning tiles have been placed.
		if (args.length > 2) {

			for (int i = 2; i < args.length; i++) {

				String arg = args[i];

				String[] move = arg
								.split("\\" + String.valueOf(Protocol.Server.Settings.DELIMITER2));

				// For each move, extract the tile.
				Tile tile = new Tile(move[0].charAt(0), move[0].charAt(1));

				// Add the tile to the hand, so it can be played by the
				// temporary player.
				try {
					tempPlayer.getHand().addToHand(tile);
				} catch (HandLimitReachedExeption e) {
					Util.log("error", "Could not parse server move.");
					Util.log(e);
				}

				// Extract the coordinates.
				int x = Integer.parseInt(move[1]);
				int y = Integer.parseInt(move[2]);

				// Try to add the move to the turn.
				try {
					turn.addMove(new Move(tile, this.boardCopy.getSquare(x, y)));
				} catch (SquareOutOfBoundsException | IllegalMoveException
								| IllegalTurnException e) {
					Util.log("error", "Could not parse server move.");
					Util.log(e);
				}
			}

			// The turn has been filled. We now try to execute it. For this we
			// need a temporary bag, since after each turn the hand of the
			// 'temporary' player will be filled from the bag.
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

			// Notify view of what happened.
			try {
				this.getView().sendNotification(args[0] + " played " + (args.length - 2)
								+ " tiles for " + turn.calculateScore() + " points.");
			} catch (SquareOutOfBoundsException | IllegalMoveException e) {
				Util.log(e);
			}

			// Try to keep track of the score.
			try {
				Integer score = scores.get(args[0]) + turn.calculateScore();
				scores.put(args[0], score);
			} catch (SquareOutOfBoundsException | IllegalMoveException e) {
				Util.log(e);
			}
			
			this.getView().sendScores(scores);

			// There are only two arguments, the players, so the player swapped
			// tiles.
		} else {

			this.getView().sendNotification(args[0] + " swapped tiles.");

		}

		// We need to keep track of tiles used in the previous turn so we can
		// re-add them if something goes wrong.
		if (!args[0].equals(this.name)) {
			try {
				this.getPlayerHand().addTohand(usedInPrevious);
			} catch (HandLimitReachedExeption e) {
				Util.log(e);
			}
		}

		// It is now our turn!
		if (args[1].equals(this.name)) {
			this.turn = new Turn(boardCopy, this.player);
			this.turn.addObserver(getView());
			this.turn.getBoardCopy().addObserver(getView());
			this.player.giveTurn();
		} else if (args[0].equals(this.name)) {
			usedInPrevious.clear();
		}

	}

	/**
	 * We get an end-game from the server which we need to parse.
	 * 
	 * @param args
	 *            The server arguments.
	 */
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
		this.getView().sendScores(this.scores);
		this.scores.clear();
		this.status = Client.Status.IN_LOBBY;
	}

	/**
	 * We receive a chat request from the view.
	 * 
	 * @param args
	 *            The chat, which is constructed of an array of strings, which
	 *            should be joined with spaces, because we use spaces as our
	 *            arguments separator. Confusing stuff. We know.
	 */
	public void chatFromClient(String[] args) {
		String message = "";
		for (String arg : args) {
			message = message.concat(" " + arg);
		}
		this.server.send(Protocol.Client.CHAT, new String[] { message });
	}

	/**
	 * We receive a chat from the server. This should be joined as well since
	 * the message could have contained the argument seperator from the
	 * protocol. Again, confusing stuff.
	 * 
	 * @param args
	 *            The server arguments.
	 */
	public void chatFromServer(String[] args) {
		String message = "";
		for (String arg : args) {
			message = message.concat(arg);
		}
		this.getView().showChat(message);
	}

	/**
	 * The view wants to send an invite to the server. We oblige, as always.
	 * 
	 * @param string
	 *            The nickname of the player-to-challenge.
	 */
	public void invite(String string) {
		if (this.status == Client.Status.IN_LOBBY) {
			this.status = Client.Status.WAITINGFORGAME;
			this.preparePlayer();
			this.server.send(Protocol.Client.INVITE, new String[] { string });
		} else {
			this.getView().sendNotification("You can only invite if you are in the lobby.");
		}
	}

	/**
	 * We decline an invite from someone.
	 */
	public void declineInvite() {
		this.server.send(Protocol.Client.DECLINEINVITE, new String[] {});
	}

	/**
	 * We accept an invite from someone.
	 */
	public void acceptInvite() {
		if (this.status == Client.Status.IN_LOBBY) {
			this.status = Client.Status.WAITINGFORGAME;
			this.preparePlayer();
			this.server.send(Protocol.Client.ACCEPTINVITE, new String[] {});
		} else {
			this.getView().sendNotification("You can only accept invites if you are in the lobby.");
		}
	}

	/**
	 * We go the leaderboard from the server, and send it to the view.
	 * 
	 * @param args
	 *            The server arguments.
	 */
	public void leaderboard(String[] args) {
		this.getView().sendLeaderboard(args);
	}

	/**
	 * We request the leaderboard from the server.
	 */
	public void requestLeaderboard() {
		this.server.send(Protocol.Client.GETLEADERBOARD, new String[] {});
	}

	/**
	 * We got an invite the server, pass it along to the view.
	 * 
	 * @param string
	 *            The nickname of the fella.
	 */
	public void gotInvite(String string) {
		this.getView().gotInvite(string);
	}

	/**
	 * Undoes the remove-from-hand during a turn. Usefull in reverting a turn,
	 * or recovering from a wrong move.
	 */
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

	/**
	 * Our invite has been declined. :(
	 */
	public void declineInviteFromServer() {
		this.status = Client.Status.IN_LOBBY;
		this.getView().sendNotification("Your challenge has been refused.");
	}

	/**
	 * The view wishes to revert the turn. Starting anew.
	 */
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

	/**
	 * Return the view.
	 * 
	 * @return The view.
	 */
	public View getView() {
		return this.view;
	}

	/**
	 * Return the board copy of this client.
	 * 
	 * @return The board copy.
	 */
	public Board getBoard() {
		return this.boardCopy;
	}

	/**
	 * Return the player hand for this client.
	 * 
	 * @return The hand.
	 */
	public Hand getPlayerHand() {
		if (this.player == null) {
			return null;
		} else {
			return this.player.getHand();
		}
	}

	/**
	 * Return the name of the player and client.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Return the player object.
	 * 
	 * @return The player.
	 */
	public Player getPlayer() {
		return this.player;
	}

	/**
	 * Return the turn. Hehe.
	 * 
	 * @return the turn
	 */
	public Turn getTurn() {
		return turn;
	}

	public void stop(String message) {
		this.getView().stop(message);		
	}

}
