package players;

import java.util.List;

import game.Tile;
import game.Turn;
import strategies.StrategyInterface;

/**
 * Under construction.
 * @author Jonathan Juursema & Peter Wessels
 */

public class ServerAI extends ServerPlayer {
	
	private StrategyInterface strategy;

	public ServerAI(StrategyInterface strategy) {
		super("SquirtleSquad");
		this.strategy = strategy;
	}
	
	@Override
	public void giveTurn(Turn turn) {
		this.getGame().receiveTurn(strategy.requestTurn(turn));
	}

	@Override
	public void addToHand(List<Tile> applyTurn) {
	}
}
