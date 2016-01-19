package application;

public class Util {

	/**
	 * A general log method. Policy is to keep logging to a minimal. Only
	 * critical logging should occur. Messages are displayed as follows: [type]
	 * message
	 * 
	 * @param type
	 *            The type of message. The developer is free to choose their own
	 *            type.
	 * @param message
	 *            The message.
	 */
	public static void log(String type, String message) {
		System.out.println("[" + type + "] " + message);
	}

}
