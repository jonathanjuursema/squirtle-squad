package exceptions;

import game.Tile;

/**
 * This exception is thrown whenever something tries to take something out of
 * the bag that isn't in it.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
@SuppressWarnings("serial")
public class TileNotInBagException extends QwirkleException {

	private Tile tile;

	public TileNotInBagException(Tile tile) {
		this.tile = tile;
	}

	public String getMessage() {
		return "The tile " + tile.toString() + " is not in the bag.";
	}

}
