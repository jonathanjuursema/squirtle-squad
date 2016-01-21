package server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.Timer;

import exceptions.SquareOutOfBoundsException;
import exceptions.TileNotInBagException;
import exceptions.TooFewTilesInBagException;
import exceptions.TooManyPlayersException;
import exceptions.TooManyTilesInBag;
import game.Bag;
import game.Board;
import game.Hand;
import game.Move;
import game.Tile;
import game.Turn;
import protocol.Protocol;
import server.Game;

/**
 * This class manages an entire game, including their players. It is
 * instantiated by the server several clients are put in it.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
public abstract class Game implements ActionListener {

	public final static int DIFFERENTSHAPES = 6;
	public final static int DIFFERENTCOLORS = 6;

	public final static int TILESPERTYPE = 3;
	public final static int MAXPLAYERS = 4;
	public final int TURNTIMEOUT = 60;

	private int noOfPlayers;
	private List<Player> players = new ArrayList<Player>();
	private int currentPlayer;

	private Map<Player, Turn> initialMoves;

	private Server parentServer;

	private Timer timeout;

	public static enum GameState {
		NOTSTARTED, WAITING, INITIAL, NORMAL, FINISHED
	};

	private GameState gameState;

	private Board board;
	private Bag bag;

	/**
	 * Initialises the game with the given players.
	 * 
	 * @param players
	 *            The players.
	 * @throws TooManyPlayersException
	 */
	public Game(Server server, int noOfPlayers) throws TooManyPlayersException {
		this.parentServer = server;
		if (noOfPlayers > 1 && noOfPlayers < Game.MAXPLAYERS) {
			this.noOfPlayers = noOfPlayers;
		} else {
			throw new TooManyPlayersException(noOfPlayers);
		}
		this.gameState = Game.GameState.NOTSTARTED;
	}

	/**
	 * Start the game.
	 */
	public void start() {

		// Initialise the game.
		this.gameState = Game.GameState.INITIAL;

		// Make a new bag and fill it.
		this.bag = new Bag();
		this.bag.fill();

		Map<Player, Turn> beginturns = new HashMap<Player, Turn>();

		// Initialise player hands, send them, and request first turn.
		for (Player p : this.players) {
			// Initialise hand
			p.assignHand(new Hand());
			try {
				this.bag.takeFromBag(p.getHand(), 6);
			} catch (TooFewTilesInBagException | TileNotInBagException e) {
				this.shutdown("Irrecoverable exception during game initialisation.");
			}

			List<Tile> tiles = p.getHand().getTilesInHand();
			String[] args = new String[tiles.size()];
			for (int i = 0; i < tiles.size(); i++) {
				args[i] = "" + tiles.get(i).getColor() + tiles.get(i).getShape();
			}
			p.sendMessage(Protocol.Server.ADDTOHAND, args);
			p.sendMessage(Protocol.Server.STARTGAME, new String[] {});

			// Request initial turn
			beginturns.put(p, null);
			new Turn(this.board, p);
		}

		this.timeout = new Timer(this.TURNTIMEOUT * 1000, this);

	}

	public void receiveInitialMove(Turn turn, Player player) {

		this.initialMoves.put(player, turn);
		for (Turn t : this.initialMoves.values()) {
			if (t == null) {
				return;
			}
		}

		this.timeout.stop();
		this.initialMove();

	}

	public void initialMove() {

		// We want to find the highest scoring move.
		Player highestScoring = null;

		for (Player p : this.initialMoves.keySet()) {
			if (this.initialMoves.get(p) != null) {
				if (highestScoring == null) {
					highestScoring = p;
				} else {
					try {
						if (this.initialMoves.get(p).calculateScore() > this.initialMoves
										.get(highestScoring).calculateScore()) {
							highestScoring = p;
						}
					} catch (SquareOutOfBoundsException e) {
						shutdown("Irrecoverable exception in determinging scores of first moves.");
					}
				}
			} else {
				this.disqualify(p);
			}
		}

		// Applying first move!
		try {
			this.initialMoves.get(highestScoring).applyTurn();
		} catch (SquareOutOfBoundsException e) {
			shutdown("Irrecoverable error in applying the first move.");
		}

		// Start the real game.
		this.gameState = Game.GameState.NORMAL;

		this.setCurrentPlayer(highestScoring);

		this.nextTurn(1);

	}

	/**
	 * Check if the game is over.
	 * 
	 * @return True if any of the win conditions is met. False otherwise.
	 */
	public boolean gameOver() {

		if (this.players.size() == 1) {
			return true;
		}

		if (this.bag.getNumberOfTiles() == 0) {
			for (Player p : this.players) {
				if (p.getHand().getTilesInHand().size() == 0) {
					return true;
				}
			}
		}

		return false;

	}

	/**
	 * Hands the current turn to a Player, awaiting their moves. Game tournament
	 * rules impose a 15 second timeout for submitting a move. If this function
	 * times out, the player is disqualified.
	 * 
	 * @param mod
	 *            The next player to be selected. This modifies the
	 *            currentPlayer field, so we have to think about what value to
	 *            put here. Example: 0 doesn't change the player, unless the a
	 *            player has been removed from the list in which case the next
	 *            player is selected. 1 picks the next player from the list in
	 *            normal situations.
	 */
	public void nextTurn(int mod) {

		this.setCurrentPlayer(this.getNextPlayer(mod));
		new Turn(this.board, this.getCurrentPlayer());

		timeout = new Timer(this.TURNTIMEOUT * 1000, this);

		this.gameState = Game.GameState.WAITING;

	}

	/**
	 * Entry function which player can use to signal their turn is done.
	 * 
	 * @param turn
	 */
	public void receiveTurn(Turn turn) {

		String[] args;

		if (turn.isSwapRequest()) {

			timeout.stop();
			List<Tile> tilesToSwap = turn.getSwap();
			Hand h = this.getCurrentPlayer().getHand();

			try {
				bag.swapTiles(h, tilesToSwap);
				// TODO Hier moet nog een exceptie komen en worden afgevangen
				// met een error 2
			} catch (TooFewTilesInBagException e) {
				this.getCurrentPlayer().sendMessage(Protocol.Server.ERROR,
								new String[] { "3", "There are not that many stones in the bag." });
				return;
			} catch (TileNotInBagException | TooManyTilesInBag e) {
				this.shutdown("Irrecoverable exception during swap: " + e.getMessage());
			}

			args = new String[2];

		} else if (turn.isMoveRequest()) {

			timeout.stop();
			List<Move> moves = turn.getMoves();

			args = new String[2 + (moves.size() * 2)];

			for (int i = 0; i < moves.size(); i++) {
				Move m = moves.get(i);
				try {
					
					board.placeTile(m.tileToPlay, m.getPosition().getX(), m.getPosition().getY());
					
					// Try to construct protocol arguments... Ugly. :/
					args[2 + (i * 2)] = "" + m.tileToPlay.getColor() + m.tileToPlay.getShape();
					args[2 + (i * 2) + 1] = "" + m.getPosition().getX()
									+ Protocol.Server.Settings.DELIMITER2 + m.getPosition().getY();
					
				} catch (SquareOutOfBoundsException e) {
					this.shutdown("Irrecoverable exception during move performing: "
									+ e.getMessage());
				}
			}

		} else {

			this.disqualify(this.getCurrentPlayer());
			return;

		}

		for (Player p : this.players) {
			p.sendMessage(Protocol.Server.MOVE, args);
		}

	}

	/**
	 * Disqualify a player. Diqualification removes a player from the game, puts
	 * their stones back in the bag and continues normal gameplay. When one
	 * player remains they win the game.
	 * 
	 * @param player
	 *            The player to be disqualified.
	 */
	public void disqualify(Player player) {
		List<Tile> tiles = player.getHand().hardResetHand();
		try {
			this.bag.addToBag(tiles);
		} catch (TooManyTilesInBag e) {
			this.shutdown("Irrecoverable exception during player disqualification: "
							+ e.getMessage());
		}
		this.removePlayer(player);
		this.nextTurn(0);
	}

	/**
	 * Exit the game for a specified reason.
	 * 
	 * @param message
	 *            The reason.
	 */
	public void shutdown(String message) {
		for (Player p : this.players) {
			p.getHand().hardResetHand();
			this.removePlayer(p);
			this.parentServer.playerToLobby(p);
			// TODO Message player.
			this.parentServer.endGame(this);
		}
	}

	/**
	 * Removes a player from the game.
	 * 
	 * @param player
	 *            The player to be removed.
	 */
	public void removePlayer(Player player) {
		players.remove(player);
	}

	/**
	 * Adds a player to the game.
	 * 
	 * @param player
	 *            The player to be added.
	 */
	public void addPlayer(Player player) {
		if (!players.contains(player)) {
			players.add(player);
			this.parentServer.playerFromLobby(player);
			if (players.size() == this.noOfPlayers) {
				this.start();
			}
		}
	}

	/**
	 * Timeout function that is called after the timeout is exceeded. What to do
	 * depends on what state the game is currently in.
	 */
	public void actionPerformed(ActionEvent e) {

		timeout.stop();

		if (this.gameState == Game.GameState.INITIAL) {
			this.initialMove();
		} else if (this.gameState == Game.GameState.WAITING) {
			this.disqualify(this.getCurrentPlayer());
			this.nextTurn(0);
		}

	}

	/*
	 * Getters and setters below.
	 */

	/**
	 * Get the current player for this game.
	 */
	public Player getCurrentPlayer() {
		return this.players.get(this.currentPlayer);
	}

	/**
	 * Get the next player for this game.
	 * 
	 * @param mod
	 *            The next player to be selected. This modifies the
	 *            currentPlayer field, so we have to think about what value to
	 *            put here. Example: 0 doesn't change the player, unless the a
	 *            player has been removed from the list in which case the next
	 *            player is selected. 1 picks the next player from the list in
	 *            normal situations.
	 */
	public Player getNextPlayer(int mod) {
		return this.players.get((this.currentPlayer + mod) % this.players.size());
	}

	/**
	 * @param currentPlayer
	 *            the currentPlayer to set
	 */
	public void setCurrentPlayer(Player p) {
		this.currentPlayer = this.players.indexOf(p);
	}

	/**
	 * Get the number of players for this game.
	 * 
	 * @return The number of players.
	 */
	public int getNoOfPlayers() {
		return this.noOfPlayers;
	}

	/**
	 * Get the game state.
	 * 
	 * @return The state.
	 */
	public Game.GameState getGameState() {
		return this.gameState;
	}

}
