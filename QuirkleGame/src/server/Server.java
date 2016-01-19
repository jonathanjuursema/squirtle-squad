package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO Write file header.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
public class Server extends Thread {

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
		boolean running = true;
		while (running) {
			try {
				new ServerConnectionHandler(this, this.socket.accept());
			} catch (IOException e) {
				/* TODO */ }
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

}
