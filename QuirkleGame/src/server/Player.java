package server;

import exceptions.IllegalTurnException;
import exceptions.SquareOutOfBoundsException;
import game.Hand;
import game.Turn;
import views.TUIview;

/**
 * TODO Write file header.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
public class Player {

	private Game game;
	private Turn turn;
	private ServerConnectionHandler connection;

	private Hand hand;
	private String name;

	private int score;

	public static enum Status {
		IN_LOBBY, IN_GAME
	};
	private Status status;

	public Player(String name) {
		this.name = name;
	}

	/**
	 * Sends a message to the connection associated with the player.
	 * 
	 * @param cmd
	 *            The command to be send.
	 * @param args
	 *            The arguments to this command.
	 */
	public void sendMessage(String cmd, String[] args) {
		this.connection.send(cmd, args);
	}

	/**
	 * Submit the turn to the game.
	 * 
	 * @param turn
	 *            the turn that needs passed through the game.
	 */
	public void playTurn() {
		this.game.receiveTurn(this.getTurn());
		this.turn = null;
	}

	/**
	 * Hands the turn to the player.
	 * 
	 * @param turn
	 *            The Turn to be populated.
	 */
	public void giveTurn(Turn turn) {
		this.turn = turn;
	}

	/**
	 * Increment the score.
	 * 
	 * @param amount
	 *            The amount to increase the score by.
	 */
	public void incrementScore(int amount) {
		this.score += amount;
	}

	/**
	 * Assigns a hand to the player
	 * 
	 * @param hand
	 */
	public void assignHand(Hand hand) {
		this.hand = hand;
	}

	// Getters & setters.

	/**
	 * @return the hand
	 */
	public Hand getHand() {
		return hand;
	}

	/**
	 * @return the game
	 */
	public Game getGame() {
		return game;
	}

	/**
	 * @param game
	 *            the game to set
	 */
	public void setGame(Game game) {
		this.game = game;
	}

	/**
	 * @return the turn
	 */
	public Turn getTurn() {
		return turn;
	}

	/**
	 * @param turn
	 *            the turn to set
	 */
	public void setTurn(Turn turn) {
		this.turn = turn;
	}

	/**
	 * @return the score
	 */
	public int getScore() {
		return score;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
	}


}
