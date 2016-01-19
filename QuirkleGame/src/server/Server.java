package server;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO Write file header.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
public class Server {
	
	private List<Player> lobby;
	private List<Game> games;
	
	public Server() {
		this.lobby = new ArrayList<Player>();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * Add a player to the lobby.
	 * @param player The player.
	 */
	public void playerToLobby(Player player) {
		lobby.add(player);
	}
	
	/**
	 * Remove a player from the lobby.
	 * @param player The player.
	 */
	public void playerFromLobby(Player player) {
		lobby.remove(player);
	}
	
	public void endGame(Game game) {
		games.remove(game);
	}

}
