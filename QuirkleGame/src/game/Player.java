package game;

public abstract class Player {
	public Hand hand;
	public String name;
	public String colour;
	public Game game;
	
	public Player(String name, String colour) {
		// TODO: implement body
	}
	
	/**
	 * Submit the turn to the game.
	 * @param turn the turn that needs passed through the game.
	 */
	
	public void playTurn(Turn turn) {
		// TODO: implement body
	}

	public Hand getHand() {
		return hand;
	}

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
