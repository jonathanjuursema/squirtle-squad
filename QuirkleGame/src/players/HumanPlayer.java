package players;

import client.Client;
import views.TextView;
import views.View;

public class HumanPlayer extends ClientPlayer {

	public HumanPlayer(String name, Client client) {
		super(client, name);
		this.view = new TextView(client);
	}

	/**
	 * @return the view
	 */
	public View getView() {
		return view;
	}

	public synchronized void giveTurn() {
		this.client.status = Client.Status.IN_TURN;
		this.client.getView().giveTurn();
	}

}
