package exceptions;

/**
 * This exception can be thrown whenever a players tries to do something whilst
 * he isn't in a game.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */

@SuppressWarnings("serial")
public class NotInGameException extends QwirkleException {
	public NotInGameException() {

	}

	public String getMessage() {
		return "You are not in a game.";
	}
}
