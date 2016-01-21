package players;

import game.Turn;
import server.Player;
import views.View;

public class HumanPlayer extends Player {
	private View view;
	
	public HumanPlayer(String name, View view) {
		super(name);
		this.view = view;
	}
	
	public void giveTurn(Turn turn) {
		this.turn = turn;
	}


}
