package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

/**
 * Class for default input/output This class has been imported from the
 * exercises from the course Software Systems at the University of Twente.
 * 
 * @author Arend Rensink en Theo Ruys.
 */
public class Console {

	/** sign for wrong input */
	static public final char FOUT = '\u0004';

	static private BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	static private PrintStream out = System.out;

	private Console() {

	}

	/**
	 * write a text on the default output
	 * 
	 * @param text
	 *            text to be written
	 */
	static synchronized public void print(String text) {
		out.print(text);
	}

	/**
	 * write a text on the default output, ending with a new line
	 * 
	 * @param text
	 *            text to be written
	 */
	static synchronized public void println(String text) {
		out.println(text);
	}

	/**
	 * Reads a line from the default input.
	 * 
	 * @param text
	 *            question
	 * @return read text (never null)
	 */
	static synchronized public String readString(String text) {
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
	static synchronized public int readInt(String text) {
		return readInt(text, "Enter a integer");
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
	static synchronized public int readInt(String text, String errorMessage) {
		do {
			String ans = readString(text);
			try {
				return Integer.parseInt(ans);
			} catch (NumberFormatException e) {
				println(errorMessage);
			}
		} while (true);
	}

}
