package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import exceptions.SquareOutOfBoundsException;

/**
 * TODO Write file header.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
public class Move {
	// TODO: Commenting
	public Tile tileToPlay;
	public BoardSquare position;

	public Move(Tile tile, BoardSquare boardsquare) {
		this.tileToPlay = tile;
		this.position = boardsquare;
	}

	public Tile getTile() {
		return tileToPlay;
	}

	public void setTileToPlay(Tile tileToPlay) {
		this.tileToPlay = tileToPlay;
	}

	public BoardSquare getPosition() {
		return position;
	}

	public void setPosition(BoardSquare position) {
		this.position = position;
	}

	/**
	 * This function will translate the object into a textual representation. It
	 * will print the Tiles contained by the move and the play they will be
	 * placed
	 * 
	 * @return String The message
	 */
	public String toString() {
		return "Move with Tile " + this.tileToPlay.toString() + " on " + this.position.toString();
	}
	
	public boolean isValidMove(Board board) throws SquareOutOfBoundsException {
		return this.isValidMove(board, false);
	}

	public boolean isValidMove(Board board, boolean firstMove) throws SquareOutOfBoundsException {
		// Check move in relation with the board
		
		if(!board.getPossiblePlaces().contains(this.getPosition())) {
			return false;
		}
		
		board.placeTile(this.getTile(), this.getPosition().getX(), this.getPosition().getY());
		
		Move tempMove = new Move(this.getTile(), this.getPosition());
		List<Move> tempList = new ArrayList<Move>();
		tempList.add(tempMove);

		Map<Move, Map<Integer, List<Tile>>> cleanedMap = Turn.getSequencesByMovesAndBoard(board, tempList);

		// System.out.println(this.boardCopy);
		board.removeTile(this.getPosition().getX(), this.getPosition().getY());

		// When all four the directions are checked,
		// both the row and column will be checked
		// if the move is legal.
		
		if (!checkSequence(cleanedMap.get(tempMove).get(0)) || !checkSequence(cleanedMap.get(tempMove).get(1))) {
			return false;
		} else {
			return true;
		}
	}

	public boolean checkSequence(List<Tile> sequence) {
		// If only 1 tile is representing the sequence,
		// the sequence does not have to be checked.
		if (sequence.size() == 1) {
			return true;
		}

		Character color = null;
		Character shape = null;

		if (sequence.size() > 1) {

			if (sequence.get(0).getColor() == sequence.get(1).getColor()) {
				color = sequence.get(0).getColor();
			} else if (sequence.get(0).getShape() == sequence.get(1).getShape()) {
				shape = sequence.get(0).getShape();
			} else {
				return false;
			}

		}
		
		// The lists that are used to check the content of an sequence
		List<Character> shapes = new ArrayList<Character>();
		List<Character> colors = new ArrayList<Character>();

		// Loop over every tile in the sequence
		for (Tile t : sequence) {
			// If the sequence resembles in the same colour
			if (color != null) {
				// check if the same shape already exists.
				if (shapes.contains(t.getShape())) {
					return false;
				} else {
					// If not, add the shape to the list
					shapes.add(t.getShape());
				}
			} else if (shape != null) {
				// check if the same colour already exists.
				if (colors.contains(t.getColor())) {
					return false;
				} else {
					// If not, add the colour to the list
					colors.add(t.getColor());
				}
			} else {
				// If the sequence has no identity
				return false;
			}
		}

		return true;
	}

}
