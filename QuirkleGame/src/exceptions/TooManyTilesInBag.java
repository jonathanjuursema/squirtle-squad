package exceptions;

import server.Game;

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
