package players;

import game.Hand;
import game.Turn;

/**
 * TODO Write file header.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
public abstract class Player {

	private Hand hand;
	private String name;
	private Turn turn;

	private int score;

	public Player(String name) {
		this.name = name;
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

}
