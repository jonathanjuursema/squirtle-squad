package players;

import java.util.Observer;

import client.Client;
import game.Turn;
import views.TUIview;
import views.View;

public class HumanPlayer extends ClientPlayer {

	/*
	 * Initialize the human player
	 */

	public HumanPlayer(String name, Client client) {
		super(name);
		super.client = client;
		this.setView(new TUIview(client));
	}

	/**
	 * @return the view
	 */
	public View getView() {
		return view;
	}

	/**
	 * @param view
	 *            the view to set
	 */
	public void setView(View view) {
		this.view = (TUIview) view;
	}

	public void giveTurn(Turn turn) {
		super.giveTurn(turn);
		view.requestMoves(this.getName() + " it is your turn.");
		this.getTurn().addObserver((Observer) turn);
	}

	public void sendError(String string) {
		view.printMessage("error", string);
	}

	@Override
	public String askForInput(String type, String message) {
		return view.askForInput(type, message);
	}

}
