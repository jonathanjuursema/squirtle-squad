package strategies;

import game.Turn;

/**
 * The SingleStoneStrategy extends the SmartStrategy and uses the SmartStrategy
 * to determine the most valuable moves. The SingleStoneStrategy will add the
 * functionality to determine the move with the most points per tile. Therefore
 * the player place more tiles and possibly get more points. This class is work
 * in progress and does not work yet.
 * 
 * @author Peter Wessels & Jonathan Juursema
 *
 */

public class SingleStoneStrategy extends SmartStrategy implements StrategyInterface {

	public SingleStoneStrategy() {
	}

	@Override
	public synchronized Turn requestTurn(Turn turn) {
		Turn editedTurn = super.requestTurn(turn);
		editedTurn.getMoves();

		// Under construction:
		// Of each possible set of moves the total score/amount of moves will
		// determine the best valuable set of move. The most valuable tiles will
		// be placed and therefore more moves can be placed because less tiles
		// are used.
		return turn;
	}
}
