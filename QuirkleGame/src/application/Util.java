package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * This class provides verious utilitary functions related to logging mostly.
 * 
 * @author Jonathan Juursema & Peter Wessels.
 *
 */
public class Util {

	/** sign for wrong input */
	static public final char FOUT = '\u0004';

	static private BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

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
	 *            The exception to be logged.
	 */

	public static void log(Exception e) {
		if (App.EXCEPTION) {
			Util.log(e.getClass().getSimpleName(), e.getMessage());
		}
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

	/**
	 * Reads a line from the default input.
	 * 
	 * @param text
	 *            question
	 * @return read text (never null)
	 */
	static public String readString(String text) {
		print(text);
		String antw = null;
		try {
			antw = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (antw == null) {
			return "" + FOUT;
		} else {
			return antw;
		}
	}

	/**
	 * Reads an integer from the default input
	 * 
	 * @param text
	 *            question text
	 * @return entered number
	 */
	static public int readInt(String text) {
		return Util.readInt(text, "Enter an integer");
	}

	/**
	 * Reads an integer from the default input With an invalid input the
	 * question will be asked again
	 * 
	 * @param text
	 *            question
	 * @param errorMessage
	 *            error message for wrong input
	 * @return number
	 */
	static public int readInt(String text, String errorMessage) {
		do {
			String ans = readString(text);
			try {
				return Integer.parseInt(ans);
			} catch (NumberFormatException e) {
				log(errorMessage);
			}
		} while (true);
	}

	/**
	 * Sorts a hashmap by the values and returns the sorted hashmap. Code by:
	 * http://stackoverflow.com/questions/8119366/sorting-hashmap-by-values
	 * 
	 * @param passedMap
	 *            The hashmap to be sorted.
	 * @return The sorted hashmap.
	 */
	public static LinkedHashMap<String, Integer> sortLeaderboard(HashMap<String, Integer> passedMap) {
		List<String> mapKeys = new ArrayList<String>(passedMap.keySet());
		List<Integer> mapValues = new ArrayList<Integer>(passedMap.values());
		Collections.sort(mapValues, Collections.reverseOrder());
		Collections.sort(mapKeys);

		LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();

		Iterator<Integer> valueIt = mapValues.iterator();
		while (valueIt.hasNext()) {
			Object val = valueIt.next();
			Iterator<String> keyIt = mapKeys.iterator();

			while (keyIt.hasNext()) {
				Object key = keyIt.next();
				String comp1 = passedMap.get(key).toString();
				String comp2 = val.toString();

				if (comp1.equals(comp2)) {
					passedMap.remove(key);
					mapKeys.remove(key);
					sortedMap.put((String) key, (Integer) val);
					break;
				}

			}

		}
		return sortedMap;
	}

}
