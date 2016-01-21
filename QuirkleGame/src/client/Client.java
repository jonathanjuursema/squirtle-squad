package client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.Console;
import application.Util;
import exceptions.IllegalMoveException;
import exceptions.IllegalTurnException;
import exceptions.SquareOutOfBoundsException;
import game.Board;
import game.BoardSquare;
import game.Hand;
import game.Move;
import game.Tile;
import game.Turn;
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
	private TUIview view;
	private Board boardCopy;
	private Hand hand;
	private Player player;
	private Turn turn;

	String nickname = "";

	public Client(InetAddress host, int port) throws IOException {

		this.socket = new Socket(host, port);
		this.server = new ClientConnectionHandler(this.socket, this);
		this.server.start();
		this.register();

		this.view = new TUIview(this);
		this.view.start();
	}

	public void enterGame() {
		this.boardCopy = new Board();
		this.hand = new Hand();
		this.player = new HumanPlayer(nickname, view);

		hand.addObserver(view);
		boardCopy.addObserver(view);
	}

	/**
	 * Register a client with the server.
	 */
	public void register() {

		nickname = Console.readString("What nickname would you like to use?" + System.lineSeparator() + "> ");
		this.server.send(Protocol.Client.HALLO, new String[] { nickname, "" });

	}

	public Board getBoardCopy() {
		return boardCopy;
	}

	public void setBoardCopy(Board boardCopy) {
		this.boardCopy = boardCopy;
	}

	public Hand getHand() {
		return hand;
	}

	public void setHand(Hand hand) {
		this.hand = hand;
	}

	public void pushErrorMessage(String string) {
		view.printMessage("error", string);
		Util.log("server error", string);
	}

	public void pushChatMessage(String string) {
		view.printMessage("chat", string);
	}

	/**
	 * Main functionality of the client.
	 */
	public void start() {

	}

	public void pushMessage(String message) {
		view.printMessage("play", message);
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	/**
	 * @param args
	 * @throws NumberFormatException
	 */
	public void updateBoard(String[] args) throws NumberFormatException {
		Turn simulatedTurn = new Turn(boardCopy, player); // TODO: edit player
		
		String message = "Player " + args[0] + " placed the following tiles : \n";
		
		int count = 0;
		for (String unParsedMove : args) {
			if (count > 2) {
				Move doneMove;
				
				try {
					doneMove = Client.parseMove(unParsedMove, this.getBoardCopy());
					simulatedTurn.addMove(doneMove);
					
					message += doneMove.toString();
				} catch (IllegalMoveException | IllegalTurnException | SquareOutOfBoundsException e) {
					this.pushErrorMessage(
							"Server and Client are out of sync, waiting for server to disqualify you. Sorry :)");
					Util.log("debug", e.getMessage() + ", waiting for server to disqualify.");
				}
				
				count++;
			}
		}
		

		try {
			simulatedTurn.applyTurn();
		} catch (SquareOutOfBoundsException e) {
			this.pushErrorMessage(
					"Server and Client are out of sync, waiting for server to disqualify you. Sorry :)");
			Util.log("debug", e.getMessage() + ", waiting for server to disqualify.");
		}

		this.pushMessage(message);
		
		if (args[1].equals(this.getNickname())) {
			Turn thisClientTurn = new Turn(boardCopy, player);
			this.turn = thisClientTurn;
			player.giveTurn(thisClientTurn);
		}
	}

	/**
	 * @param args
	 * @return
	 * @throws SquareOutOfBoundsException 
	 * @throws NumberFormatException 
	 */
	public static Move parseMove(String argument, Board board) throws SquareOutOfBoundsException {
		String[] move = argument.split("" + Protocol.Server.Settings.DELIMITER2);
		Tile tile = new Tile(move[0].charAt(0), move[0].charAt(1));
		int x = Integer.parseInt(move[1]);
		int y = Integer.parseInt(move[2]);
		BoardSquare returnBS = board.getSquare(x, y);
		Move returnMove = new Move(tile, returnBS);

		return returnMove;
	}

}
