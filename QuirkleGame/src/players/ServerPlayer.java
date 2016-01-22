package players;

import exceptions.NotYourTurnException;
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
	 */
	public void playMoves(String[] moves) throws NotYourTurnException {
		if (!this.game.getCurrentPlayer().equals(this)) {
			throw new NotYourTurnException();
		}
		// TODO Parse the moves according to protocol.
		// TODO Play the moves.
	}

	public void playSwap(String[] tiles) throws NotYourTurnException {
		if (!this.game.getCurrentPlayer().equals(this)) {
			throw new NotYourTurnException();
		}
		// TODO Parse the stones according to protocol.
		// TODO Perform the swap.
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
