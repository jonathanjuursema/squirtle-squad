package networking;

import java.net.Socket;

/**
 * A dummy ConnectionHandler for testing purposes.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
public class DummyHandler extends ConnectionHandler {

	public DummyHandler() {
		super(new Socket());
	}

	@Override
	public void parse(String command, String[] args) { }

	@Override
	public void shutdown(String reason) { }

}
