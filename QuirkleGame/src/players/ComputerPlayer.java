package players;

import application.Util;
import client.Client;
import exceptions.SquareOutOfBoundsException;
import game.Tile;

public class ComputerPlayer extends ClientPlayer {

	
	public ComputerPlayer(Client client, String name) {
		super(client, name);
	}

	@Override
	public void giveTurn() {
		for(Tile t : this.client.getPlayer().getHand().getTilesInHand()) {
			try {
				if(this.getTurn().getBoardCopy().getPossiblePlaceByTile(t, this.getTurn().getMoves()).size() > 0){
					//for(BoardSquare bs : this.getTurn().getBoardCopy().getPossiblePlaceByTile(t, this.getTurn().getMoves())){
						
					//}
				}
			} catch (SquareOutOfBoundsException e) {
				Util.log(e);
			}
		}
	}
	
	public void calculateScorePerMove(Tile t) {
		
	}

}
