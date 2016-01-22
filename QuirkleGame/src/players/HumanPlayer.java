package players;

import client.Client;
import game.Turn;
import views.TUIview;
import views.View;

public class HumanPlayer extends Player {
	private TUIview view;
	
	public HumanPlayer(String name, Client client) {
		super(name,client);
		this.setView(new TUIview(client));
	}

	/**
	 * @return the view
	 */
	public View getView() {
		return view;
	}

	/**
	 * @param view the view to set
	 */
	public void setView(TUIview view) {
		this.view = view;
	}
	
	public void giveTurn(Turn turn) {
		super.giveTurn(turn);
		view.requestMoves(this.getName() + " it is your turn.");
	}
	
	public void sendError(String string) {
		view.printMessage("error", string);
	}


}
