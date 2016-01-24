package exceptions;

import java.util.ArrayList;
import java.util.List;

import game.Move;

/**
 * This exception is thrown when a the player wants to play a move that is not
 * according to the game rules.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */

@SuppressWarnings("serial")
public class IllegalMoveException extends QwirkleException {
	private List<Move> moves = new ArrayList<Move>();
	private String customMessage;

	/**
	 * Constructor which creates a new Exception.
	 * 
	 * @param move
	 *            The move object that caused the exception.
	 * @param customMessage
	 *            To specify the exact problem.
	 */
	public IllegalMoveException(Move move, String customMessage) {
		this.moves.add(move);
		this.customMessage = customMessage;
	}

	/**
	 * Creates a new Exception.
	 * 
	 * @param moves
	 *            The list with moves.
	 */
	public IllegalMoveException(List<Move> moves) {
		this.moves = moves;
	}

	/**
	 * Constructor which creates a new Exception.
	 * 
	 * @param move
	 *            The move object that caused the exception.
	 */
	public IllegalMoveException(Move move) {
		this.moves.add(move);
	}

	/**
	 * Creates a new Exception.
	 * 
	 * @param moves
	 *            The list with moves.
	 * @param customMessage
	 *            To specify the exact problem.
	 */
	public IllegalMoveException(List<Move> moves, String customMessage) {
		this.moves = moves;
		this.customMessage = customMessage;
	}

	/**
	 * Overrides the getMessage function of Exception. The message contains
	 * information about the move with all the tiles that are contained by the
	 * move. Because this.moves is a list, the toString() function of each of
	 * the objects is called to get a representation of such an object.
	 * 
	 * @return String The complete error message.
	 */
	public String getMessage() {
		String message = "The move " + this.moves.toString() + " cannot be placed on the board. \n";
		return message + customMessage;
	}
}