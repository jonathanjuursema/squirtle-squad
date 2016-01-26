package exceptions;

/**
 * This exception can be thrown whenever a turn is illegal.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
@SuppressWarnings("serial")
public class IllegalTurnException extends QwirkleException {

	public IllegalTurnException() {

	}

	public String getMessage() {
		return "This turn is illegal!";
	}

}
