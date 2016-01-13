package game;

import java.util.List;

import server.ConnectionHandler;
import server.Server;

/**
 * This class manages an entire game, including their players. It is instantiated by the
 * server several clients are put in it.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
public class Game {
	
	public final static int DIFFERENTSHAPES = 6;
	public final static int DIFFERENTCOLORS = 6;
	
	public final static int DEFAULTTILESPERTYPE = 3;
	private int tilesPerType = Game.DEFAULTTILESPERTYPE;
	
	private int playerCount;
	private List<Player> players;
	private int currentPlayer;
	
	private Server parentServer;
	private ConnectionHandler remoteServer;
	
	public static enum GameType { CLIENT, SERVER };
	private GameType gameType;
	
	public static enum GameState { WAITING, INITIAL, NORMAL, FINISHED };
	private GameState gameState;
	
	/**
	 * Initializes a new client-side game, with a conncetion to a server.
	 * @param server The ConnectionHandler to the server.
	 */
	public Game(ConnectionHandler server) {
		// TODO Implement constructor.
	}
	/**
	 * Initializes a new server-side game, attached to a Server.
	 * @param server
	 */
	public Game(Server server) {
		// TODO Implement constructor.
	}
	
	/**
	 * Adds a player to the game.
	 * @param player The player to be added.
	 */
	public void addPlayer(Player player) {
		// TODO Implement body.
	}
	
	/**
	 * Removes a player from the game. This will also disqualify the player.
	 * @param player The player to be removed.
	 */
	public void removePlayer(Player player) {
		// TODO Implement body.
	}
	
	/**
	 * Start the game!
	 */
	private void start() {
		// TODO Implement body.
	}
	
	/**
	 * Hands the current turn to a Player, awaiting their moves.
	 * Game tournament rules impose a 15 second timeout for submitting a move.
	 * If this function times out, the player is disqualified.
	 * @param player The player whose turn it is.
	 */
	private void turn(Player player) {
		// TODO Implement body.
	}
	
	/**
	 * Disqualify a player. Diqualification removes a player from the game,
	 * puts their stones back in the bag and continues normal gameplay. When one player 
	 * remains they win the game.
	 * @param player The player to be disqualified.
	 */
	private void disqualify(Player player) {
		// TODO Implement body.
	}
	
	/**
	 * If the game has a winner, finish the game. This will make up the final score,
	 * inform all clients and puts the game in a final state.
	 */
	private void finish() {
		// TODO Implement body.
	}
	
	/**
	 * Returns the amount of tiles per type for this game. Usually 3.
	 * @return The amount of tiles per type.
	 */
	public int getTilesPerType() {
		return this.tilesPerType;
	}

}
