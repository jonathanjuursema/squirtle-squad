package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import application.Util;

/**
 * TODO Write file header.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
public class Server extends Thread {

	public static final String FUNCTIONS = "";

	private List<Player> lobby;
	private List<Player> players;
	private List<Game> games;

	private ServerSocket socket;

	public Server(int port) throws IOException {
		this.lobby = new ArrayList<Player>();
		this.players = new ArrayList<Player>();
		this.socket = new ServerSocket(port);
		this.start();
	}

	/**
	 * The main functionality of the server.
	 */
	public void run() {
		Util.log("debug", "Server thread has started.");
		boolean running = true;
		while (running) {
			try {
				Util.log("debug", "Server is now waiting for a connection.");
				(new ServerConnectionHandler(this, this.socket.accept())).start();
				Util.log("debug", "A new client has connected.");
			} catch (IOException e) {
				Util.log("exception", "IOException caught whil listening for connections: "
								+ e.getMessage());
			}
		}
	}

	/**
	 * Add a player to the lobby.
	 * 
	 * @param player
	 *            The player.
	 */
	public void playerToLobby(Player player) {
		lobby.add(player);
	}

	/**
	 * Remove a player from the lobby.
	 * 
	 * @param player
	 *            The player.
	 */
	public void playerFromLobby(Player player) {
		lobby.remove(player);
	}

	/**
	 * Adds a player to the player list.
	 * 
	 * @param player
	 *            The player.
	 */
	public void addPlayer(Player player) {
		players.add(player);
	}

	/**
	 * Removes a player after disconnecting.
	 * 
	 * @param player
	 *            The player.
	 */
	public void removePlayer(Player player) {
		players.remove(player);
		this.playerFromLobby(player);
	}

	/**
	 * Verify if a nickname already exists.
	 * 
	 * @param name
	 *            The nickname.
	 * @return True if the name does not exist, false otherwise.
	 */
	public boolean isUniqueName(String name) {
		for (Player p : this.players) {
			if (p.getName().equals(name)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Remove all references to a game so it can be garbage collected.
	 * 
	 * @param game
	 *            The game to be removed.
	 */
	public void endGame(Game game) {
		games.remove(game);
	}

	/**
	 * Returns a list of all current games.
	 * 
	 * @return The list.
	 */
	public List<Game> getGames() {
		return this.games;
	}

}
