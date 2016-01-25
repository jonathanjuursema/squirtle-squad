package players;

import client.Client;
import game.Hand;
import game.Turn;
import views.TextView;
import views.View;

public class HumanPlayer extends ClientPlayer {

	public HumanPlayer(String name, Client client) {
		super(client, name);
		Hand hand = new Hand();
		super.assignHand(hand);
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
