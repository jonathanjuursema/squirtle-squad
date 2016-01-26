package players;

import java.util.List;

import game.Tile;
import game.Turn;
import server.Game;

public abstract class ServerPlayer extends Player {

	private Turn turn;
	private Game game;

	public ServerPlayer(String name) {
		super(name);
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
	 * Return the ServerPlayer's game.
	 * 
	 * @return The game. You lost it.
	 */
	public Game getGame() {
		return this.game;
	}

	/**
	 * Set the game of the ServerPlayer.
	 * 
	 * @param game
	 *            The game. You lost it.
	 */
	public void setGame(Game game) {
		this.game = game;
	}

	public abstract void addToHand(List<Tile> applyTurn);

}
