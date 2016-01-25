package players;

import client.Client;
import exceptions.IllegalMoveException;
import exceptions.IllegalTurnException;
import exceptions.SquareOutOfBoundsException;
import exceptions.TileNotInHandException;
import game.Hand;
import game.Move;
import game.Tile;
import game.Turn;

/**
 * TODO Write file header.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
public abstract class Player {

	private Client client;
	private Hand hand;
	private String name;
	private Turn turn;

	private int score;

	public Player(String name) {
		this.name = name;
	}

	public Player() {
	}

	public void placeMove(Move move) throws SquareOutOfBoundsException, IllegalMoveException,
					IllegalTurnException, TileNotInHandException {
		this.turn.addMove(move);
	}

	public void addSwap(Tile t) throws SquareOutOfBoundsException, IllegalMoveException,
					IllegalTurnException, TileNotInHandException {
		this.turn.addSwapRequest(t);
	}

	/**
	 * Hands the turn to the player.
	 * 
	 * @param turn
	 *            The Turn to be populated.
	 */
	public void giveTurn(Turn turn) {
		this.turn = turn;
		// IF HUMANPLAYER NOW THE TUI KICKS IN
		// IF COMPUTER PLAYER THE COMPUTER PLAYER WILL CALCULATE THE BEST TURN
	}

	/**
	 * Allow subclass to access this player's private turn.
	 * 
	 * @return The turn.
	 */
	public Turn getTurn() {
		return this.turn;
	}

	/**
	 * Assigns a hand to the player
	 * 
	 * @param hand
	 */
	public void assignHand(Hand hand) {
		this.hand = hand;
	}

	/**
	 * @return the hand
	 */
	public Hand getHand() {
		return hand;
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
	 * Sets the name of the player
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the name of the player.
	 */
	public String toString() {
		return this.getName();
	}

}
