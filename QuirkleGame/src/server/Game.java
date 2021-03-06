package server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.Timer;

import application.Util;
import exceptions.HandLimitReachedExeption;
import exceptions.IllegalMoveException;
import exceptions.IllegalTurnException;
import exceptions.PlayerAlreadyInGameException;
import exceptions.SquareOutOfBoundsException;
import exceptions.TileNotInBagException;
import exceptions.TileNotInHandException;
import exceptions.TooFewTilesInBagException;
import exceptions.TooManyPlayersException;
import exceptions.TooManyTilesInBag;
import game.Bag;
import game.Board;
import game.BoardSquare;
import game.Hand;
import game.Move;
import game.Tile;
import game.Turn;
import players.ServerHuman;
import players.ServerPlayer;
import protocol.Protocol;
import server.Game;

/**
 * This class constitutes a server-side game and as such represents the state of
 * a game as known by the server. The Game manages a board and a bag, as well as
 * any number of players. The game communicates to Clients via intermediate
 * Players.
 * 
 * The game orchestrates the game flow. It tells clients when it is their turn,
 * receives the commands from the clients and permutates the board and the bag
 * accordingly.
 * 
 * A game can be constructed, after which players can be added. As soon as the
 * requested number of players is reached, the game is automatically started.
 * Clients are requested to submit their initial turn, after which all turns are
 * examined and the highest scoring turn is allowed to start. After that it
 * alternates turns between every player, enforcing a time limit per turn and
 * disqualifying players when necessary.
 * 
 * When end game conditions for Qwirkle have been met, the Game finishes and
 * sends all players the result. When this is done, the game is emptied (all
 * players are put back in the lobby) and the Game object is disposed of.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
public class Game implements ActionListener {

	public final static int DIFFERENTSHAPES = 6;
	public final static int DIFFERENTCOLORS = 6;

	public final static int TILESPERTYPE = 3;
	public final static int MAXPLAYERS = 4;
	public final int TURNTIMEOUT = 15;

	private static final int BONUS_WHEN_FINISH_FIRST = 6;

	private Server parentServer;
	private Board board;
	private Bag bag;
	private Timer timeout;

	// @ public invariant noOfPlayers >= 0 && noOfPlayers <= MAXPLAYERS
	private int noOfPlayers;

	// @ public invariant players.size == noOfPlayers
	private List<ServerPlayer> players = new ArrayList<ServerPlayer>();

	private int currentPlayer;
	private Map<ServerPlayer, Turn> initialMoves;

	public static enum GameState {
		NOTSTARTED, WAITING, INITIAL, NORMAL, FINISHED
	};

	private GameState gameState;

	/**
	 * Initialises the game with the given number of players.
	 * 
	 * @param players
	 *            The players.
	 * @throws TooManyPlayersException
	 * 
	 * @ requires noOfPlayers >= 0 && noOfPlayers <= MAXPLAYERS
	 */
	public Game(Server server, int noOfPlayers) throws TooManyPlayersException {
		this.parentServer = server;
		if (noOfPlayers > 1 && noOfPlayers < Game.MAXPLAYERS) {
			this.noOfPlayers = noOfPlayers;
		} else {
			throw new TooManyPlayersException(noOfPlayers);
		}

		this.board = new Board();

		this.gameState = Game.GameState.NOTSTARTED;

		Util.log("debug", "A new game has been initialized for " + this.getNoOfPlayers()
						+ " players.");
	}

	/**
	 * Adds a player to the game.
	 * 
	 * @param player
	 *            The player to be added.
	 * @throws PlayerAlreadyInGameException
	 * 
	 * @ requires !isPlayer(player) @ ensures isPlayer(player)
	 */
	public void addPlayer(ServerPlayer player) throws PlayerAlreadyInGameException {
		if (!players.contains(player)) {
			players.add(player);
			player.setGame(this);
			Util.log("debug",
							player.getName() + " joined a game for " + this.getNoOfPlayers() + ".");
		} else {
			throw new PlayerAlreadyInGameException(player);
		}
		if (player instanceof ServerHuman) {
			this.parentServer.playerFromLobby((ServerHuman) player);
			if (players.size() == this.noOfPlayers) {
				this.start();
			} else {
				for (ServerPlayer p : this.players) {
					if (p instanceof ServerHuman) {
						((ServerHuman) p).sendMessage(Protocol.Server.OKWAITFOR, new String[] {
								"" + (this.getNoOfPlayers() - this.players.size()) });
					}
				}
			}
		}
	}

	/**
	 * Start the game.
	 * 
	 * @ requires players.size() == noOfPlayers @ ensures (\forall SeverPlayer
	 * player; players.contains(player); player.getHand().getAmountOfTiles ==
	 * 6) @ ensures bag.getNumberOfTiles == (DIFFERENTSHAPES * DIFFERENTCOLORS *
	 * TILESPERTYPE) - 6 * noOfPlayers
	 */
	public void start() {

		Util.log("debug", "Starting game for " + this.getNoOfPlayers() + " players.");

		// Initialise the game.
		this.gameState = Game.GameState.INITIAL;

		// Make a new bag and fill it.
		this.bag = new Bag();
		this.bag.fill();

		this.initialMoves = new HashMap<ServerPlayer, Turn>();

		// Constructing player names to send to client.
		String[] playerNames = new String[this.noOfPlayers];
		for (int i = 0; i < this.noOfPlayers; i++) {
			playerNames[i] = this.players.get(i).getName();
		}

		// Initialise player hands, send them, and request first turn.
		for (ServerPlayer p : this.players) {
			// Initialise hand
			p.assignHand(new Hand());
			try {
				this.bag.takeFromBag(p.getHand(), 6);
			} catch (TooFewTilesInBagException | TileNotInBagException
							| HandLimitReachedExeption e) {
				Util.log(e);
				this.shutdown("Irrecoverable exception during game initialisation.");
			}

			List<Tile> tiles = p.getHand().getTilesInHand();
			String[] args = new String[tiles.size()];
			for (int i = 0; i < tiles.size(); i++) {
				args[i] = tiles.get(i).toProtocol();
			}

			if (p instanceof ServerHuman) {
				((ServerHuman) p).sendMessage(Protocol.Server.ADDTOHAND, args);
				((ServerHuman) p).sendMessage(Protocol.Server.STARTGAME, playerNames);
			}

			// Request initial turn
			initialMoves.put(p, null);
			Turn turn = new Turn(this.board, p);
			p.giveTurn(turn);
		}

		this.timeout = new Timer(this.TURNTIMEOUT * 1000, this);
		this.timeout.start();

	}

	/**
	 * Submit an initial move for a specified player.
	 * 
	 * @param turn
	 *            The initial turn.
	 * @param player
	 *            The player.
	 * 
	 * @ requires (\forall Move m; turn.getMoves().contains(m);
	 * m.isValidMove(turn.getBoardCoby, turn))
	 */
	public void receiveInitialMove(Turn turn, ServerPlayer player) {

		Util.log("debug", "Recevied initial move for " + player.getName() + ".");

		if (turn.isMoveRequest()) {

			this.initialMoves.put(player, turn);

			for (Turn t : this.initialMoves.values()) {
				if (t == null) {
					return;
				}
			}

			this.timeout.stop();
			this.initialMove();

		} else {

			if (player instanceof ServerHuman) {
				((ServerHuman) player).sendMessage(Protocol.Server.ERROR,
								new String[] { "7", "NoSwapAllowed" });
			}

		}

	}

	/**
	 * Process the initial move.
	 * 
	 * We could write JML to ensure the highest scoring move is executed. But
	 * since this function executes the turn and not returns it, we cannot
	 * access the played turn via JML. Otherwise it would be like this:
	 * 
	 * @ ensures (\forall Turn t; intialMoves.values().contains(t);
	 * t.calCulateScore() <= theTurnThatGotPlayed.calculateScore())
	 */
	public void initialMove() {

		// We want to find the highest scoring move.
		ServerPlayer highestScoring = null;

		List<ServerPlayer> toDisqualify = new ArrayList<ServerPlayer>();

		for (ServerPlayer p : this.initialMoves.keySet()) {
			if (this.initialMoves.get(p) != null) {
				if (highestScoring == null) {
					highestScoring = p;
				} else {
					try {
						if (this.initialMoves.get(p).calculateScore() > this.initialMoves
										.get(highestScoring).calculateScore()) {
							highestScoring = p;
						}
					} catch (SquareOutOfBoundsException | IllegalMoveException e) {
						Util.log(e);
						shutdown("Unrecoverable exception in determinging scores of first moves.");
					}
				}
			} else {
				toDisqualify.add(p);
			}
		}

		for (ServerPlayer p : toDisqualify) {
			this.disqualify(p);
		}

		// Applying first move!
		this.gameState = Game.GameState.NORMAL;
		this.setCurrentPlayer(highestScoring);
		this.receiveTurn(this.initialMoves.get(highestScoring));

	}

	/**
	 * Timeout function that is called after the timeout is exceeded. What to do
	 * depends on what state the game is currently in.
	 */
	public void actionPerformed(ActionEvent e) {

		Util.log("debug", "Timeout occured in a game for " + this.getNoOfPlayers() + ".");

		timeout.stop();

		if (this.gameState == Game.GameState.INITIAL) {
			this.initialMove();
		} else if (this.gameState == Game.GameState.WAITING) {
			this.disqualify(this.getCurrentPlayer());

			for (ServerPlayer p : this.players) {
				if (p instanceof ServerHuman) {
					((ServerHuman) p).sendMessage(Protocol.Server.MOVE, new String[] {
							"Disqualified", this.getNextPlayer(0).toString() });
				}
			}

			this.nextTurn(0);
		}

	}

	/**
	 * Entry function for submission of a turn into a game.
	 * 
	 * @param turn
	 * 
	 * @ requires (\forall Move m; turn.getMoves().contains(m);
	 * m.isValidMove(turn.getBoardCoby, turn))
	 * 
	 * We could write JML to verify every tile played is now on the current
	 * board, but we have no easy way of looping over all BoardSquares on the
	 * board (because the array is 2-dimensional). Otherwise it would look like
	 * this:
	 * 
	 * @ ensures (\forall Move m; turn.getMoves().contains(m); (\exists
	 * BoardSquare b; board.isBoardSquareOfThis(b); b.getTile() ==
	 * turn.getMoves().get(1).tileToPlay))
	 */
	public void receiveTurn(Turn turn) {

		try {

			timeout.stop();
			this.getCurrentPlayer().addToHand(turn.applyTurn(this.board, this.bag));
			turn.assignedPlayer.incrementScore(turn.calculateScore());

			String[] args = new String[turn.getMoves().size() + 2];
			args[0] = this.getCurrentPlayer().toString();
			args[1] = this.getNextPlayer(1).toString();
			for (int i = 0; i < turn.getMoves().size(); i++) {
				Move m = turn.getMoves().get(i);
				args[i + 2] = m.getTile().toProtocol() + Protocol.Server.Settings.DELIMITER2
								+ m.getPosition().getX() + Protocol.Server.Settings.DELIMITER2
								+ m.getPosition().getY();
			}

			for (ServerPlayer p : this.players) {
				if (p instanceof ServerHuman) {
					((ServerHuman) p).sendMessage(Protocol.Server.MOVE, args);
				}
			}

			this.nextTurn(1);

		} catch (TooFewTilesInBagException | TileNotInBagException | TooManyTilesInBag
						| TileNotInHandException | IllegalTurnException | SquareOutOfBoundsException
						| HandLimitReachedExeption | IllegalMoveException e) {

			Util.log(e);
			this.shutdown("Unrecoverable exception during move application.");

		}

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
	 * 
	 * @ ensures !gameOver() ==> currentPlayer = (\old(currentPlayer) + mod) %
	 * noOfPlayers;
	 */
	public void nextTurn(int mod) {

		if (!this.gameOver()) {

			this.setCurrentPlayer(this.getNextPlayer(mod));
			this.getCurrentPlayer().giveTurn(new Turn(this.board, this.getCurrentPlayer()));

			timeout = new Timer(this.TURNTIMEOUT * 1000, this);
			timeout.start();

			this.gameState = Game.GameState.WAITING;

		} else {
			this.finish();
		}

	}

	/**
	 * Check if the game is over.
	 * 
	 * @return True if any of the win conditions is met. False otherwise.
	 * 
	 *         We could write JML here, but it would mean only to copy the logic
	 *         from the method itself and apply it as follows: @ ensures
	 *         (this.players.size() == 1) ==> \result
	 */
	public boolean gameOver() {

		// There is only one player left.
		if (this.players.size() == 1) {
			return true;
		}

		// The bag is empty, and at least one of the players emptied their
		// hands.
		if (this.bag.getNumberOfTiles() == 0) {
			for (ServerPlayer p : this.players) {
				if (p.getHand().getTilesInHand().size() == 0) {
					p.incrementScore(Game.BONUS_WHEN_FINISH_FIRST);
					return true;
				}
			}
		}

		// There are no players left.
		if (this.players.size() == 0) {
			this.shutdown("We have no players left.");
			return true;
		}

		return false;

	}

	/**
	 * Disqualify a player. Disqualification removes a player from the game,
	 * puts their stones back in the bag and continues normal gameplay.
	 * 
	 * @param player
	 *            The player to be disqualified.
	 * 
	 * @ ensures noOfPlayers = \old(noOfPlayers) - 1;
	 * 
	 * @ ensures bag.getNumberOfTiles() = \old(bag.getNumberOfTiles()) +
	 * \old(player.getHand().getTilesInHand().size());
	 */
	public void disqualify(ServerPlayer player) {
		Util.log("debug", "Disqualifying " + player.getName() + ".");
		if (this.isPlayer(player)) {
			if (this.gameState != Game.GameState.NOTSTARTED) {
				List<Tile> tiles = player.getHand().hardResetHand();
				try {
					this.bag.addToBag(tiles);
				} catch (TooManyTilesInBag e) {
					Util.log(e);
					this.shutdown("Irrecoverable exception during player disqualification.");
				}
			}
			this.removePlayer(player);
			if (player instanceof ServerHuman) {
				((ServerHuman) player).sendMessage(Protocol.Server.GAME_END,
								new String[] { "DISCONNECT", "DISQUALIFIED" });
			}
		}
	}

	/**
	 * When the game is over, finish the game (submitting scores and such).
	 */
	private void finish() {

		int highScore = 0;
		for (ServerPlayer p : this.players) {
			this.parentServer.submitToLeaderboard(p.getName(), p.getScore());
			if (p.getScore() > highScore) {
				highScore = p.getScore();
			}
		}
		List<ServerPlayer> winners = new ArrayList<ServerPlayer>();
		for (ServerPlayer p : this.players) {
			if (p.getScore() == highScore) {
				winners.add(p);
			}
		}

		for (ServerPlayer p : this.players) {
			if (p instanceof ServerHuman) {
				if (winners.size() == 1) {
					((ServerHuman) p).sendMessage(Protocol.Server.GAME_END,
									new String[] { "WIN", winners.get(0).toString() });
				} else if (winners.size() > 1) {
					((ServerHuman) p).sendMessage(Protocol.Server.GAME_END,
									new String[] { "DRAW", winners.size() + "Winners" });
				} else {
					((ServerHuman) p).sendMessage(Protocol.Server.GAME_END,
									new String[] { "DISCONNECT", "FinishedButUncertainEnd" });
				}
			}
		}

		this.cleanUp();

	}

	/**
	 * Exit the game for a specified reason.
	 * 
	 * @param message
	 *            The reason.
	 */
	public void shutdown(String message) {
		for (ServerPlayer p : this.players) {
			if (p instanceof ServerHuman) {
				((ServerHuman) p).sendMessage(Protocol.Server.GAME_END,
								new String[] { "DISCONNECT", message });
			}
		}
		Util.log("error", "Shutdown of game: " + message);
		cleanUp();
	}

	/**
	 * Cleans up the game after finishing or abandoning it.
	 * 
	 * @ ensures players.size() == 0
	 * 
	 * @ ensures (\forall ServerPlayer p; \old(players).contains(p);
	 * parentServer.players.contains(p))
	 */
	private void cleanUp() {
		Util.log("debug", "Cleaning up game.");
		for (ServerPlayer p : this.players) {
			p.getHand().hardResetHand();
		}
		this.playersToLobby();
		this.players.clear();
		this.parentServer.removeGame(this);
	}

	/**
	 * Sends a chat message to all players in this game. Text is already
	 * pre-formatted.
	 * 
	 * @param text
	 *            The message.
	 */
	public void sendChat(String text) {
		for (ServerPlayer p : this.players) {
			if (p instanceof ServerHuman) {
				if (((ServerHuman) p).canChat()) {
					((ServerHuman) p).sendMessage(Protocol.Server.CHAT, new String[] { text });
				}
			}
		}
	}

	/**
	 * Move all players to the lobby.
	 * 
	 * @ ensures players.size() == 0
	 * 
	 * @ ensures (\forall ServerPlayer p; \old(players).contains(p);
	 * parentServer.players.contains(p))
	 */
	public void playersToLobby() {
		ArrayList<ServerPlayer> playersToMove = new ArrayList<ServerPlayer>();
		playersToMove.addAll(this.players);
		for (ServerPlayer p : playersToMove) {
			if (p instanceof ServerHuman) {
				this.parentServer.playerToLobby(((ServerHuman) p));
			}
		}
	}

	/**
	 * Removes a player from the game.
	 * 
	 * @param player
	 *            The player to be removed.
	 * 
	 *            We could write JML for this function, but it would again be a
	 *            duplicate logic of what is already in the method.
	 */
	public void removePlayer(ServerPlayer player) {
		Util.log("debug", "Removing " + player.getName() + " from the game.");
		int curPlayer = currentPlayer;
		int playerNo = this.players.indexOf(player);

		if (playerNo == players.size() - 1) {
			this.currentPlayer = 0;
			players.remove(player);
			this.noOfPlayers--;
			if (playerNo == curPlayer) {
				this.nextTurn(0);
			}
		} else {
			this.currentPlayer = playerNo;
			players.remove(player);
			this.noOfPlayers--;
			if (playerNo == curPlayer) {
				this.nextTurn(0);
			}
		}
	}

	/*
	 * Getters and setters below.
	 */

	/**
	 * Check if the given player is participating in this game.
	 * 
	 * @param player
	 *            The Player.
	 * @return True if the player is participating, false otherwise.
	 */
	public boolean isPlayer(ServerPlayer player) {
		return this.players.contains(player);
	}

	/*
	 * Getters and setters below.
	 */

	/**
	 * Get the current player for this game.
	 */
	public ServerPlayer getCurrentPlayer() {
		return this.players.get(this.currentPlayer);
	}

	/**
	 * @param currentPlayer
	 *            the currentPlayer to set
	 */
	public void setCurrentPlayer(ServerPlayer p) {
		this.currentPlayer = this.players.indexOf(p);
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
	public ServerPlayer getNextPlayer(int mod) {
		return this.players.get((this.currentPlayer + mod) % this.players.size());
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

	/**
	 * Returns the number of tiles in the bag.
	 * 
	 * @return The number of tiles in the bag.
	 */
	public int getTilesInBag() {
		return this.bag.getNumberOfTiles();
	}

	/**
	 * Get the boardSquare object for a specific coordiante.
	 * 
	 * @param x
	 *            The x-coordinate.
	 * @param y
	 *            The y-coordinate.
	 * @return The BoardSquare object.
	 * @throws SquareOutOfBoundsException
	 */
	public BoardSquare getBoardSquare(int x, int y) throws SquareOutOfBoundsException {
		return this.board.getSquare(x, y);
	}

}
