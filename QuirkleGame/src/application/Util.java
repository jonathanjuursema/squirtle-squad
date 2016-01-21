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
		switch (type) {
		case "rx":
			//Console.println("[" + type + "] " + message);
			break;
		case "tx":
			//Console.println("[" + type + "] " + message);
			break;
		default:
			Console.println("[" + type + "] " + message);
			break;
		}
	}
	/**
	 * The specified log function which logs exception message. The 
	 * message is consist of the type of exception and the message 
	 * of the Exception.
	 * 
	 * @param e
	 */
	
	public static void log(Exception e) {
		Console.println("["+ e.getClass().getSimpleName() + "] " + e.getMessage());
	}

	/**
	 * A command that (tries to) clear the terminal.
	 */
	public static void clearScreen() {
		final String ANSI_CLS = "\u001b[2J";
		final String ANSI_HOME = "\u001b[H";
		System.out.print(ANSI_CLS + ANSI_HOME);
		System.out.flush();
	}

}
