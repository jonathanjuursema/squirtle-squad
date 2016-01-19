package server;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import game.Game;
import game.Hand;
import game.Move;
import game.Player;
import game.Tile;
import game.Turn;

public class GameServer extends Game implements ActionListener {

	private Map<Player, Turn> initialMoves;

	private Server parentServer;

	private Timer timeout;

	/**
	 * Initializes a new server-side game, attached to a Server.
	 * 
	 * @param server
	 */
	public GameServer(Server server) {
		this.parentServer = server;
		this.start();
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
	 * If the game has a winner, finish the game. This will make up the final
	 * score, inform all clients and puts the game in a final state.
	 */
	public void finish() {
		// TODO Implement body.
	}

	/**
	 * Check if the game is over and has a winner.
	 * 
	 * @return True if the game is over, false otherwise.
	 */
	public boolean gameOver() {
		return false;
		// TODO Implement
	}

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
		this.initialMove();
	}

	public void initialMove() {

		// We want to find the highest scoring move.
		Player highestScoring = null;

		for (Player p : this.initialMoves.keySet()) {
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
		}

		// Applying first move!
		try {
			this.initialMoves.get(highestScoring).applyTurn();
		} catch (SquareOutOfBoundsException e) {
			/* TODO */ }

		// TODO Set right player to start next.

		// Start the real game.
		this.setGameState(Game.GameState.NORMAL);

		this.setCurrentPlayer(highestScoring);

		this.nextTurn(1);

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
			// TODO Afvangen foutieve turn
		}
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

}
