package players;

import client.Client;
import game.Hand;
import game.Turn;
import views.TextView;
import views.View;

public class HumanPlayer extends ClientPlayer {

	private Turn turn;

	public HumanPlayer(String name, Client client) {
		super(name);
		super.client = client;
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

	public synchronized void giveTurn(Turn turn) {
		this.turn = this.client.getView().requestMoves(turn);
		if (this.turn != null) {
			this.client.submitTurn(this.turn);
		}
	}

}
