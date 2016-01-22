package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import exceptions.SquareOutOfBoundsException;
import server.Game;

/**
 * A class representing a game board, used by the Game class. The board is
 * represented as a native 2D array of custom objects. We use a fixed array
 * because of the relative small size of the array and to keep things
 * simplified. All public methods of this class working with coordinates assume
 * board coordinates, allowing for negative integers. Internally, these are
 * converted back to array coordinates starting at 0.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
public class Board extends Observable {

	private BoardSquare[][] board;

	/**
	 * Initializes a game board for a given game.
	 * 
	 * @param game
	 *            The game. You lost it.
	 */
	public Board() {
		int arraySize = 2 * Game.TILESPERTYPE * Game.DIFFERENTCOLORS * Game.DIFFERENTSHAPES;
		this.board = new BoardSquare[arraySize][arraySize];
		for (int i = 0; i < arraySize; i++) {
			for (int j = 0; j < arraySize; j++) {
				this.board[i][j] = new BoardSquare(this, i - (board.length / 2),
								j - (board[0].length / 2));
			}
		}
	}

	/**
	 * Retrieves the BoardSquare on a specified position on the Board.
	 * 
	 * @param xcoord
	 *            The x coordinate of the BoardSquare.
	 * @param ycoord
	 *            The y coordinate of the BoardSquare.
	 * @return The BoardSquare on the specified position.
	 * @throws SquareOutOfBoundsException
	 *             When a coordinate is out of bounds.
	 */
	public BoardSquare getSquare(int xcoord, int ycoord) throws SquareOutOfBoundsException {
		int x = xcoord + (board.length / 2);
		if (x >= board.length || x < 0) {
			throw new SquareOutOfBoundsException(x, board.length, 'x');
		}
		int y = ycoord + (board[x].length / 2);
		if (y >= board[x].length || y < 0) {
			throw new SquareOutOfBoundsException(y, board[x].length, 'y');
		}
		return this.board[x][y];
	}

	/**
	 * Places a Tile on a specific position on the game board.
	 * 
	 * @param tile
	 *            The Tile to be placed.
	 * @param x
	 *            The x coordinate of the Tile.
	 * @param y
	 *            The y coordinate of the Tile.
	 * @throws SquareOutOfBoundsException
	 *             When a coordinate is out of bounds.
	 */
	public void placeTile(Tile tile, int x, int y) throws SquareOutOfBoundsException {
		// TODO Should throw exception when there is already a tile.
		this.getSquare(x, y).placeTile(tile);

		setChanged();
		notifyObservers("board");
	}

	/**
	 * Removes a Tile from a specific position on the game board.
	 * 
	 * @param tile
	 *            The Tile to be placed.
	 * @param x
	 *            The x coordinate of the Tile.
	 * @param y
	 *            The y coordinate of the Tile.
	 * @throws SquareOutOfBoundsException
	 *             When a coordinate is out of bounds.
	 */
	public void removeTile(int x, int y) throws SquareOutOfBoundsException {
		// TODO Should throw exception when there is no tile.
		this.getSquare(x, y).removeTile();
	}

	/**
	 * Retrieves the Tile that is currently on a specific position on the Board.
	 * 
	 * @param x
	 *            The x coordinate of the Tile.
	 * @param y
	 *            The y coordinate of the Tile.
	 * @return The Tile on the specified position.
	 * @throws SquareOutOfBoundsException
	 *             When a coordinate is out of bounds.
	 */
	public Tile getTile(int x, int y) throws SquareOutOfBoundsException {
		return this.getSquare(x, y).getTile();
	}

	/**
	 * Overwrites the current board with a new board. This is useful after
	 * making copies.
	 * 
	 * @param theBoard
	 *            The new BoardSquare array.
	 */
	public void setBoard(BoardSquare[][] theBoard) {
		this.board = theBoard;
	}

	/**
	 * Returns a copy of the game board.
	 * 
	 * @param theBoard
	 *            The (empty) board to which BoardSquares should be associated.
	 * @return A copy of the game board.
	 */
	public BoardSquare[][] copy(Board theBoard) {
		// We make a new 2D-array of board squares of the same size as our
		// current board.
		BoardSquare[][] boardCopy = new BoardSquare[this.board.length][this.board[0].length];
		// We loop over every x-coordinate to copy every column.
		for (int i = 0; i < this.board.length; i++) {
			/*
			 * We cannot just assign, because this would barely duplicate the
			 * address. To truly copy, we use System.arraycopy on every column
			 * and assign this copy to the new column in boardCopy.
			 */
			for (int j = 0; j < this.board[i].length; j++) {
				boardCopy[i][j] = new BoardSquare(theBoard, this.board[i][j].getX(),
								this.board[i][j].getY(), this.board[i][j].getTile());
			}
		}
		// Finally, we construct a new Board using this copy of the board.
		return boardCopy;
	}

	/**
	 * Returns the minimal and maximal X and Y values that are occupied on the
	 * board.
	 * 
	 * @return An array of integers, where the indexes map the values as
	 *         follows: 0 > smallest X 1 > largest X 2 > smallest Y 3 > largest
	 *         Y
	 */
	public int[] getMinMax() {
		int minmax[] = new int[4];

		for (int i = 0; i < this.board.length; i++) {
			for (int j = 0; j < this.board[i].length; j++) {
				BoardSquare s = this.board[i][j];
				if (!s.isEmpty()) {
					if (s.getX() > minmax[1]) {
						minmax[1] = s.getX();
					} else if (s.getX() < minmax[0]) {
						minmax[0] = s.getX();
					}
					if (s.getY() > minmax[3]) {
						minmax[3] = s.getY();
					} else if (s.getY() < minmax[2]) {
						minmax[2] = s.getY();
					}
				}
			}
		}

		return minmax;
	}

	public List<BoardSquare> getPossiblePlaces() throws SquareOutOfBoundsException {
		List<BoardSquare> retList = new ArrayList<BoardSquare>();
		List<BoardSquare> igList = new ArrayList<BoardSquare>();

		if (this.getSquare(0, 0).isEmpty()) {
			retList.add(this.getSquare(0, 0));
			return retList;
		}

		int[] minmax = this.getMinMax();

		for (int y = (minmax[3] + 1); y >= (minmax[2] - 1); y--) {
			int count = 0;
			for (int x = (minmax[0] - 1); x <= (minmax[1] + 1); x++) {
				BoardSquare current = this.getSquare(x, y);
				if (!current.isEmpty()) {
					count++;
					if (count != 6) {
						if (current.getNeighbour(3).isEmpty()
										&& !retList.contains(current.getNeighbour(3))) {
							retList.add(current.getNeighbour(3));
						}
						if (current.getNeighbour(1).isEmpty()
										&& !retList.contains(current.getNeighbour(1))) {
							retList.add(current.getNeighbour(1));
						}
					} else {
						if (current.getNeighbour(3).isEmpty()
										&& !retList.contains(current.getNeighbour(3))) {
							igList.add(current.getNeighbour(3));
						}
						BoardSquare temp = this.getSquare(current.getX() - count, current.getY());

						if (temp.isEmpty() && !igList.contains(temp)) {
							igList.add(temp);
						}
					}
				} else {
					count = 0;
				}

			}
		}

		for (int x = (minmax[0] - 1); x <= (minmax[1] + 1); x++) {
			int count = 0;
			for (int y = (minmax[3] + 1); y >= (minmax[2] - 1); y--) {

				BoardSquare current = this.getSquare(x, y);
				if (!current.isEmpty()) {
					count++;
					if (count != 6) {
						if (current.getNeighbour(0).isEmpty()
										&& !retList.contains(current.getNeighbour(0))) {
							retList.add(current.getNeighbour(0));
						}
						if (current.getNeighbour(2).isEmpty()
										&& !retList.contains(current.getNeighbour(2))) {
							retList.add(current.getNeighbour(2));
						}
					} else {
						if (current.getNeighbour(0).isEmpty()
										&& !igList.contains(current.getNeighbour(0))) {
							igList.add(current.getNeighbour(0));
						}
						BoardSquare temp = this.getSquare(current.getX(), current.getY() + count);
						if (temp.isEmpty() && !igList.contains(temp)) {
							igList.add(temp);
						}
					}
				} else {
					count = 0;
				}

			}
		}

		retList.removeAll(igList);
		return retList;

	}

	/**
	 * Returns a textual representation of the board.
	 */
	public String toString() {
		int[] minmax = getMinMax();

		String representation = "Displaying board from (" + minmax[0] + "," + minmax[2] + ") to ("
						+ minmax[1] + "," + minmax[3] + ")." + System.lineSeparator()
						+ System.lineSeparator();

		representation = representation.concat("     |");

		String linesep = " ----+";
		for (int x = minmax[0] - 1; x <= minmax[1] + 1; x++) {
			representation = representation.concat(String.format("% 3d ", x) + "|");
			linesep = linesep.concat("----+");
		}

		representation = representation
						.concat(System.lineSeparator() + linesep + System.lineSeparator());

		for (int y = minmax[3] + 1; y >= minmax[2] - 1; y--) {
			representation = representation.concat(" " + String.format("% 3d ", y) + "|");
			for (int x = minmax[0] - 1; x <= minmax[1] + 1; x++) {
				try {
					BoardSquare s = this.getSquare(x, y);
					if (s.isEmpty()) {
						representation = representation.concat("    |");
					} else {
						representation = representation.concat(" " + s.getTile() + " |");
					}
				} catch (SquareOutOfBoundsException e) {
				}
			}
			representation = representation
							.concat(System.lineSeparator() + linesep + System.lineSeparator());
		}

		return representation;
	}

}
