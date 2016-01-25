package players;

import client.Client;
import game.Turn;
import views.TextView;
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
	public TextView view;

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

	public ClientPlayer(Client client, String name) {
		super(name);
		this.client = client;
	}

	/**
	 * Returns the view.
	 * 
	 * @return The view class.
	 */
	public View getView() {
		return this.view;
	}
	
	public abstract void giveTurn();

}
