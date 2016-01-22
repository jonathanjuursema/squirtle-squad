package client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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

	//TODO: also the computer player has a tui but specify this in the player
	private TUIview view;
	
	private Board boardCopy;
	private ClientPlayer player;

	String nickname = "";

	public Client(InetAddress host, int port) throws IOException {

		this.socket = new Socket(host, port);
		this.server = new ClientConnectionHandler(this.socket, this);
		this.server.start();
		this.register();
		
		this.player = new HumanPlayer(nickname, this);
		this.view = new TUIview(this);
		this.view.start();
	}

	/**
	 * Main functionality of the client.
	 */
	public void start() {
		//TODO: Start afvangen
	}

	/**
	 * Register a client with the server.
	 */
	public void register() {
	
		nickname = Console.readString("What nickname would you like to use?" + System.lineSeparator() + "> ");
		this.server.send(Protocol.Client.HALLO, new String[] { nickname, "" });
	
	}

	public void enterGame() {
		this.boardCopy = new Board();

		this.player.getHand().addObserver(view);
		boardCopy.addObserver(view);
		
		// First move
		Turn newTurn = new Turn(boardCopy, player);
		this.player.giveTurn(newTurn);
	}

	public void sendTurnToServer() throws SquareOutOfBoundsException, TileNotInHandException {
		
		if(this.getPlayer().getTurn().isMoveRequest()) {
			
			String[] completeTurn = new String[this.player.getTurn().getMoves().size()];
			
			for(int i = 0; i < this.player.getTurn().getMoves().size(); i++) {
				Move m = this.player.getTurn().getMoves().get(i); 
				completeTurn[i] = parseMove(m);
			}
			this.sendMessageToServer(Protocol.Client.MAKEMOVE, completeTurn);
		} else if (this.getPlayer().getTurn().isSwapRequest()) {
			String[] swapRequest = new String[this.player.getTurn().getSwap().size()];
			for(int i = 0; i < this.player.getTurn().getSwap().size(); i++) {
				Tile t = this.player.getTurn().getSwap().get(i);
				swapRequest[i] = parseSwap(t);
			}
			this.sendMessageToServer(Protocol.Client.CHANGESTONE, swapRequest);
		}
		
	}

	public void pushErrorMessage(String string) {
		view.printMessage("error", string);
		Util.log("server error", string);
	}

	public void pushChatMessage(String string) {
		view.printMessage("chat", string);
	}

	/**
	 * A function that is used to communicate through the view
	 * with the player.
	 * @param message
	 */
	
	public void pushMessage(String message) {
		view.printMessage("play", message);
	}

	/**
	 * @param args
	 * @throws NumberFormatException
	 * @throws SquareOutOfBoundsException 
	 */
	public void updateBoard(String[] args) throws NumberFormatException, SquareOutOfBoundsException {
		int count = 0;
		for (String unParsedMove : args) {
			if (count > 2) {
				Move doneMove;
				doneMove = Client.unParseMove(unParsedMove, this.getBoardCopy());
				
				this.boardCopy.placeTile(doneMove.getTile(), doneMove.getPosition().getX(), doneMove.getPosition().getY());
				count++;
			}
		}
	}

	/**
	 * @param args
	 * @return
	 * @throws SquareOutOfBoundsException 
	 * @throws NumberFormatException 
	 */
	public static Move unParseMove(String argument, Board board) throws SquareOutOfBoundsException {
		String[] move = argument.split("" + Protocol.Server.Settings.DELIMITER2);
		Tile tile = new Tile(move[0].charAt(0), move[0].charAt(1));
		int x = Integer.parseInt(move[1]);
		int y = Integer.parseInt(move[2]);
		BoardSquare returnBS = board.getSquare(x, y);
		Move returnMove = new Move(tile, returnBS);

		return returnMove;
		// TODO: Exception for non existing move
	}

	/**
	 * @param args
	 */
	public void addTilesToHand(String[] args) {
		List<Tile> addList = new ArrayList<Tile>();
		for (String tile : args) {
			addList.add(new Tile(tile.charAt(0), tile.charAt(1)));
		}
		try {
			getPlayer().getHand().addTohand(addList);
		} catch (HandLimitReachedExeption e) {
			pushErrorMessage(
					"Server and Client are out of sync, waiting for server to disqualify you. Sorry :)");
			Util.log("debug", e.getMessage() + ", waiting for server to disqualify.");
		}
	}
	
	/**
	 * @param args
	 * @throws SquareOutOfBoundsException 
	 */
	public void removeTilesToHand(String[] args) throws SquareOutOfBoundsException {
		int count = 0;
		for (String parsedMove : args) {
			if (count > 2) {
				Move doneMove = Client.unParseMove(parsedMove, this.getBoardCopy());
				
				try {
					this.getPlayer().getHand().removeFromHand(doneMove.getTile());
				} catch (TileNotInHandException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				count++;
			}
		}
	}
	
	public void sendMessageToServer(String command, String[] args) {
		server.send(command, args);;
	}
	
	public String parseMove(Move m) {
		String position = "" + m.getPosition().getX() + Protocol.Server.Settings.DELIMITER2 + m.getPosition().getY();
		String tile = "" + m.getTile().getColor() + Protocol.Server.Settings.DELIMITER2 + m.getTile().getShape();
		return tile + Protocol.Server.Settings.DELIMITER2 + position;
	}
	
	public String parseSwap(Tile tile) {
		return "" + tile.getColor() + tile.getShape();
	}

	public Board getBoardCopy() {
		return boardCopy;
	}

	public void setBoardCopy(Board boardCopy) {
		this.boardCopy = boardCopy;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(ClientPlayer player) {
		this.player = player;
	}

	/**
	 * @param clientConnectionHandler TODO
	 * @param args
	 */
	public void unParseTurn(String[] args) {
		try {
			this.updateBoard(args);
		} catch (NumberFormatException | SquareOutOfBoundsException e) {
			// TODO Auto-generated catch block
		}
		
		if (args[1].equals(getNickname())) {
			Turn thisClientTurn = new Turn(getBoardCopy(), getPlayer());
			this.getPlayer().giveTurn(thisClientTurn);
		} else if (args[0].equals(getNickname())) {
			// TODO: update hand
			try {
				removeTilesToHand(args);
			} catch (SquareOutOfBoundsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			String message = "Player " + args[0] + " played his turn.";
			message += getBoardCopy().toString();
			this.pushMessage(message);
		}
	}
	
	public void unParseErrorMessage(String[] message) {
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

}
