package server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Timer;

import application.Util;
import exceptions.AlreadyChallengedSomeoneException;
import exceptions.PlayerAlreadyInGameException;
import exceptions.PlayerCannotBeChallengedException;
import exceptions.PlayerIsNoChallengeeException;
import exceptions.TooManyPlayersException;
import players.Player;
import players.ServerAI;
import players.ServerHuman;
import protocol.Protocol;
import strategies.SmartStrategy;

/**
 * This is the server. The server is responsible for accepting incoming
 * connections, managing games and players, and so one. The server holds a lobby
 * of players not in a game, and a list of games and their associated players. A
 * player can not be in the lobby and a game at the same time.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
public class Server extends Thread implements ActionListener {

	public static final String[] FUNCTIONS = { "CHALLENGE", "CHAT", "LEADERBOARD" };

	private static final int MAXLEADERBOARDLENGTH = 10;

	private List<ServerHuman> lobby;
	private List<ServerHuman> players;
	private List<Game> games;

	private Map<ServerHuman, ServerHuman> challenges;
	private HashMap<String, Integer> leaderboard;

	private ServerSocket socket;

	public Server(int port) throws IOException {
		this.lobby = new ArrayList<ServerHuman>();
		this.players = new ArrayList<ServerHuman>();
		this.socket = new ServerSocket(port);
		this.games = new ArrayList<Game>();
		this.challenges = new HashMap<ServerHuman, ServerHuman>();
		this.leaderboard = new HashMap<String, Integer>();
		this.start();

		(new Timer(5000, this)).start();
	}

	/**
	 * Perform clean-up of empty games and disconnected players.
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		ArrayList<ServerHuman> cleanupPlayers = new ArrayList<ServerHuman>();
		cleanupPlayers.addAll(players);
		ArrayList<Game> cleanupGames = new ArrayList<Game>();
		cleanupGames.addAll(games);

		for (Game g : cleanupGames) {
			if (g.gameOver() && g.getGameState() != Game.GameState.NOTSTARTED) {
				Util.log("debug", "Game is lingering.");
				this.removeGame(g);
			}
		}

		for (ServerHuman p : cleanupPlayers) {
			if (!p.isConnected()) {
				Util.log("debug", "Player " + p.getName() + " is lingering.");
				this.removePlayer(p);
			}
		}
	}

	/**
	 * The main functionality of the server.
	 */
	public void run() {
		Util.println("Server is now accepting connections. Enjoy your game!");
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
	public void chat(ServerHuman player, String message) {
		String text = "<" + player.getName() + "> " + message;
		if (this.isInGame(player)) {
			Util.log("debug", "Received game chat from " + player.getName() + ": " + message);
			player.getGame().sendChat(text);
		} else {
			Util.log("debug", "Received lobby chat from " + player.getName() + ": " + message);
			for (ServerHuman p : this.lobby) {
				if (p.canChat()) {
					p.sendMessage(Protocol.Server.CHAT, new String[] { text });
				}
			}
		}
	}

	/**
	 * Add a player to the lobby.
	 * 
	 * @param player
	 *            The player.
	 */
	public void playerToLobby(ServerHuman player) {
		if (!lobby.contains(player)) {
			lobby.add(player);
			this.chat(player, "< entered the lobby >");
			if (player.canChat()) {
				String inLobby = "";
				for (ServerHuman p : this.lobby) {
					inLobby += p.getName() + " ";
				}
				player.sendMessage(Protocol.Server.CHAT,
								new String[] { "<Server> Currently in the lobby: " + inLobby });
			}
			Util.log("debug", player.getName() + " joined the lobby.");
		}
	}

	/**
	 * Remove a player from the lobby.
	 * 
	 * @param player
	 *            The player.
	 */
	public void playerFromLobby(ServerHuman player) {
		if (lobby.contains(player)) {
			lobby.remove(player);
			this.chat(player, "< left the lobby >");
			Util.log("debug", player.getName() + " left the lobby.");
		}
	}

	/**
	 * Adds a player to the player list.
	 * 
	 * @param player
	 *            The player.
	 */
	public void addPlayer(ServerHuman player) {
		if (!players.contains(player)) {
			players.add(player);
			Util.log("debug", player.getName() + " joined the server.");
		}
	}

	/**
	 * Removes a player after disconnecting.
	 * 
	 * @param player
	 *            The player.
	 */
	public void removePlayer(ServerHuman player) {
		if (players.contains(player)) {
			this.playerFromLobby(player);
			players.remove(player);
			Util.log("debug", player.getName() + " left the server.");
		}
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
		game.playersToLobby();
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
	public void findGameFor(ServerHuman player, int noOfPlayers)
					throws TooManyPlayersException, PlayerAlreadyInGameException {

		if (noOfPlayers == 1) {
			Game game = new Game(this, 2);
			game.addPlayer(player);
			game.addPlayer(new ServerAI(new SmartStrategy()));
			addGame(game);
			Util.log("debug", "Created an AI game for "
							+ player.getName() + ".");
		} else {
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
			Util.log("debug", "Created a game of " + game.getNoOfPlayers() + " for "
							+ player.getName() + ".");
		}
	}

	/**
	 * Checks if the given player is currently in a game.
	 * 
	 * @param player
	 *            The player.
	 * @return True if the player is in a game, false otherwise.
	 */
	public boolean isInGame(ServerHuman player) {
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
	public void challenge(ServerHuman challenger, String challengeeName)
					throws PlayerCannotBeChallengedException, AlreadyChallengedSomeoneException {

		ServerHuman challengee = null;

		for (ServerHuman p : this.players) {
			if (p.getName().equals(challengeeName)) {
				challengee = p;
			}
		}

		if (challengee == null) {
			throw new PlayerCannotBeChallengedException(challengee);
		}

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
	private boolean isChallengee(ServerHuman challengee) {
		return this.challenges.containsValue(challengee);
	}

	/**
	 * See if the specified player is already challenging someone.
	 * 
	 * @param challengee
	 *            The player.
	 * @return True of the player is already challenging, false otherwise.
	 */
	private boolean isChallenger(ServerHuman challenger) {
		return this.challenges.containsKey(challenger);
	}

	/**
	 * The given player declines the invite.
	 * 
	 * @param challengee
	 *            The player who was challenged.
	 * @throws PlayerIsNoChallengeeException
	 */
	public void declineInvite(ServerHuman challengee) throws PlayerIsNoChallengeeException {
		if (!isChallengee(challengee)) {
			throw new PlayerIsNoChallengeeException(challengee);
		} else {
			ServerHuman challenger = null;
			for (ServerHuman p : this.challenges.keySet()) {
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
	public void acceptInvite(ServerHuman challengee)
					throws PlayerIsNoChallengeeException, PlayerAlreadyInGameException {
		if (!isChallengee(challengee)) {
			throw new PlayerIsNoChallengeeException(challengee);
		} else {
			ServerHuman challenger = null;
			for (ServerHuman p : this.challenges.keySet()) {
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
	public void forfeitChallenge(ServerHuman player) {
		if (isChallengee(player)) {
			ServerHuman challenger = null;
			for (ServerHuman p : this.challenges.keySet()) {
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
		this.leaderboard.put(name, score);
	}

	/**
	 * Convert the leaderboard object to protocol, so it can be send to clients.
	 * 
	 * @return Arguments that can be passed directly into the client message
	 *         sender.
	 */
	public String[] leaderboardToProtocol() {
		this.leaderboard = Util.sortLeaderboard(leaderboard);
		String[] args = new String[(this.leaderboard.keySet().size() < Server.MAXLEADERBOARDLENGTH ? this.leaderboard.keySet().size() : Server.MAXLEADERBOARDLENGTH)];
		int i = 0;
		for (String name : this.leaderboard.keySet()) {
			if (i < Server.MAXLEADERBOARDLENGTH) {
				args[i] = name + Protocol.Server.Settings.DELIMITER2
								+ this.leaderboard.get(name);
			}
			i++;
		}
		return args;
	}

}
