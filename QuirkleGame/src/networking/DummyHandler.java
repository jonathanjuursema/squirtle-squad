package networking;

/**
 * A dummy ConnectionHandler for testing purposes.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
public class DummyHandler extends ConnectionHandler {

	public DummyHandler() {
		super(null);
	}

	@Override
	public void parse(String command, String[] args) { }

	@Override
	public void shutdown(String reason) { }

}
