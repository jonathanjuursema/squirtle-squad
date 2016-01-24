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

	public ClientPlayer(String name) {
		super(name);
		this.getHand().addObserver(view);
	}

	public abstract void sendError(String string);

	public abstract String askForInput(String type, String message);

	public void setView(View view) {
		this.view = (TUIview) view;
	}

	public View getView() {
		return (TUIview) this.view;
	}

}
