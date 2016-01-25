package players;

import java.util.ArrayList;
import java.util.List;

import exceptions.IllegalMoveException;
import exceptions.IllegalTurnException;
import exceptions.NotInGameException;
import exceptions.NotYourTurnException;
import exceptions.SquareOutOfBoundsException;
import exceptions.TileNotInHandException;
import game.Move;
import game.Tile;
import protocol.Protocol;
import server.Game;
import server.ServerConnectionHandler;

public class ServerPlayer extends Player {

	private boolean canInvite;
	private boolean chanChat;
	private boolean canLeaderBoard;

	private Game game;
	private ServerConnectionHandler connection;

	/**
	 * Instantiate the new server player.
	 * 
	 * @param name
	 *            The player name.
	 */
	public ServerPlayer(String name, ServerConnectionHandler server) {
		super(name);
		this.connection = server;
	}

	/**
	 * Sends a message to the connection associated with the player.
	 * 
	 * @param cmd
	 *            The command to be send.
	 * @param args
	 *            The arguments to this command.
	 */
	public void sendMessage(String cmd, String[] args) {
		this.connection.send(cmd, args);
	}

	/**
	 * Submit the turn to the game.
	 * 
	 * @param turn
	 *            the turn that needs passed through the game.
	 * @throws NotYourTurnException
	 * @throws NotInGameException
	 * @throws IllegalTurnException
	 * @throws IllegalMoveException
	 * @throws SquareOutOfBoundsException
	 * @throws NumberFormatException
	 */
	public synchronized void placeMove(String[] moves)
					throws NotYourTurnException, NotInGameException, NumberFormatException,
					SquareOutOfBoundsException, IllegalMoveException, IllegalTurnException {

		this.getTurn().getMoves().clear();
		this.getTurn().getSwap().clear();

		if (this.game == null || !this.game.isPlayer(this)) {
			throw new NotInGameException();
		}

		if (!this.game.getCurrentPlayer().equals(this)
						&& this.game.getGameState() != Game.GameState.INITIAL) {
			throw new NotYourTurnException();
		}

		List<Tile> handCopy = new ArrayList<Tile>();
		handCopy.addAll(this.getHand().getTilesInHand());

		for (String move : moves) {
			// We need to 'escape' the DELIMITER2 because of RegexReasons.
			String[] args = move.split("\\" + String.valueOf(Protocol.Server.Settings.DELIMITER2));

			boolean moveValid = false;

			for (Tile t : this.getHand().getTilesInHand()) {

				if (t.toProtocol().equals(args[0]) && handCopy.contains(t)) {

					handCopy.remove(t);

					this.getTurn().addMove(new Move(t, this.getTurn().getBoardCopy().getSquare(
									Integer.parseInt(args[1]), Integer.parseInt(args[2]))));
					moveValid = true;

					break;

				}

			}

			if (!moveValid) {
				this.getTurn().getMoves().clear();
				this.sendMessage(Protocol.Server.ERROR, new String[] { "2", "StoneNotInHand" });
				return;
			}

		}

		if (this.game.getGameState() == Game.GameState.INITIAL) {
			this.game.receiveInitialMove(this.getTurn(), this);
		} else {
			this.game.receiveTurn(this.getTurn());
		}

	}

	public void playSwap(String[] tiles)
					throws NotYourTurnException, NotInGameException, IllegalTurnException, TileNotInHandException {

		this.getTurn().getMoves().clear();
		this.getTurn().getSwap().clear();

		if (this.game == null || !this.game.isPlayer(this)) {
			throw new NotInGameException();
		}
		if (!this.game.getCurrentPlayer().equals(this)
						&& this.game.getGameState() != Game.GameState.INITIAL) {
			throw new NotYourTurnException();
		}

		List<Tile> handCopy = new ArrayList<Tile>();
		handCopy.addAll(this.getHand().getTilesInHand());
		List<Tile> toSwap = new ArrayList<Tile>();

		for (String tile : tiles) {

			boolean tileValid = false;

			for (Tile t : this.getHand().getTilesInHand()) {
				if (t.toProtocol().equals(tile) && handCopy.contains(t)) {
					handCopy.remove(t);
					toSwap.add(t);
					this.getTurn().addSwapRequest(t);
					tileValid = true;
					break;
				}
			}

			if (!tileValid) {
				this.getTurn().getMoves().clear();
				throw new TileNotInHandException(tile, this.getHand());
			}

		}

		this.game.receiveTurn(this.getTurn());
	}

	/**
	 * Sends an invite to challenge to this player.
	 * 
	 * @param challenger
	 *            The challenger player.
	 */
	public void invite(ServerPlayer challenger) {
		this.sendMessage(Protocol.Server.INVITE, new String[] { challenger.getName() });
	}

	/**
	 * Tells the player their invite is declined.
	 */
	public void decline() {
		this.sendMessage(Protocol.Server.DECLINEINVITE, new String[] {});
	}

	/**
	 * Return the ServerPlayer's game.
	 * 
	 * @return The game. You lost it.
	 */
	public Game getGame() {
		return this.game;
	}

	/**
	 * Set the game of the ServerPlayer.
	 * 
	 * @param game
	 *            The game. You lost it.
	 */
	public void setGame(Game game) {
		this.game = game;
	}

	/**
	 * @return the canInvite
	 */
	public boolean canInvite() {
		return canInvite;
	}

	/**
	 * @param canInvite
	 *            the canInvite to set
	 */
	public void canInvite(boolean canInvite) {
		this.canInvite = canInvite;
	}

	/**
	 * @return the chanChat
	 */
	public boolean canChat() {
		return chanChat;
	}

	/**
	 * @param chanChat
	 *            the chanChat to set
	 */
	public void canChat(boolean chanChat) {
		this.chanChat = chanChat;
	}

	/**
	 * @return the canLeaderBoard
	 */
	public boolean canLeaderBoard() {
		return canLeaderBoard;
	}

	/**
	 * @param canLeaderBoard
	 *            the canLeaderBoard to set
	 */
	public void canLeaderBoard(boolean canLeaderBoard) {
		this.canLeaderBoard = canLeaderBoard;
	}

	public void addToHand(List<Tile> tiles) {
		this.sendMessage(Protocol.Server.ADDTOHAND, Tile.toArgs(tiles));
	}

}
