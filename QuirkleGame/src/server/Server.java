package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import application.Console;
import application.Util;
import exceptions.PlayerAlreadyInGameException;
import exceptions.TooManyPlayersException;
import players.Player;
import players.ServerPlayer;
import protocol.Protocol;

/**
 * TODO Write file header.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
public class Server extends Thread {

	public static final String[] FUNCTIONS = { "CHALLENGE", "CHAT", "LEADERBORD" };

	private List<ServerPlayer> lobby;
	private List<ServerPlayer> players;
	private List<Game> games;

	private ServerSocket socket;

	public Server(int port) throws IOException {
		this.lobby = new ArrayList<ServerPlayer>();
		this.players = new ArrayList<ServerPlayer>();
		this.socket = new ServerSocket(port);
		this.games = new ArrayList<Game>();
		this.start();
	}

	/**
	 * The main functionality of the server.
	 */
	public void run() {
		Console.println("Server is now accepting connections. Enjoy your game!");
		Util.log("debug", "Server thread has started.");
		boolean running = true;
		while (running) {
			try {
				Util.log("debug", "Server is now waiting for a connection.");
				(new ServerConnectionHandler(this, this.socket.accept())).start();
				Util.log("debug", "A new client has connected.");
			} catch (IOException e) {
				Util.log(e);
			}
		}
	}

	/**
	 * Submits a chat message to the server. The server will prepend the
	 * nickname and send it to all supported clients.
	 * 
	 * @param player
	 * @param message
	 */
	public void chat(ServerPlayer player, String message) {
		String text = "(" + player.getName() + ") " + message;
		if (this.isInGame(player)) {
			player.getGame().sendChat(text);
		} else {
			for (ServerPlayer p : this.lobby) {
				if (p.canChat()) {
					p.sendMessage(Protocol.Server.CHAT, new String[] { text });
				}
			}
		}
		Util.log("debug", "Received chat from " + player.getName() + ": " + message);
	}

	/**
	 * Add a player to the lobby.
	 * 
	 * @param player
	 *            The player.
	 */
	public void playerToLobby(ServerPlayer player) {
		lobby.add(player);
		Util.log("debug", player.getName() + " joined the lobby.");
	}

	/**
	 * Remove a player from the lobby.
	 * 
	 * @param player
	 *            The player.
	 */
	public void playerFromLobby(ServerPlayer player) {
		lobby.remove(player);
		Util.log("debug", player.getName() + " left the lobby.");
	}

	/**
	 * Adds a player to the player list.
	 * 
	 * @param player
	 *            The player.
	 */
	public void addPlayer(ServerPlayer player) {
		players.add(player);
		Util.log("debug", player.getName() + " joined the server.");
	}

	/**
	 * Removes a player after disconnecting.
	 * 
	 * @param player
	 *            The player.
	 */
	public void removePlayer(ServerPlayer player) {
		players.remove(player);
		this.playerFromLobby(player);
		Util.log("debug", player.getName() + " left the server.");
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
	public void removeGame(Game game) {
		games.remove(game);
		Util.log("debug", "A game of " + game.getNoOfPlayers() + " has been removed.");
	}

	/**
	 * Add a new game to the list of current games.
	 * 
	 * @param game
	 *            The game. You lost it.
	 */
	public void addGame(Game game) {
		this.games.add(game);
		Util.log("debug", "A game of " + game.getNoOfPlayers() + " has been created.");
	}

	/**
	 * Try to find a game for a player for a specified amount of players. If
	 * none can be found, create a new one.
	 * 
	 * @param serverConnectionHandler
	 * @param player
	 * @param noOfPlayers
	 * @throws TooManyPlayersException
	 * @throws PlayerAlreadyInGameException
	 */
	public void findGameFor(ServerPlayer player, int noOfPlayers)
					throws TooManyPlayersException, PlayerAlreadyInGameException {
		// TODO Support computer player.
		for (Game game : this.games) {
			if (game.getGameState() == Game.GameState.NOTSTARTED
							&& game.getNoOfPlayers() == noOfPlayers) {
				game.addPlayer(player);
				return;
			}
		}
		if (this.isInGame(player)) {
			throw new PlayerAlreadyInGameException(player);
		}
		Game game = new Game(this, noOfPlayers);
		game.addPlayer(player);
		addGame(game);
		Util.log("debug", "Created a game of " + game.getNoOfPlayers() + " for " + player.getName()
						+ ".");
	}

	/**
	 * Checks if the given player is currently in a game.
	 * 
	 * @param player
	 *            The player.
	 * @return True if the player is in a game, false otherwise.
	 */
	public boolean isInGame(ServerPlayer player) {
		for (Game game : this.games) {
			if (game.isPlayer(player) == true) {
				return true;
			}
		}
		return false;
	}

}
