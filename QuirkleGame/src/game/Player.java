package game;

import exceptions.IllegalTurnException;
import exceptions.SquareOutOfBoundsException;
import server.Game;
import server.ServerConnectionHandler;

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
	private String colour;

	private int score;

	public static enum Status {
		IN_LOBBY, IN_GAME
	};

	public Player(Game game, String name) {
		// TODO: implement body
		this.name = name;
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
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the hand
	 */
	public Hand getHand() {
		return hand;
	}

	/**
	 * @return the colour
	 */
	public String getColour() {
		return colour;
	}

	/**
	 * @param colour
	 *            the colour to set
	 */
	public void setColour(String colour) {
		this.colour = colour;
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

}
