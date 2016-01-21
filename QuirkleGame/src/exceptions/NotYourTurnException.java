/**
 * 
 */
package exceptions;

/**
 * This exception tells you that it is not your turn, silly.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
@SuppressWarnings("serial")
public class NotYourTurnException extends QwirkleException {

	public NotYourTurnException() {}
	
	public String toString() {
		return "It is not your turn.";
	}
}
