package server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import exceptions.IllegalTurnException;
import exceptions.SquareOutOfBoundsException;
import exceptions.TileNotInBagException;
import exceptions.TooFewTilesInBagException;
import game.Bag;
import game.Game;
import game.Hand;
import game.Player;
import game.Turn;

public class GameServer extends Game {

	private Server parentServer;

	/**
	 * Initializes a new server-side game, attached to a Server.
	 * 
	 * @param server
	 */
	public GameServer(Server server) {
		this.parentServer = server;
		this.start();
	}
	
	private boolean gameOver() {
		return false;
		// TODO Implement
	}

	public void run() {
		
		// Initialise the game.
		this.setGameState(Game.GameState.INITIAL);

		// Make a new bag and fill it.
		this.setBag(new Bag(this));
		this.getBag().fill();
		
		final Lock waitForTurn = new ReentrantLock();
		final Condition turnReady = waitForTurn.newCondition();
		
		List<Turn> beginturns = new ArrayList<Turn>();

		// Assign hands to players and request first moves
		for (Player p : this.getPlayers()) {
			p.assignHand(new Hand(p));
			try {
				
				this.getBag().takeFromBag(p.getHand(), 6);
				// TODO Send hand
				
				beginturns.add(new Turn(this.getBoard(), p, turnReady));
				// TODO Send request for first move
				
			} catch (TooFewTilesInBagException | TileNotInBagException e) {
				ConnectionHandler.log("error", "Could not initialize player hands.");
			}
		}
				
		// Wait for all turns to be ready.
		boolean allTurnsReady = false;

		while (!allTurnsReady) {
			try {
				// We get a notify from another thread.
				turnReady.await();
				// See if all turns are ready.
				for (Turn t : beginturns) {
					if (t.isReady()) {
						allTurnsReady = true;
					} else {
						allTurnsReady = false;
						break;
					}
				}
			} catch (InterruptedException e) { /* TODO */ }
		}
		
		// We want to find the highest scoring move.
		Turn highestScoring = beginturns.get(0);
		
		for (Turn t : beginturns) {
			try {
				if (t.calculateScore() > highestScoring.calculateScore()) {
					highestScoring = t;
				}
			} catch (SquareOutOfBoundsException e) { /* TODO */ }
		}
		
		// Applying first move!
		try {
			highestScoring.applyTurn();
		} catch (IllegalTurnException e) { /* TODO */ }
		
		this.setCurrentPlayer(this.getPlayers().indexOf(highestScoring.getPlayer()));
		
		// Start the real game.
		this.setGameState(Game.GameState.NORMAL);
		while(!this.gameOver()) {
			this.nextTurn();
		}
		
		// Game is over
		this.setGameState(Game.GameState.FINISHED);
		
		// TODO Implement
		
	}

}
