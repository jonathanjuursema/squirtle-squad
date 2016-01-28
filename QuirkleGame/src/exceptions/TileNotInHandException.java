package exceptions;

import game.Hand;
import game.Tile;

/**
 * When the tile is not in hand, this exception will be thrown.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
@SuppressWarnings("serial")
public class TileNotInHandException extends QwirkleException {
	public String tileRequest;
	public Hand hand;

	public TileNotInHandException(Tile tileRequest, Hand hand) {
		this(tileRequest.toString(), hand);
	}

	public TileNotInHandException(String tile, Hand hand) {
		this.tileRequest = tile;
		this.hand = hand;
	}

	public String getMessage() {
		return "The requested tile " + tileRequest + " is currently not the hand \n "
						+ this.hand;
	}
}
