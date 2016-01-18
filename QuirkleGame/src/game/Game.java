package game;

import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import exceptions.IllegalTurnException;

/**
 * This class manages an entire game, including their players. It is
 * instantiated by the server several clients are put in it.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
public abstract class Game extends Thread {

	public final static int DIFFERENTSHAPES = 6;
	public final static int DIFFERENTCOLORS = 6;

	public final static int DEFAULTTILESPERTYPE = 3;
	private int tilesPerType = Game.DEFAULTTILESPERTYPE;

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
	public abstract void run();

	/**
	 * Hands the current turn to a Player, awaiting their moves. Game tournament
	 * rules impose a 15 second timeout for submitting a move. If this function
	 * times out, the player is disqualified.
	 */
	public void nextTurn() {

		this.setGameState(Game.GameState.WAITING);

		// TODO Implement timeout.

		final Lock waitForTurn = new ReentrantLock();
		final Condition turnReady = waitForTurn.newCondition();

		this.currentPlayer = (this.currentPlayer + 1) % this.players.size();
		Turn turn = new Turn(this.board, this.players.get(this.currentPlayer), turnReady);

		while (!turn.isReady()) {
			try {
				turnReady.await();
			} catch (InterruptedException e) {
				/* TODO */ }
		}
		try {
			turn.applyTurn();
		} catch (IllegalTurnException e) {
			/* TODO */ }
		
		this.setGameState(Game.GameState.NORMAL);

	}

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
	 * @param players
	 *            the players to set
	 */
	public void setPlayers(List<Player> players) {
		this.players = players;
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
	 * @param board
	 *            the board to set
	 */
	public void setBoard(Board board) {
		this.board = board;
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
	 * @param tilesPerType
	 *            the tilesPerType to set
	 */
	public void setTilesPerType(int tilesPerType) {
		this.tilesPerType = tilesPerType;
	}

	/**
	 * @param currentPlayer
	 *            the currentPlayer to set
	 */
	public void setCurrentPlayer(int currentPlayer) {
		this.currentPlayer = currentPlayer;
	}

}
