package players;

import java.util.ArrayList;
import java.util.List;

import exceptions.IllegalMoveException;
import exceptions.IllegalTurnException;
import exceptions.NotInGameException;
import exceptions.NotYourTurnException;
import exceptions.SquareOutOfBoundsException;
import game.Move;
import game.Tile;
import protocol.Protocol;
import server.Game;
import server.ServerConnectionHandler;

public class ServerPlayer extends Player {

	private Game game;
	private ServerConnectionHandler connection;

	/**
	 * Instantiate the new server player.
	 * 
	 * @param name
	 *            The player name.
	 */
	public ServerPlayer(String name) {
		super(name);
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

		if (!this.game.getCurrentPlayer().equals(this)) {
			throw new NotYourTurnException();
		}

		List<Tile> handCopy = new ArrayList<Tile>();
		handCopy.addAll(this.getHand().getTilesInHand());

		for (String move : moves) {
			String[] args = move.split(String.valueOf(Protocol.Server.Settings.DELIMITER2));

			boolean moveValid = false;

			for (Tile t : this.getHand().getTilesInHand()) {

				if (t.toProtocol().equals(args[0]) && handCopy.contains(t)) {

					handCopy.remove(t);

					this.getTurn().addMove(new Move(t, this.game.getBoardSquare(
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

		this.game.receiveTurn(this.getTurn());

	}

	public void playSwap(String[] tiles)
					throws NotYourTurnException, NotInGameException, IllegalTurnException {

		this.getTurn().getMoves().clear();
		this.getTurn().getSwap().clear();

		if (this.game == null || !this.game.isPlayer(this)) {
			throw new NotInGameException();
		}
		if (!this.game.getCurrentPlayer().equals(this)) {
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
				this.sendMessage(Protocol.Server.ERROR, new String[] { "2", "StoneNotInHand" });
				return;
			}

		}

		this.game.receiveTurn(this.getTurn());
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

	public void setCanInvite(boolean b) {
		// TODO Auto-generated method stub

	}

	public void setCanChat(boolean b) {
		// TODO Auto-generated method stub

	}

	public void setCanLeaderBoard(boolean b) {
		// TODO Auto-generated method stub

	}

}
