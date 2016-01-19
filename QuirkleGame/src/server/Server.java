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
	private List<Game> games;
	
	private ServerSocket socket;
	
	public Server(int port) throws IOException {
		this.lobby = new ArrayList<Player>();
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
				new ServerConnectionHandler(this.socket.accept());
			} catch (IOException e) { /* TODO */ }
		}
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
