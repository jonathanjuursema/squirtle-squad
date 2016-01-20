package exceptions;

import server.Game;

/**
 * This exception can be thrown if a game is attempted to start with too many
 * players.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
@SuppressWarnings("serial")
public class TooManyPlayersException extends QwirkleException {
	private int tried;

	public TooManyPlayersException(int tried) {
		this.tried = tried;
	}

	public String getMessage() {
		return "Could not start game with " + this.tried + " players, max is " + Game.MAXPLAYERS
						+ ".";
	}
}
