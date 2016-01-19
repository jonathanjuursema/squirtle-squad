package game;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.Timer;

import exceptions.IllegalTurnException;
import exceptions.SquareOutOfBoundsException;

/**
 * This class manages an entire game, including their players. It is
 * instantiated by the server several clients are put in it.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
public abstract class Game implements ActionListener {

	public final static int DIFFERENTSHAPES = 6;
	public final static int DIFFERENTCOLORS = 6;

	public final static int DEFAULTTILESPERTYPE = 3;
	private int tilesPerType = Game.DEFAULTTILESPERTYPE;

	private List<Player> players;
	private int currentPlayer;

	private Timer timeout;

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

	/**
	 * Hands the current turn to a Player, awaiting their moves. Game tournament
	 * rules impose a 15 second timeout for submitting a move. If this function
	 * times out, the player is disqualified.
	 * 
	 * @param playerModifier
	 *            The next player to be selected. This modifies the
	 *            currentPlayer field, so we have to think about what value to
	 *            put here. Example: 0 doesn't change the player, unless the a
	 *            player has been removed from the list in which case the next
	 *            player is selected. 1 picks the next player from the list in
	 *            normal situations.
	 */
	public void nextTurn(int playerModifier) {

		this.currentPlayer = (this.currentPlayer + playerModifier) % this.players.size();
		new Turn(this.board, this.players.get(this.currentPlayer));

		timeout = new Timer(15000, this);

		this.setGameState(Game.GameState.WAITING);

	}

	/**
	 * Timeout function that is called after 15 seconds.
	 */
	public void actionPerformed(ActionEvent e) {
		timeout.stop();
		this.disqualify(this.players.get(currentPlayer));
		this.removePlayer(this.players.get(currentPlayer));
		this.nextTurn(0);
	}

	/**
	 * Entry function which player can use to signal their turn is done.
	 * 
	 * @param turn
	 */
	public void receiveTurn(Turn turn) {
		timeout.stop();
		try {
			turn.applyTurn();
			this.nextTurn(1);
		} catch (SquareOutOfBoundsException e) {
			// TODO Afvangen foutieve turn
		}
	}

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
	public void setCurrentPlayer(int currentPlayer) {
		this.currentPlayer = currentPlayer;
	}

}
