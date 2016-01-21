package exceptions;

import players.Player;

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
