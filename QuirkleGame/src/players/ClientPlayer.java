package players;

import client.Client;
import views.TUIview;
import views.View;

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
		//this.getHand().addObserver(view);
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

	public void setView(View view) {
		this.view = (TUIview) view;
	}

	public View getView() {
		return (TUIview) this.view;
	}


}
