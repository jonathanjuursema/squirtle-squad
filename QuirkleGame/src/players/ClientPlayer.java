package players;

public abstract class ClientPlayer extends Player {

	public ClientPlayer(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	public abstract void sendError(String string);

}
