package exceptions;

import server.Game;

/**
 * This exception can be thrown when there are too many tiles in the bag.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
@SuppressWarnings("serial")
public class TooManyTilesInBag extends QwirkleException {

	int inBag, addAmount;

	public TooManyTilesInBag(int inBag, int addAmount) {
		this.inBag = inBag;
		this.addAmount = addAmount;
	}

	public String getMessage() {
		return "Tried to add " + this.addAmount + " tiles to bag, but has allready " + this.inBag
						+ "/" + Game.DIFFERENTCOLORS * Game.DIFFERENTSHAPES * Game.TILESPERTYPE;
	}
}
