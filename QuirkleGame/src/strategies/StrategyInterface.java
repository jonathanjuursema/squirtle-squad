package strategies;

import game.Turn;

/**
 * This interface abstracts all strategies. A strategy should implement a single
 * public method: requestTurn
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
public interface StrategyInterface {

	/**
	 * Requests the turn to be done. Can either be move or swap.
	 * 
	 * @param turn
	 *            The turn to be filled.
	 * @return The filled turn.
	 */
	public Turn requestTurn(Turn turn);

}
