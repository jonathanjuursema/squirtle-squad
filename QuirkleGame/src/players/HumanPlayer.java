package players;

import client.Client;
import views.TextView;
import views.View;

/**
 * This class is a dedicated "human player" in the client. It makes sure the
 * client and the view can interact with each other, as opposed to a computer
 * player who can manage turns all by itsel.
 * 
 * @author Jonathan Juursema & Peter Wessels
 */
public class HumanPlayer extends ClientPlayer {

	public HumanPlayer(String name, Client client) {
		super(client, name);
		this.view = new TextView(client);
	}

	/**
	 * Return the associated view.
	 * 
	 * @return the view
	 */
	public View getView() {
		return view;
	}

	/**
	 * Accept the turn when given by the client.
	 */
	public synchronized void giveTurn() {
		this.client.status = Client.Status.IN_TURN;
		this.client.getView().giveTurn();
	}

}
