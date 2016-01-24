/**
 * 
 */
package exceptions;

import players.Player;

/**
 * This exception is thrown whenever a player that does not support challenges
 * or is otherwise not challangeble on this moment. is challenged.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */

@SuppressWarnings("serial")
public class PlayerCannotBeChallengedException extends QwirkleException {
	private Player p;

	public PlayerCannotBeChallengedException(Player p) {
		this.p = p;
	}

	public String getMessage() {
		return "Player " + p.getName() + " cannot be challenged.";
	}
}
