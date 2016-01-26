package players;

import game.Hand;

/**
 * This is the general player class. All player should extend from this one. All
 * core functionality regarding hands is embedded in the player, and both client
 * and server can extend from there where necessary.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
public abstract class Player {

	private Hand hand;
	private String name;

	private int score;

	/**
	 * Create a new player with the given name.
	 * @param name The player name.
	 */
	public Player(String name) {
		this.name = name;
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
