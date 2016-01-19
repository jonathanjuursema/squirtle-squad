package server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.Timer;

import exceptions.IllegalTurnException;
import exceptions.SquareOutOfBoundsException;
import exceptions.TileNotInBagException;
import exceptions.TooFewTilesInBagException;
import game.Bag;
import game.Board;
import game.Hand;
import game.Move;
import game.Player;
import game.Tile;
import game.Turn;
import server.Game;

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

	public final static int TILESPERTYPE = 3;
	public final int TURNTIMEOUT = 60;

	private List<Player> players;
	private int currentPlayer;

	private Map<Player, Turn> initialMoves;

	private Server parentServer;

	private Timer timeout;

	public static enum GameState {
		WAITING, INITIAL, NORMAL, FINISHED
	};

	private GameState gameState;

	private Board board;
	private Bag bag;

	/**
	 * Initialises the game with the given players.
	 * 
	 * @param players
	 *            The players.
	 */
	public Game(Server server, List<Player> players) {
		this.players = players;
		this.parentServer = server;
		this.start();
	}

	/**
	 * Removes a player from the game.
	 * 
	 * @param player
	 *            The player to be removed.
	 */
	public void removePlayer(Player player) {
		players.remove(player);
	}

	/**
	 * Start the game.
	 */
	public void start() {

		// Initialise the game.
		this.setGameState(Game.GameState.INITIAL);

		// Make a new bag and fill it.
		this.setBag(new Bag(this));
		this.getBag().fill();

		Map<Player, Turn> beginturns = new HashMap<Player, Turn>();

		// Initialise player hands, send them, and request first turn.
		for (Player p : this.getPlayers()) {
			// Initialise hand
			p.assignHand(new Hand(p));
			try {
				this.getBag().takeFromBag(p.getHand(), 6);
			} catch (TooFewTilesInBagException | TileNotInBagException e) {
				/* TODO */ }

			// TODO Send hand to player.

			// Request initial turn
			beginturns.put(p, null);
			new Turn(this.getBoard(), p);
		}

		this.timeout = new Timer(this.TURNTIMEOUT * 1000, this);

	}

	public void receiveInitialMove(Turn turn, Player player) {
		
		this.initialMoves.put(player, turn);
		for (Turn t : this.initialMoves.values()) {
			if (t == null) {
				return;
			}
		}

		this.timeout.stop();
		this.initialMove();
		
	}

	public void initialMove() {

		// We want to find the highest scoring move.
		Player highestScoring = null;

		for (Player p : this.initialMoves.keySet()) {
			if (this.initialMoves.get(p) != null) {
				if (highestScoring == null) {
					highestScoring = p;
				} else {
					try {
						if (this.initialMoves.get(p).calculateScore() > this.initialMoves
										.get(highestScoring).calculateScore()) {
							highestScoring = p;
						}
					} catch (SquareOutOfBoundsException e) {
						/* TODO */ }
				}
			} else {
				this.disqualify(p);
			}
		}

		// Applying first move!
		try {
			this.initialMoves.get(highestScoring).applyTurn();
		} catch (SquareOutOfBoundsException e) {
			/* TODO */ }

		// Start the real game.
		this.setGameState(Game.GameState.NORMAL);

		this.setCurrentPlayer(highestScoring);

		this.nextTurn(1);

	}

	/**
	 * Check if the game is over.
	 * @return True if any of the win conditions is met. False otherwise.
	 */
	public boolean gameOver() {
		
		if (this.getPlayers().size() == 1) {
			return true;
		}
		
		if (this.getBag().getNumberOfTiles() == 0) {
			for (Player p : this.getPlayers()) {
				if (p.getHand().getTilesInHand().size() == 0) {
					return true;
				}
			}
		}
		
		return false;
		
	}

	/**
	 * Hands the current turn to a Player, awaiting their moves. Game tournament
	 * rules impose a 15 second timeout for submitting a move. If this function
	 * times out, the player is disqualified.
	 * 
	 * @param mod
	 *            The next player to be selected. This modifies the
	 *            currentPlayer field, so we have to think about what value to
	 *            put here. Example: 0 doesn't change the player, unless the a
	 *            player has been removed from the list in which case the next
	 *            player is selected. 1 picks the next player from the list in
	 *            normal situations.
	 */
	public void nextTurn(int mod) {

		this.setCurrentPlayer(this.getNextPlayer(mod));
		new Turn(this.getBoard(), this.getCurrentPlayer());

		timeout = new Timer(this.TURNTIMEOUT * 1000, this);

		this.setGameState(Game.GameState.WAITING);

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
			// TODO
		}
	}

	/**
	 * Disqualify a player. Diqualification removes a player from the game, puts
	 * their stones back in the bag and continues normal gameplay. When one
	 * player remains they win the game.
	 * 
	 * @param player
	 *            The player to be disqualified.
	 */
	public void disqualify(Player player) {
		List<Tile> tiles = player.getHand().hardResetHand();
		this.getBag().addToBag(tiles);
		this.removePlayer(player);
	}

	/**
	 * Clean-up the game.
	 */
	public void finish() {
		// TODO Implement body.
	}

	/**
	 * Timeout function that is called after the timeout is exceeded. What to do
	 * depends on what state the game is currently in.
	 */
	public void actionPerformed(ActionEvent e) {

		timeout.stop();

		if (this.getGameState() == Game.GameState.INITIAL) {
			this.initialMove();
		} else if (this.getGameState() == Game.GameState.WAITING) {
			this.disqualify(this.getCurrentPlayer());
			this.nextTurn(0);
		}

	}

	/**
	 * Exit the game for a specified reason.
	 * @param message The reason.
	 */
	public void shutdown(String message) {
		for (Player p : this.getPlayers()) {
			p.getHand().hardResetHand();
			this.removePlayer(p);
			this.parentServer.playerToLobby(p);
			// TODO Message player.
			this.parentServer.endGame(this);
		}
	}

	/*
	 * Getters and setters below.
	 */

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
