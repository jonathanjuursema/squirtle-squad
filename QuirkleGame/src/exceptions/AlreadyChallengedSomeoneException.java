/**
 * 
 */
package exceptions;

/**
 * This exception is thrown whenever someone tries to challenge someone while
 * they already challenge someone else.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
@SuppressWarnings("serial")
public class AlreadyChallengedSomeoneException extends QwirkleException {

	public AlreadyChallengedSomeoneException() {
	}

	public String getMessage() {
		return "You have already challenged someone.";
	}
	
}
