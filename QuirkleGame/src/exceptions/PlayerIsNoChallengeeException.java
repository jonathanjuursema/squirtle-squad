/**
 * 
 */
package exceptions;

import players.Player;

/**
 * Thrown when a challange is accepted by someone who was not challenged.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
@SuppressWarnings("serial")
public class PlayerIsNoChallengeeException extends QwirkleException {
	private Player p;

	public PlayerIsNoChallengeeException(Player p) {
		this.p = p;
	}

	public String toString() {
		return p.getName() + " is not challanged.";
	}
}
