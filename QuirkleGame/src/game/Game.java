package game;

import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import exceptions.IllegalTurnException;
import exceptions.SquareOutOfBoundsException;

/**
 * This class manages an entire game, including their players. It is
 * instantiated by the server several clients are put in it.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
public abstract class Game {

	public final static int DIFFERENTSHAPES = 6;
	public final static int DIFFERENTCOLORS = 6;

	public final static int DEFAULTTILESPERTYPE = 3;
	public final int tilesPerType = Game.DEFAULTTILESPERTYPE;

	public final int TURNTIMEOUT = 60;

	private List<Player> players;
	private int currentPlayer;

	public static enum GameState {
		WAITING, INITIAL, NORMAL, FINISHED
	};

	private GameState gameState;

	private Board board;
	private Bag bag;

	/**
	 * Adds a player to the game.
	 * 
	 * @param player
	 *            The player to be added.
	 */
	public void addPlayer(Player player) {
		players.add(player);
	}

	/**
	 * Removes a player from the game. This will also disqualify the player.
	 * 
	 * @param player
	 *            The player to be removed.
	 */
	public void removePlayer(Player player) {
		players.remove(player);
	}

	/**
	 * Start the game!
	 */
	public abstract void start();

	public abstract void disqualify(Player player);

	public abstract void finish();

	public abstract boolean gameOver();

	/*
	 * Getters and setters below.
	 */

	/**
	 * Returns the amount of tiles per type for this game. Usually 3.
	 * 
	 * @return The amount of tiles per type.
	 */
	public int getTilesPerType() {
		return this.tilesPerType;
	}

	/**
	 * Get the current player for this game.
	 */
	public Player getCurrentPlayer() {
		return this.players.get(this.currentPlayer);
	}

	/**
	 * Get the next player for this game.
	 * 
	 * @param mod
	 *            The next player to be selected. This modifies the
	 *            currentPlayer field, so we have to think about what value to
	 *            put here. Example: 0 doesn't change the player, unless the a
	 *            player has been removed from the list in which case the next
	 *            player is selected. 1 picks the next player from the list in
	 *            normal situations.
	 */
	public Player getNextPlayer(int mod) {
		return this.players.get((this.currentPlayer + mod) % this.players.size());
	}

	/**
	 * @return the players
	 */
	public List<Player> getPlayers() {
		return players;
	}

	/**
	 * @return the gameState
	 */
	public GameState getGameState() {
		return gameState;
	}

	/**
	 * @param gameState
	 *            the gameState to set
	 */
	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}

	/**
	 * @return the board
	 */
	public Board getBoard() {
		return board;
	}

	/**
	 * @return the bag
	 */
	public Bag getBag() {
		return bag;
	}

	/**
	 * @param bag
	 *            the bag to set
	 */
	public void setBag(Bag bag) {
		this.bag = bag;
	}

	/**
	 * @param currentPlayer
	 *            the currentPlayer to set
	 */
	public void setCurrentPlayer(Player p) {
		this.currentPlayer = this.players.indexOf(p);
	}

}
