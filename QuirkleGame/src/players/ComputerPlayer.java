package players;

import application.Util;
import client.Client;
import exceptions.TileNotInHandException;
import game.Move;
import game.Turn;
import strategies.SmartStrategy;
import strategies.StrategyInterface;

/**
 * The computer player that will use the strategy which is assigned. 
 * @author Jonathan Juursema & Peter Wessels
 */

public class ComputerPlayer extends ClientPlayer {

	public ComputerPlayer(Client client) {
		super(client, "SquirtleSquad");
	}

	@Override
	public void giveTurn() {
		Turn turn = this.client.getTurn();
		StrategyInterface s = new SmartStrategy();
		turn = s.requestTurn(turn);
		
		if (turn.isMoveRequest()) {
			for (Move m : turn.getMoves()) {
				try {
					turn.getPlayer().getHand().removeFromHand(m.getTile());
					this.client.usedInPrevious.add(m.getTile());
				} catch (TileNotInHandException e) {
					Util.log(e);
				}
			}
		} else if (turn.isSwapRequest()) {
			try {
				turn.getPlayer().getHand().removeFromHand(turn.getSwap());
				this.client.usedInPrevious.addAll(turn.getSwap());
			} catch (TileNotInHandException e) {
				Util.log(e);
			}
		}
		
		this.client.setTurn(turn);
		this.client.submitTurn();
	}

}
