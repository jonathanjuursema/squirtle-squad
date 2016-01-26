package exceptions;

import players.Player;

/**
 * This exception can be thrown if a player is added to the same game twice.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
@SuppressWarnings("serial")
public class PlayerAlreadyInGameException extends QwirkleException {
	private Player player;

	public PlayerAlreadyInGameException(Player p) {
		this.player = p;
	}

	public String toString() {
		return "Player " + this.player.getName() + " is already in this game.";
	}
}
