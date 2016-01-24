package exceptions;

/**
 * This exception is thrown when a square outside the game board is being
 * accessed.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
@SuppressWarnings("serial")
public class SquareOutOfBoundsException extends QwirkleException {

	int index, max, min;
	char vector;

	public SquareOutOfBoundsException(int index, int max, char vector) {
		this.index = index - (max / 2);
		this.max = max / 2;
		this.min = (this.max - 1) * -1;
		this.vector = vector;
	}

	public String getMessage() {
		return "The " + this.vector + " coordinate is out of bounds. " + "Requested " + this.index
						+ " but should be " + this.min + ".." + this.max + ".";
	}

}
