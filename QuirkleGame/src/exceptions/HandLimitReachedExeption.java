package exceptions;

import game.Hand;

/**
 * Exception can be thrown whener one tries to add more stones in a hand than
 * allowed.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
@SuppressWarnings("serial")
public class HandLimitReachedExeption extends QwirkleException {
	Hand hand;

	public HandLimitReachedExeption(Hand hand) {
		this.hand = hand;
	}

	public String getMessage() {
		return "The maximum amount (" + Hand.LIMIT + ") of tiles in the hand ("
						+ hand.getAmountOfTiles() + ") is reached.";
	}
}
