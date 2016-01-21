package exceptions;

import game.Hand;
import game.Tile;

public class TileNotInHandException extends QwirkleException {
	public Tile tileRequest;
	public Hand hand;

	public TileNotInHandException(Tile tileRequest, Hand hand) {
		this.tileRequest = tileRequest;
		this.hand = hand;
	} 
	
	public String getMessage() {
		return "The requested tile " + tileRequest.toString() + " is currently not the hand \n " + this.hand;
	}
}
