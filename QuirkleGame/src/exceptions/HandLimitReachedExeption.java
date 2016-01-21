package exceptions;

import game.Hand;

public class HandLimitReachedExeption extends QwirkleException {
	Hand hand;
	
	public HandLimitReachedExeption(Hand hand) {
		this.hand = hand;
	}
	
	public String getMessage() {
		return "The maximum amount (" + Hand.LIMIT + ") of tiles in the hand (" + hand.getAmountOfTiles() + ") is reached.";
	}
}
