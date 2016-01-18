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
	
	/**
	 * Disqualify a player. Diqualification removes a player from the game,
	 * puts their stones back in the bag and continues normal gameplay. When one player 
	 * remains they win the game.
	 * @param player The player to be disqualified.
	 */
	public void disqualify(Player player) {
		// TODO Implement body.
	}
	
	/**
	 * If the game has a winner, finish the game. This will make up the final score,
	 * inform all clients and puts the game in a final state.
	 */
	public void finish() {
		// TODO Implement body.
	}
	
	/**
	 * Check if the game is over and has a winner.
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
				
		List<Turn> beginturns = new ArrayList<Turn>();

		// TODO Implement initial turns
		
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
		} catch (SquareOutOfBoundsException e) {
			// TODO Auto-generated catch block
		}
		
		this.setCurrentPlayer(this.getPlayers().indexOf(highestScoring.getPlayer()));
		
		// Start the real game.
		this.setGameState(Game.GameState.NORMAL);
		
		this.nextTurn(1);
		
	}

}
