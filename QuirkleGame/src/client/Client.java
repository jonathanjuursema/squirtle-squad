package client;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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

	public static final String[] FUNCTIONS = { "CHAT", "CHALLENGE", "LEADERBORD" };

	public Status status;

	public Client() throws IOException {

		this.status = Client.Status.INITIALIZING;

		this.view = new TextView(this);

		this.socket = new Socket(view.askForHost(), view.askForPort());

		this.server = new ClientConnectionHandler(this.socket, this);
		this.server.start();

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

	public void submitTurn() {
		if (this.turn.isMoveRequest()) {

		} else if (this.turn.isSwapRequest()) {

		}
		this.status = Client.Status.IN_GAME;
	}

	public void requestGame(int no) {

		if (this.status != Client.Status.IN_LOBBY) {
			this.view.sendNotification("error", "You are not in the lobby.");
			return;
		}

		String playerType = this.view.askForPlayerType();

		while (!playerType.equals("human") && !playerType.equals("computer")) {
			playerType = this.view.askForPlayerType();
		}

		if (playerType.equals("human")) {
			this.player = new HumanPlayer(this.name, this);
		} else if (playerType.equals("computer")) {
			this.player = new HumanPlayer(this.name, this);
		}

		this.player.getHand().hardResetHand();

		this.status = Client.Status.WAITINGFORGAME;
		this.server.send(Protocol.Client.REQUESTGAME, new String[] { "" + no });
	}

	public void startGame() {
		if (this.status == Client.Status.WAITINGFORGAME) {
			if (this.getPlayerHand().getTilesInHand().size() > 0) {
				this.status = Client.Status.IN_GAME_INITIAL;
				this.boardCopy = new Board();
				this.turn = new Turn(boardCopy, this.player);
				this.player.giveTurn(this.turn);
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

	public void requestSwap(String[] args) {
	
		if (this.status == Client.Status.IN_TURN) {
	
			boolean[] tilesFromHand = new boolean[Hand.LIMIT];
	
			for (String tile : args) {
	
				int no = Integer.parseInt(tile);
				if (tilesFromHand[no] != true && no < Hand.LIMIT) {
					tilesFromHand[no] = true;
					try {
						this.
						turn.
						addSwapRequest(
										this.getPlayerHand().
										getTilesInHand().
										get(no)
										);
					} catch (IllegalTurnException e) {
						this.getView().sendNotification("This swap is illegal: " + e.getMessage());
						Util.log(e);
						this.turn.getSwap().clear();
						return;
					}
				}
	
			}
			
			this.getView().showTurn();
			
			Util.log("debug", "Registered swap request.");
	
		} else {
			
			this.getView().sendNotification("It is not your turn!");
			
		}
	
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

	public synchronized void registerTurn(String[] args) {

		// TODO Implement so we can keep track of the scores.

		Player tempPlayer = new HumanPlayer("Temp", this);
		Turn turn = new Turn(boardCopy, tempPlayer);

		for (int i = 2; i < args.length; i++) {

			String arg = args[i];

			String[] move = arg.split("\\" + String.valueOf(Protocol.Server.Settings.DELIMITER2));

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
			} catch (SquareOutOfBoundsException | IllegalMoveException | IllegalTurnException e) {
				Util.log("error", "Could not parse server move.");
				Util.log(e);
			}
		}

		Bag bag = new Bag();
		bag.fill();

		try {
			turn.applyTurn(boardCopy, bag);
		} catch (TooFewTilesInBagException | TileNotInBagException | TooManyTilesInBag
						| TileNotInHandException | IllegalTurnException | SquareOutOfBoundsException
						| HandLimitReachedExeption e) {
			Util.log("error", "Could not parse server move.");
			Util.log(e);
		}

		if (args[1].equals(this.name)) {
			this.turn = new Turn(boardCopy, this.player);
			this.player.giveTurn(this.turn);
		} else if (args[0].equals(this.name)) {
			try {
				this.getPlayerHand().removeFromHand(usedInPrevious);
			} catch (TileNotInHandException e) {
				Util.log("error", "Could not remove tiles from player hand.");
				Util.log(e);
			}
			usedInPrevious.clear();
		}

	}

	public void declineInvite() {
		// TODO Auto-generated method stub
	}

	public void endGame(String[] args) {
		switch (args[0]) {
		case "WIN":
			this.getView().sendNotification("The game is over. The winner is: " + args[1]);
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

	public void invite(String string) {
		// TODO Auto-generated method stub
	}

	public void leaderboard(String[] args) {
		this.getView().sendLeaderboard(args);
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

}
