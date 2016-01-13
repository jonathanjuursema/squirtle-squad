package exceptions;

/**
 * This exception is thrown when more tiles are being requested from the bag than are in it.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
@SuppressWarnings("serial")
public class TooFewTilesInBagException extends QwirkleException {
	
	int inBag, requested;

	public TooFewTilesInBagException(int inBag, int requested) {
		this.inBag = inBag;
		this.requested = requested;
	}
	
	public String getMessage() {
		return "Tried to get " + this.requested + " tile from bag, only " + this.inBag + "there.";
	}

}
