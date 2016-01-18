package game;

/**
 * TODO Write file header.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
public class Player {
	private Hand hand;
	private String name;
	private String colour;
	private Game game;
	private Turn turn;
	
	public int score;
	
	public static enum status { IN_LOBBY, IN_GAME };
	
	public Player(String name, String colour) {
		// TODO: implement body
		this.name = name;
	}
	
	public void giveTurn(Turn turn) {
		this.turn = turn;
	}
	
	/**
	 * Submit the turn to the game.
	 * @param turn the turn that needs passed through the game.
	 */
	
	public void playTurn() {
		// TODO: implement body
		this.turn.setReady();
		this.turn = null;
	}
	
	/**
	 * Increment the score.
	 * @param amount
	 */
	
	public void incrementScore(int amount) {
		this.score += amount;
	}
	
	/**
	 * Returns the score of the player
	 */
	
	public int getScore() {
		return this.score;
	}
	
	/**
	 * Returns the hand of the player
	 * @return
	 */

	public Hand getHand() {
		return hand;
	}
	
	/**
	 * Assigns a hand to the player
	 * @param hand
	 */
	public void assignHand(Hand hand) {
		this.hand = hand;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getColour() {
		return colour;
	}

	public void setColour(String colour) {
		this.colour = colour;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}
	
	
}
