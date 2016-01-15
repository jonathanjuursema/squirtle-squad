package game;

import java.util.ArrayList;
import java.util.List;

import exceptions.IllegalMoveException;
import exceptions.IllegalTurnException;
import exceptions.SquareOutOfBoundsException;

/**
 * The class represents the turn. During a turn the assignPlayer have the choice
 * to do several moves or to make an swapRequest. A move can only be added to
 * the turn if the move is according to the game rules.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
public class Turn {

	private List<Move> moves = new ArrayList<Move>();
	private Board boardCopy;

	private Swap swapRequest = null;
	private Player assignedPlayer;

	private int score;

	/**
	 * Creates a turn with assigned Player and the board. This functions creates
	 * a deepcopy of the board.
	 * 
	 * @param player
	 */

	public Turn(Player player, Board board) {
		this.boardCopy = board.copy();
		this.assignedPlayer = player;
		// TODO: implement body
	}

	/**
	 * Add a move to the turn. If not possible it throws an exception.
	 * 
	 * @param move
	 *            the move that needs to
	 */

	public void addMove(Move move) {
		this.moves.add(move);
		
		System.out.println("---------- PLACE TILE -----------");
		try {
			System.out.println("[debug] Tile " + move.getTile() + " placed on " + move.getPosition().getX() + ", " + move.getPosition().getY());
			this.boardCopy.placeTile(move.getTile(), move.getPosition().getX(), move.getPosition().getY());
			
			BoardSquare editedSquare = this.boardCopy.getSquare(move.getPosition().getX(), move.getPosition().getY());
			System.out.println("[debug] Status after placement: " + editedSquare.toString());
		} catch (SquareOutOfBoundsException e1) {
			// TODO Auto-generated catch block
			System.out.println(" ! [error] Tile " + move.getTile() + " is not placed on " + move.getPosition().getX() + ", " + move.getPosition().getY());
			e1.printStackTrace();
		}
		System.out.println("---------- END PLACE TILE -----------\n");
		
		if(!this.isValidMove(move)){
			System.out.println("[debug] Is not valid move");
		}
	
		// TODO: implement further
	}

	/**
	 * Add a swap request to the turn.
	 * 
	 * @param swap
	 *            object with the tiles that needs to be swapped.
	 */
	public void addSwapRequest(Swap swap) {
		// TODO: implement further
	}

	/**
	 * Private function to check if current turn is possible.
	 * 
	 * @return true if turn is according to the game rules.
	 * @throws SquareOutOfBoundsException
	 * @throws IllegalTurnException
	 * @throws IllegalMoveException
	 */

	private boolean isPossibleTurn() throws IllegalTurnException, IllegalMoveException {

		// Check if an swapRequest has been added
		if (this.swapRequest != null) {
			// If an swap request has been added then no moves can be added
			if (this.getMoves().size() != 0) {
				throw new IllegalTurnException();
			}

			// TODO: check swapRequest
		}

		// If moves has been added
		if (this.getMoves().size() != 0) {
			// Check if swapRequest has been added too
			if (this.swapRequest != null) {
				throw new IllegalTurnException();
			}

			// Loop over the moves
			for (Move m : this.getMoves()) {
				System.out.println("---------------------");
				System.out.println("[start debug] Checking move " + m.toString());
				// Check if every move is according to the game rules
				if (!this.isValidMove(m)) {
					throw new IllegalMoveException(m);
				} else {
					System.out.println("[debug] Not illegal move");
				}
				System.out.println("---------------------\n");
			}
		}

		return true;
	}

	private boolean isValidMove(Move m) {
		// We first create 2 sequences to
		// represent the horizontal line, the row
		// and the vertical line, the column
		Sequence row = new Sequence();
		Sequence column = new Sequence();
		
		// Then we loop over the 4 directions
		// to check the tiles on both 4 directions.
		for (int i = 0; i < 4; i++) {
			System.out.println("[debug] Checking direction " + i);
			
			try {
				// The initial BoardSquare is set here
				BoardSquare currentSquare = this.boardCopy.getSquare(m.getPosition().getX(), m.getPosition().getY());
				System.out.println("[debug] Initial square " + currentSquare.toString());
				
				// The while loop checks if the current move
				// has an neighbour in the current direction.
				System.out.println("[debug] Trying to find neighbours.");
				System.out.println("[debug] Status of neighbour: " + currentSquare.getNeighbour(i).toString());

				while (!currentSquare.getNeighbour(i).isEmpty()) {

					System.out.println("[debug] Neighbour found: \n " + currentSquare.getNeighbour(i).toString());
					// If the neighbour is not empty,
					// the tile will be added to the
					// corresponding row or column.
					if ((i & 1) == 0) {
						row.addTile(currentSquare.getNeighbour(i).getTile());
						System.out.println("[debug] Neighbour added to row");
					} else {
						column.addTile(currentSquare.getNeighbour(i).getTile());
						System.out.println("[debug] Neighbour added to column");
					}
					// Setting the next currentMove
					currentSquare = currentSquare.getNeighbour(i);
					System.out.println("[debug] New current move is assigned");
				}
			} catch (SquareOutOfBoundsException e) {
				// If the neighbour is not existing the
				// next direction will be checked by i++;
				System.out.println(e.getMessage());
			}

			System.out.println("[debug] Direction ended. \n");
		}

		// When all four the directions are checked,
		// both the row and column will be checked
		// if the move is legal.
		if (!row.checkSequence() || !column.checkSequence()) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * The function getScore() calculates the score of the current turn
	 * according to the game rules. First will determined if the moves form a
	 * row or a column. Of each tile the corresponding neighbours (in case of a
	 * row, the columns and viceversa) will be counted.
	 * 
	 * @return The score of the whole turn.
	 */

	public int getScore() {
		// Determine if moves are in same row or column
		List<Integer> directions = new ArrayList<Integer>();
		if (this.getMoves().get(0).getPosition().getX() == this.getMoves().get(1).getPosition().getX()) {
			// If the sequence is a row, then the columns needs to be checked
			directions.add(BoardSquare.NORTH);
			directions.add(BoardSquare.SOUTH);
		} else if (this.getMoves().get(0).getPosition().getY() == this.getMoves().get(1).getPosition().getY()) {
			// If the sequence is a column, then the rows needs to be checked
			directions.add(BoardSquare.EAST);
			directions.add(BoardSquare.WEST);
		} else {
			// Error
		}

		// The score of the moves will be determined by
		// checking the neighbours in opposite directions.
		// In a row, the score of all columns will be counted.
		// In a column, the score of all the rows will be counted.

		// Setting the score to 0;
		int score = 0;

		// Loop over the moves that needs to be placed
		for (Move m : this.getMoves()) {

			// Loop over the directions
			for (Integer oppositeDirection : directions) {

				// Initialize the sequence
				Sequence sequence = new Sequence();

				// Get the initial BoardSquare
				BoardSquare currentSquare = m.getPosition();

				// Add move to sequence
				sequence.addTile(m.getTile());

				try {
					// Loop over the neighbours in opposite directions
					while (!this.getBoardCopy().getSquare(currentSquare.getX(), currentSquare.getY())
							.getNeighbour(oppositeDirection).isEmpty()) {
						// Add the tile to the sequence
						sequence.addTile(this.getBoardCopy().getSquare(currentSquare.getX(), currentSquare.getY())
								.getNeighbour(oppositeDirection).getTile());

						// Set the new currentSquare
						currentSquare = this.getBoardCopy().getSquare(currentSquare.getX(), currentSquare.getY())
								.getNeighbour(oppositeDirection);
					}

				} catch (SquareOutOfBoundsException e) {
					continue;
				}

				// adding the score of the sequence to the total score
				score += sequence.getScore();
			}
		}

		// TODO: Add score of column/row of sequence
		return score;
	}

	/**
	 * Get current moves of the turn.
	 * 
	 * @return
	 */
	public List<Move> getMoves() {
		return moves;
	}

	/**
	 * Get the representation of the board of current turn.
	 * 
	 * @return
	 */
	public Board getBoardCopy() {
		return boardCopy;
	}

	public void setBoardCopy(Board boardCopy) {
		this.boardCopy = boardCopy;
	}

	public String toString() {
		String message = "The turn is for " + this.assignedPlayer.getName() + "\n";
		message += "With approved moves: \n ";
		for (Move m : this.getMoves()) {
			message += m.toString() + "\n";
		}
		return message;
	}

}
