package application;

public class Util {

	/**
	 * A general log method. Messages are displayed as follows: [type] message
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
		case "tx":
			if (App.VERBOSE) {
				Util.log("[" + type + "] " + message);
			}
			break;
		case "debug":
			if (App.DEBUG) {
				Util.log("[" + type + "] " + message);
			}
			break;
		default:
			Util.log("[" + type + "] " + message);
			break;
		}
	}

	/**
	 * A shorthand for log if no specific type is desired.
	 * 
	 * @param message
	 */
	public static void log(String message) {
		System.out.println(message);
	}

	/**
	 * Same as log, but can print to a seperate output.
	 * 
	 * @param message
	 *            The message.
	 */
	public static void println(String message) {
		Util.print(message + System.lineSeparator());
	}

	/**
	 * Same as println.
	 * 
	 * @param message
	 *            The message.
	 */
	public static void print(String message) {
		System.out.print(message);
	}

	/**
	 * The specified log function which logs exception message. The message is
	 * consist of the type of exception and the message of the Exception.
	 * 
	 * @param e
	 */

	public static void log(Exception e) {
		Util.log(e.getClass().getSimpleName(), e.getMessage());
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

	public static String joinStringArray(String[] string, char join) {
		String result = "";
		for (String s : string) {
			result = result.concat(s).concat(String.valueOf(join));
		}
		return result.substring(0, result.length() - 1);
	}

}
