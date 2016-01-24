package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.Console;
import application.Util;
import exceptions.AlreadyChallengedSomeoneException;
import exceptions.PlayerAlreadyInGameException;
import exceptions.PlayerCannotBeChallengedException;
import exceptions.PlayerIsNoChallengeeException;
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

	private static final int MAXLEADERBOARDLENGTH = 10;

	private List<ServerPlayer> lobby;
	private List<ServerPlayer> players;
	private List<Game> games;

	private Map<ServerPlayer, ServerPlayer> challenges;
	private List<LeaderboardEntry> leaderboard;

	private ServerSocket socket;

	public Server(int port) throws IOException {
		this.lobby = new ArrayList<ServerPlayer>();
		this.players = new ArrayList<ServerPlayer>();
		this.socket = new ServerSocket(port);
		this.games = new ArrayList<Game>();
		this.challenges = new HashMap<ServerPlayer, ServerPlayer>();
		this.leaderboard = new ArrayList<LeaderboardEntry>();
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

	/**
	 * Tries to establish a challenge between two players.
	 * 
	 * @param challenger
	 *            The challenger.
	 * @param challengee
	 *            The one who's challenged.
	 * @throws PlayerCannotBeChallengedException
	 * @throws AlreadyChallengedSomeoneException
	 */
	public void challenge(ServerPlayer challenger, ServerPlayer challengee)
					throws PlayerCannotBeChallengedException, AlreadyChallengedSomeoneException {
		if (!challengee.canInvite() || isChallengee(challengee)) {
			throw new PlayerCannotBeChallengedException(challengee);
		} else if (isChallenger(challenger)) {
			throw new AlreadyChallengedSomeoneException();
		}
		this.challenges.put(challenger, challengee);
		challengee.invite(challenger);
	}

	/**
	 * See if the player is already challenged by someone.
	 * 
	 * @param challenger
	 *            The player.
	 * @return True of the player is already being challenged, false otherwise.
	 */
	private boolean isChallengee(ServerPlayer challengee) {
		return this.challenges.containsValue(challengee);
	}

	/**
	 * See if the specified player is already challenging someone.
	 * 
	 * @param challengee
	 *            The player.
	 * @return True of the player is already challenging, false otherwise.
	 */
	private boolean isChallenger(ServerPlayer challenger) {
		return this.challenges.containsKey(challenger);
	}

	/**
	 * The given player declines the invite.
	 * 
	 * @param challengee
	 *            The player who was challenged.
	 * @throws PlayerIsNoChallengeeException
	 */
	public void declineInvite(ServerPlayer challengee) throws PlayerIsNoChallengeeException {
		if (!isChallengee(challengee)) {
			throw new PlayerIsNoChallengeeException(challengee);
		} else {
			ServerPlayer challenger = null;
			for (ServerPlayer p : this.challenges.keySet()) {
				if (this.challenges.get(p) == challengee) {
					challenger = p;
				}
			}
			if (challenger != null) {
				challenger.decline();
				this.challenges.remove(challenger, challengee);
			} else {
				throw new PlayerIsNoChallengeeException(challengee);
			}

		}
	}

	/**
	 * The given player accepts the invite.
	 * 
	 * @param challengee
	 *            The challenged player.
	 * @throws PlayerIsNoChallengeeException
	 * @throws PlayerAlreadyInGameException
	 */
	public void acceptInvite(ServerPlayer challengee)
					throws PlayerIsNoChallengeeException, PlayerAlreadyInGameException {
		if (!isChallengee(challengee)) {
			throw new PlayerIsNoChallengeeException(challengee);
		} else {
			ServerPlayer challenger = null;
			for (ServerPlayer p : this.challenges.keySet()) {
				if (this.challenges.get(p) == challengee) {
					challenger = p;
				}
			}
			if (challenger != null) {
				try {
					Game game = new Game(this, 2);
					game.addPlayer(challengee);
					game.addPlayer(challenger);
					addGame(game);
					this.challenges.remove(challenger, challengee);
				} catch (TooManyPlayersException e) {
					Util.log(e);
				}
			} else {
				throw new PlayerIsNoChallengeeException(challengee);
			}

		}
	}

	/**
	 * When either side of a challenge enters a game, we'll forfeit any
	 * challenge.
	 */
	public void forfeitChallenge(ServerPlayer player) {
		if (isChallengee(player)) {
			ServerPlayer challenger = null;
			for (ServerPlayer p : this.challenges.keySet()) {
				if (this.challenges.get(p) == player) {
					challenger = p;
				}
			}
			if (challenger != null) {
				challenger.decline();
				this.challenges.remove(challenger);
			}
		} else if (isChallenger(player)) {
			this.challenges.remove(player);
		}
	}

	/**
	 * Submit a score the the leaderboard.
	 * 
	 * @param name
	 *            The name of the player.
	 * @param score
	 *            The score of the player.
	 */
	public void submitToLeaderboard(String name, int score) {
		LeaderboardEntry e = new LeaderboardEntry(name, score);
		if (this.leaderboard.size() < 1) {
			this.leaderboard.add(e);
		} else {
			for (int i = 0; i < this.leaderboard.size(); i++) {
				if (e.getScore() > this.leaderboard.get(i).getScore()) {
					this.leaderboard.add(i, e);
					return;
				}
				if (i >= Server.MAXLEADERBOARDLENGTH) {
					return;
				}
			}
		}
	}

	public String[] leaderboardToProtocol() {
		String[] args = new String[Server.MAXLEADERBOARDLENGTH];
		for (int i = 0; i < Server.MAXLEADERBOARDLENGTH; i++) {
			if (i >= this.leaderboard.size()) {
				args[i] = "<empty>" + Protocol.Server.Settings.DELIMITER2 + "0";
			} else {
				args[i] = this.leaderboard.get(i).getName() + Protocol.Server.Settings.DELIMITER2
								+ this.leaderboard.get(i).getScore();
			}
		}
		return args;
	}

	/**
	 * This is a small sub-class for the leaderboard.
	 * 
	 * @author Jonathan Juursema & Peter Wessels
	 */
	class LeaderboardEntry {

		private String name;
		private int score;

		public LeaderboardEntry(String name, int score) {
			this.name = name;
			this.score = score;
		}

		public int getScore() {
			return this.score;
		}

		public String getName() {
			return this.name;
		}

	}

}
