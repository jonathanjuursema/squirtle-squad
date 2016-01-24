package client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import application.Console;
import application.Util;
import exceptions.HandLimitReachedExeption;
import exceptions.SquareOutOfBoundsException;
import exceptions.TileNotInHandException;
import game.Board;
import game.BoardSquare;
import game.Move;
import game.Tile;
import game.Turn;
import players.ClientPlayer;
import players.HumanPlayer;
import players.Player;
import protocol.Protocol;
import views.TUIview;

/**
 * TODO Write file header.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
public class Client {

	private Socket socket;
	private ClientConnectionHandler server;

	// TODO: also the computer player has a tui but specify this in the player

	private Board boardCopy;
	private ClientPlayer player;

	public static enum Status {
		IN_LOBBY, IN_GAME
	};

	public Status status;

	private String nickname;

	/**
	 * Setting up the client. The sockets will be initialised aswel as the
	 * player and the view.
	 * 
	 * @param host
	 * @param port
	 * @throws IOException
	 */
	public Client(InetAddress host, int port) throws IOException {
		this.socket = new Socket(host, port);
		this.server = new ClientConnectionHandler(this.socket, this);
		this.server.start();

		this.player = new HumanPlayer(nickname, this);
		this.player.setView(new TUIview(this));

		this.run();
	}

	/**
	 * Main functionality of the client.
	 */
	public void run() {
		boolean run = true;

		while (run) {

			if (this.nickname == null) {
				this.register();
			}

		}
		// TODO: Start afvangen
	}

	/**
	 * Register a client to the server
	 */
	public void register() {
		String nickname = Console.readString("What nickname would you like to use?" + System.lineSeparator() + "> ");
		this.setNickname(nickname);
		this.server.send(Protocol.Client.HALLO, this.getNickname());
	}

	public void requestGame() {

		String amount = this.waitForInput("game",
				"With how much players do you want to play? (2-4 players, type 1 for any kind of game)");
		if (amount != null) {
			int choice = Integer.parseInt(amount);
			if (choice >= 1 && choice <= 4) {
				this.sendMessageToServer(Protocol.Client.REQUESTGAME, "" + choice);
				this.pushMessage("Waiting for the server to start a game.");
			}
		}

	}

	/**
	 * If the server put the client into a game, this method is called. This
	 * method will change the status into "IN_GAME" and initialize the board.
	 * And setup the first turn.
	 */
	public void enterGame() {
		this.status = Status.IN_GAME;

		this.boardCopy = new Board();

		ClientPlayer cPlayer = this.player;
		if (cPlayer.getView() instanceof Observable) {
			this.boardCopy.addObserver((Observer) cPlayer.getView());
		}

		// First move

		Turn newTurn = new Turn(boardCopy, player);
		this.pushMessage("Please select your first best move.");
		this.player.giveTurn(newTurn);

	}

	/*
	 * The method that will parse the turn into a command. The turn can either
	 * be "MAKEMOVE" or "CHANGESTONE". If the turn is a MoveRequest, the moves
	 * are parsed into arguments. If the turn is swap object the swaprequests
	 * will be parsed into tiles.
	 */
	public void sendTurnToServer() {

		if (this.getPlayer().getTurn().isMoveRequest()) {

			String[] completeTurn = new String[this.player.getTurn().getMoves().size()];

			for (int i = 0; i < this.player.getTurn().getMoves().size(); i++) {
				Move m = this.player.getTurn().getMoves().get(i);
				completeTurn[i] = parseMove(m);
			}
			this.sendMessageToServer(Protocol.Client.MAKEMOVE, completeTurn);
		} else if (this.getPlayer().getTurn().isSwapRequest()) {
			String[] swapRequest = new String[this.player.getTurn().getSwap().size()];
			for (int i = 0; i < this.player.getTurn().getSwap().size(); i++) {
				Tile t = this.player.getTurn().getSwap().get(i);
				swapRequest[i] = parseSwap(t);
			}
			this.sendMessageToServer(Protocol.Client.CHANGESTONE, swapRequest);
		}

	}

	/**
	 * Pushes the error message to the player. The difference with pushMessage()
	 * is that this message will also logs the error with Util.log. It is not a
	 * good sign if this method is called because if the server sends an error
	 * message, this means these illegal actions are not catched by the client.
	 * 
	 * @param string
	 *            The error message
	 */

	public void pushErrorMessage(String string) {

		((TUIview) this.player.getView()).printMessage("error", string);
		Util.log("server error", string);
	}

	/**
	 * This function is used to push a message about the game to the player.
	 * 
	 * @param message
	 *            The message that needs to be pushed.
	 */

	public void pushMessage(String message) {
		((TUIview) this.player.getView()).printMessage("play", message);
	}

	/**
	 * This function will update the board according to the raw moves. The moves
	 * will be unparsed with the function unParsMove() and will be placed on the
	 * copy of the board contained by the client.
	 * 
	 * @param args
	 *            The raw strings of moves, for example "CC*1*2_CC*2*1"
	 */
	public void updateBoard(String[] args) {
		int count = 1;
		for (String unParsedMove : args) {
			if (count > 2) {
				Move doneMove;
				doneMove = unParseMove(unParsedMove);

				try {
					this.boardCopy.placeTile(doneMove.getTile(), doneMove.getPosition().getX(),
							doneMove.getPosition().getY());
				} catch (SquareOutOfBoundsException e) {
					Util.log(e);
				}
			}
			count++;
		}
	}

	/**
	 * This function will unparse the raw move. A move consist of a Tile and a
	 * place, for example "CC*2*1". The result is a Move object.
	 * 
	 * @param args
	 *            The raw move, for example "CC*2*1"
	 * @return
	 */
	public Move unParseMove(String argument) {

		String[] move = argument.split("\\" + String.valueOf(Protocol.Server.Settings.DELIMITER2));
		Tile tile = new Tile(move[0].charAt(0), move[0].charAt(1));

		int x = Integer.parseInt(move[1]);
		int y = Integer.parseInt(move[2]);

		BoardSquare returnBS;
		Move returnMove = null;
		try {
			returnBS = this.getBoardCopy().getSquare(x, y);
			returnMove = new Move(tile, returnBS);
		} catch (SquareOutOfBoundsException e) {
			Util.log(e);
		}

		return returnMove;
		// TODO: Exception for non existing move
	}

	/**
	 * The function will add the stones received from the server to the hand of
	 * the client. If the stones cannot be added because the server has given
	 * the client the stones first before the move has been accepted, the client
	 * will wait 1 second and try again. The function will try 3 times before it
	 * failed.
	 * 
	 * @param args
	 *            The tiles that needs to be added. For example "CC*1*2"
	 */
	public void addTilesToHand(String[] args) {
		if (this.getPlayer().getTurn() != null) {
			if (this.getPlayer().getTurn().isMoveRequest()) {
				Util.log("debug",
						"" + this.getPlayer().getTurn().getMoves().size() + this.getPlayer().getTurn().toString());
				List<Tile> tileList = new ArrayList<Tile>();
				for (Move m : this.getPlayer().getTurn().getMoves()) {
					tileList.add(m.getTile());
				}

				try {
					this.getPlayer().getHand().removeFromHand(tileList);
				} catch (TileNotInHandException e1) {
					Util.log(e1);
				}
			} else if (this.getPlayer().getTurn().isSwapRequest()) {
				Util.log("debug",
						"" + this.getPlayer().getTurn().getSwap().size() + this.getPlayer().getTurn().getSwap().toString());
				List<Tile> tileList = new ArrayList<Tile>();
				for (Tile t : this.getPlayer().getTurn().getSwap()) {
					tileList.add(t);
				}

				try {
					this.getPlayer().getHand().removeFromHand(tileList);
				} catch (TileNotInHandException e1) {
					Util.log(e1);
				}
			}
		}

		List<Tile> addList = new ArrayList<Tile>();

		boolean succesfull = false;
		int tries = 0;

		for (String tile : args) {
			addList.add(new Tile(tile.charAt(0), tile.charAt(1)));
		}

		while (!succesfull && tries < 3) {
			try {
				getPlayer().getHand().addTohand(addList);
				tries++;
			} catch (HandLimitReachedExeption e) {
				if (tries == 0) {
					pushErrorMessage("Waiting for the server to take your stones and add new stones.");
				}
				tries++;
				Util.log(e);
				continue;
			}
			succesfull = true;
		}

		if (!succesfull) {
			pushErrorMessage("No stones have been added to the hand.");
		}
		
		pushMessage(this.getPlayer().getHand().toString());
	}

	/**
	 * This function ensures that tiles that are placed on the board and are
	 * approved by the server will be taken out of the hand. This function can
	 * be called when the server approved the turn and so the hand of the player
	 * needs to be updated.
	 * 
	 * @param args
	 *            The argument with the different moves according to the
	 *            protocol. A argument of the array consist of a specified Tile
	 *            with the X- and Y- coordinate.
	 * @throws SquareOutOfBoundsException
	 */
	public void removeTilesToHand(String[] args) {
		int count = 1;
		for (String parsedMove : args) {
			if (count > 2) {
				Move doneMove = this.unParseMove(parsedMove);

				try {
					this.getPlayer().getHand().removeFromHand(doneMove.getTile());
				} catch (TileNotInHandException e) {
					Util.log(e);
				}
			}
			count++;
		}
	}

	/**
	 * Simpler version of sendMessageToServer with a command and a single String
	 * instead of an array with multiple strings.
	 * 
	 * @param command
	 *            The command according to the protocol.
	 * @param arg
	 *            The argument that goes with the command.
	 */

	private void sendMessageToServer(String command, String arg) {
		server.send(command, arg);
	}

	/**
	 * Simple function to send a command to the server. The command must be
	 * according to the protocol aswel as the arguments.
	 * 
	 * @param command
	 *            The command according to the protocol
	 * @param args
	 *            The arguments that needs that specifies the command
	 */
	public void sendMessageToServer(String command, String[] args) {
		server.send(command, args);
	}

	/**
	 * Parse a move to generate a string to send this to the server.
	 * 
	 * @param m
	 *            The move that needs to be parsed according to the protocol
	 * @return The string that contains the Tile and the position
	 */
	public String parseMove(Move m) {
		String position = "" + m.getPosition().getX() + Protocol.Server.Settings.DELIMITER2 + m.getPosition().getY();
		String tile = "" + m.getTile().getColor() + m.getTile().getShape();
		return tile + Protocol.Server.Settings.DELIMITER2 + position;
	}

	/**
	 * Parse a single tile to a String to send this to the server.
	 * 
	 * @param tile
	 * @return
	 */
	public String parseSwap(Tile tile) {
		return "" + tile.getColor() + tile.getShape();
	}

	/**
	 * Returns the copy of the board that is contained by the client. The
	 * clients ensures his own copy is the same as the server board, this
	 * function will retrieve this own copy.
	 * 
	 * @return
	 */
	public Board getBoardCopy() {
		return boardCopy;
	}

	/**
	 * Retrieves the nickname
	 * 
	 * @return The nickname of the player/client
	 */
	public String getNickname() {
		return this.player.getName();
	}

	/**
	 * Set the nickname of the client.
	 * 
	 * @param player
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
		this.player.setName(nickname);
	}

	/**
	 * Get the player that is connected to the client.
	 * 
	 * @return The player object which the client is connect to will be
	 *         returned.
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Sets the player.
	 * 
	 * @param player
	 */
	public void setPlayer(ClientPlayer player) {
		this.player = player;
	}

	/**
	 * Retrieves the status of the client.
	 * 
	 * @param status
	 *            The status can be IN_LOBBY when the client is not in a game
	 *            and IN_GAME, when the client is not in a game.
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * Set the status of the client to IN_LOBBY or IN_GAME. This parameter
	 * determines which functions can be called upon the client. For example,
	 * when a player tries to place moves, these will be removed.
	 * 
	 * @param status
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * This functions registers the turn that is send by the server. This
	 * functions always calls the function that update the board. When the
	 * server sends the message with as first argument the client's nickname,
	 * the client knows the past turn was from his player and updates the hand.
	 * When the second argument is the client's nickname, the client creates a
	 * turn and ask the player, which is connected to the client, for to add
	 * moves.
	 * 
	 * @param args
	 *            The arguments of the move that has been made. " Example:
	 *            "nickname_nickname_CC*1*2_CC*1*2"
	 * @throws SquareOutOfBoundsException 
	 */
	public void registerTurn(String[] args) throws SquareOutOfBoundsException {
		try {
			this.updateBoard(args);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
		}

		if (args[1].equals(getNickname())) {

			Turn thisClientTurn = new Turn(this.boardCopy, getPlayer());
			this.getPlayer().giveTurn(thisClientTurn);

		} else if (args[0].equals(getNickname())) {
			String message = "You played a move and scored " + this.getPlayer().getTurn().calculateScore() + " points, wait for other players to make a move. \n";			
			this.pushMessage(message);
		} else {
			String message = "Player " + args[0] + " played his turn.";
			message += getBoardCopy().toString();
			this.pushMessage(message);
		}
	}

	public void pushErrorMessage(String[] message) {
		switch (Integer.parseInt(message[0])) {
		case 1: // not your turn
			this.player.sendError("It is not your turn at the moment, wait for your turn please.");
			break;
		case 2: // not your stone
			this.player.sendError("This tile is not your tile, please pick a stone of your own hand.");
			break;
		case 3: // not that many stones available
			this.player.sendError("The bag doesn't have enough tiles to complete this action.");
			break;
		case 4: // name exists
			this.register();
			break;
		case 5: // not challengable
			this.player.sendError("This person cannot be challenged at the moment.");
			break;
		case 6: // challenge refused
			this.player.sendError("The person has refused your challenge.");
			break;
		case 7: // invalid move
			this.player.sendError("This move is invalid.");
			break;
		default:
			this.player.sendError(message[1]);
			Util.log("protocol", "Recevied an generic error from the server. ");
			break;
		}
	}

	public String waitForInput(String type, String message) {
		String input = this.player.askForInput(type, message);
		return input;
	}

}
