package players;

import client.Client;
import views.TUIview;
import views.View;

/**
 * The player that is connected to the client. This player can be an computer
 * player of a HumanPlayer, this is not specified here. The ClientPlayer is
 * responsible for pushing the required actions from the client to the player
 * that is linked to this ClientPlayer. 
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */

public abstract class ClientPlayer extends Player {

	public Client client;
	public TUIview view;

	public static enum Status {
		IN_LOBBY, IN_GAME
	};

	/**
	 * The constructor sets the name of the player and also add the view as an
	 * observer of the player his hand.
	 * 
	 * @param name
	 *            The name of the player.
	 */

	public ClientPlayer(String name) {
		super(name);
		// this.getHand().addObserver(view);
	}

	public void applyTurn() {
		this.client.sendTurnToServer();
	}

	/**
	 * Pushes the error to the player
	 * 
	 * @param string
	 *            The error that needs to be pushed to the player.
	 */

	public abstract void sendError(String string);

	/**
	 * This function asks the player for input.
	 * 
	 * @param type
	 *            The type of message you want to ask. For example "chat" if the
	 *            person can chat, or "game" if actions for the game are
	 *            required.
	 * @param message
	 * @return
	 */
	public abstract String askForInput(String type, String message);

	/**
	 * Sets the view to a particular view class.
	 * 
	 * @param view
	 *            The view that will be used by the client.
	 */
	public void setView(View view) {
		this.view = (TUIview) view;
	}

	/**
	 * Returns the view.
	 * 
	 * @return The view class.
	 */
	public View getView() {
		return (TUIview) this.view;
	}

}
