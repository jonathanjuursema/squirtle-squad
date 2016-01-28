package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
				this.board[i][j] = new BoardSquare(this, i - (board.length / 2), j - (board[0].length / 2));
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
	/*
	 * @ requires xcoord <= board.length || xcoord < 0 && ycoord <= board.length || y < 0 
	 pure */ public BoardSquare getSquare(int xcoord, int ycoord) throws SquareOutOfBoundsException {
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
	/*
	 * @ requires xcoord <= board.length || xcoord < 0 && ycoord <= board.length || y < 0 
	 * pure */ public void placeTile(Tile tile, int x, int y) throws SquareOutOfBoundsException {
		this.getSquare(x, y).placeTile(tile);

		setChanged();
		notifyObservers("placeTile");
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
	/*
	 * @ requires xcoord <= board.length || xcoord < 0 && ycoord <= board.length || y < 0 
	 *   ensures this.getSquare.isEmpty()
	 */
	 public void removeTile(int x, int y) throws SquareOutOfBoundsException {
		this.getSquare(x, y).removeTile();

		setChanged();
		notifyObservers("removeTile");
	 }

	/**
	 * Retrieves the Tile that is currently on a specific position on the Board.
	 * 
	 * @param x
	 *            The x coordinate of the Tile.
	 * 
	 * @param y
	 *            The y coordinate of the Tile.
	 * 
	 * @return The Tile on the specified position.
	 * 
	 * @throws SquareOutOfBoundsException
	 *             When a coordinate is out of bounds.
	 */
	/* @ requires xcoord <= board.length || xcoord < 0 && ycoord <= board.length || y < 0 pure
	 */ public Tile getTile(int x, int y) throws SquareOutOfBoundsException {
		return this.getSquare(x, y).getTile();
	}

	/**
	 * Overwrites the current board with a new board. This is useful after
	 * making copies.
	 * 
	 * @param theBoard
	 *            The new BoardSquare array.
	 */
	//@ requires theBoard.length != 0
	//@ ensures board = theBoard 
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
	//@ requires theBoard.length != 0
	/*@ pure */
	public BoardSquare[][] copy(Board theBoard) {
		// We make a new 2D-array of board squares of the same size as our
		// current board.
		BoardSquare[][] boardCopy = new BoardSquare[this.board.length][this.board[0].length];
		// We loop over every x-coordinate to copy every column.
		for (int i = 0; i < this.board.length; i++) {
			/*
			 * We cannot just assign, because this would barely duplicate the
			 * address. To truly copy, we construct a new BoardSquare with the
			 * same properties.
			 */
			for (int j = 0; j < this.board[i].length; j++) {
				boardCopy[i][j] = new BoardSquare(theBoard, this.board[i][j].getX(), this.board[i][j].getY(),
						this.board[i][j].getTile());
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
	/*@ pure */ public int[] getMinMax() {
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

	/**
	 * This method returns the possible places on which the tile can be placed.
	 * The method uses the method getAllPossiblePlaces to return all the
	 * possible BoardSquares to place an tile.
	 * 
	 * @param tile
	 *            The tile on which the boardsquares can be placed.
	 * @return A list of boardsquares on which the tile can be placed.
	 * @throws SquareOutOfBoundsException
	 */
	public List<BoardSquare> getPossiblePlaceByTile(Tile tile) throws SquareOutOfBoundsException {
		// Creates a new returnList
		List<BoardSquare> retList = new ArrayList<BoardSquare>();

		// Loops over each possible boardsquares
		for (Entry<BoardSquare, List<Tile>> entry : this.getAllPossiblePlaces().entrySet()) {
			// For each boardsquare a list of tiles is retrieved.
			// This loops over every potential tile
			for (Tile t : entry.getValue()) {
				// If the tile is exactly the same as the variable turn
				if (t.getColor() == tile.getColor() && t.getShape() == tile.getShape()) {
					// Then the tile will be added to the return list.
					retList.add(entry.getKey());
				}
			}
		}

		return retList;
	}

	/**
	 * A method that returns all possible boardsquares on which the tile can be
	 * placed. The method determines the boardsquares with the list of moves.
	 * Because a set of moves can only be placed in the same row or column this
	 * is neccesary to validate a move (if the move is not the first move of a
	 * turn).
	 * 
	 * @param tile
	 *            The tile that needs to be placed on the board
	 * @param selectedMoves
	 *            The moves that are allready placed in the current turn.
	 * @return The list of boardsquares on which the tile can be placed.
	 * @throws SquareOutOfBoundsException
	 */

	public List<BoardSquare> getPossiblePlaceByTile(Tile tile, List<Move> selectedMoves)
			throws SquareOutOfBoundsException {

		// If no moves are provided the method uses the method without a list of
		// moves
		if (selectedMoves == null || selectedMoves.size() == 0) {
			return this.getPossiblePlaceByTile(tile);
		}

		// Creates a new return list
		List<BoardSquare> retList = new ArrayList<BoardSquare>();
		// Loops over all boardsquares which can contain a tile according to the
		// selectedMoves
		for (Entry<BoardSquare, List<Tile>> entry : getPossiblePlacesByMoves(selectedMoves).entrySet()) {
			// Loops over every Tile
			for (Tile t : entry.getValue()) {
				// If the tile t is the same as the selected tiles
				if (t.getColor() == tile.getColor() && t.getShape() == tile.getShape()) {
					// The tile will be added.
					retList.add(entry.getKey());
				}
			}
		}

		return retList;
	}

	/**
	 * This function will retrieve all possible boardsquares on which a tile can
	 * be placed. The function returns a map which mapped the boardsquares to a
	 * list of possible tiles.
	 * 
	 * @return A map with a boardsquare with potential tiles.
	 * @throws SquareOutOfBoundsException
	 */
	public Map<BoardSquare, List<Tile>> getAllPossiblePlaces() throws SquareOutOfBoundsException {
		List<Move> moves = new ArrayList<Move>();
		return getPossiblePlaces(moves, false, false);
	}

	/**
	 * This function returns possible places based on previous moves.
	 * 
	 * @param selectedMoves
	 * @return
	 * @throws SquareOutOfBoundsException
	 */
	public Map<BoardSquare, List<Tile>> getPossiblePlacesByMoves(List<Move> selectedMoves)
			throws SquareOutOfBoundsException {
		return getPossiblePlacesBySquares(selectedMoves);
	}

	/**
	 * Retrieve all possible places by the selected Moves
	 * 
	 * @param selected
	 *            The selected moves
	 * @return
	 * @throws SquareOutOfBoundsException
	 */
	public Map<BoardSquare, List<Tile>> getPossiblePlacesBySquares(List<Move> selected)
			throws SquareOutOfBoundsException {
		boolean isRow = false;
		boolean isColumn = false;

		// Determine if the moves form a row or a column. This is neccesary
		// because then no Tiles can be placed either on in the X or Y
		// direction.
		if (selected.size() > 1) {
			if (selected.get(0).getPosition().getX() == selected.get(1).getPosition().getX()) {
				isRow = false;
				isColumn = true;
			}

			if (selected.get(0).getPosition().getY() == selected.get(1).getPosition().getY()) {
				isRow = true;
				isColumn = false;
			}
		}

		return getPossiblePlaces(selected, isRow, isColumn);
	}

	/**
	 * The main method with checks all the boardsquares that are selected and
	 * generates a map with relates an empty boardsquare to possible tiles that
	 * can be place.
	 * 
	 * @param selected
	 *            The selected moves
	 * @param isRow
	 *            If the moves form an row
	 * @param isColumn
	 *            Or if the moves form an column
	 * @return A map with boardsquares related to a list of tiles.
	 * @throws SquareOutOfBoundsException
	 */
	public Map<BoardSquare, List<Tile>> getPossiblePlaces(List<Move> selected, boolean isRow, boolean isColumn)
			throws SquareOutOfBoundsException {

		// Creates a return map
		Map<BoardSquare, List<Tile>> retList = new HashMap<BoardSquare, List<Tile>>();

		// If the 0,0 boardsquare is empty this is the only place to place a
		// tile.
		if (this.getSquare(0, 0).isEmpty()) {

			// All tiles will be added to the potential list of tiles
			List<Tile> allTiles = new ArrayList<Tile>();
			for (char i = Tile.FIRSTCOLOR; i <= Tile.LASTCOLOR; i++) {
				for (char j = Tile.FIRSTSHAPE; j <= Tile.LASTSHAPE; j++) {
					allTiles.add(new Tile(j, i));
				}
			}

			retList.put(this.getSquare(0, 0), allTiles);
			return retList;
		}

		// A list will be created to map the boardsquares to the potential
		// tiles.
		// First all the tiles will be checked in the X direction.
		Map<BoardSquare, List<Tile>> possibleTilesX = new HashMap<BoardSquare, List<Tile>>();

		// An ignore list will be created to add tiles that are already checked.
		List<BoardSquare> igList = new ArrayList<BoardSquare>();

		// If no moves are added the min and max coordinates are retrieved
		int[] minmax = this.getMinMax();

		// The list with all the to be checked coordinates
		List<Integer> yList = new ArrayList<Integer>();
		List<Integer> xList = new ArrayList<Integer>();

		// If no moves are selected, the coordinats of the all boardsquares will
		// be added.
		if (selected == null || selected.size() == 0) {

			for (int y = (minmax[3] + 1); y >= (minmax[2] - 1); y--) {
				yList.add(y);
			}

			for (int x = minmax[0] - 1; x <= (minmax[1] + 1); x++) {
				xList.add(x);
			}
		} else if (selected != null) {
			// If moves are selected the coordinates of the moves will be added.
			for (Move m : selected) {
				if (!yList.contains(m.getPosition().getY())) {
					yList.add(m.getPosition().getY());
				}
				if (!xList.contains(m.getPosition().getX())) {
					xList.add(m.getPosition().getX());
				}
			}

		}

		// If the moves does not form a column or the moves does not form a
		// sequence at all each row will be checked.
		if (!isColumn) {

			for (Integer y : yList) {
				for (Integer x : xList) {

					// Sets the current boardsquare.
					BoardSquare current = this.getSquare(x, y);

					// If the current boardsquare is not empty and is not
					// checked at all
					if (!current.isEmpty() && !igList.contains(current)) {

						// The boardsquares will be maped to the tiles
						Map<BoardSquare, Tile> sequence = new HashMap<BoardSquare, Tile>();

						// The most left boardsquare is retrieved here.
						BoardSquare lefty = current.getNeighbour(3);

						while (!lefty.isEmpty()) {
							sequence.put(lefty, lefty.getTile());
							lefty = lefty.getNeighbour(3);
						}

						BoardSquare emptyLeft = lefty;

						// Put the current boardsquare to the sequence
						sequence.put(current, current.getTile());

						// And to the ignore list.
						igList.add(current);

						// Now the most right tile will be checked.
						BoardSquare temp = this.getSquare(current.getX(), current.getY());

						while (!temp.isEmpty()) {
							sequence.put(temp, temp.getTile());
							igList.add(temp);

							temp = temp.getNeighbour(1);
						}

						BoardSquare emptyRight = temp;

						// A list of nomiatedTiles will be created
						List<Tile> nominatedTiles = new ArrayList<Tile>();

						List<Character> colors = new ArrayList<Character>();
						for (char i = Tile.FIRSTCOLOR; i <= Tile.LASTCOLOR; i++) {
							colors.add(i);
						}

						List<Character> shapes = new ArrayList<Character>();
						for (char j = Tile.FIRSTSHAPE; j <= Tile.LASTSHAPE; j++) {
							shapes.add(j);
						}

						// The sequence will be examened
						int count = 0;
						Tile firstInSequence = null;
						Tile secondInSequence = null;
						boolean isSameColor = false;
						for (Entry<BoardSquare, Tile> entry : sequence.entrySet()) {
							if (count == 0) {
								firstInSequence = entry.getValue();
							}

							if (count == 1) {
								secondInSequence = entry.getValue();
							}
							count++;
						}

						// The identity of the sequence will be determined
						if (count > 1 && (firstInSequence.getColor() == secondInSequence.getColor())) {
							isSameColor = true;
						} else if (count > 1 && (firstInSequence.getShape() == secondInSequence.getShape())) {
							isSameColor = false;
						}

						// If the sequence is really a sequence
						if (sequence.size() > 1 && sequence.size() < 6) {
							// If the identity of the row is the same color
							if (isSameColor) {
								// Then the shapes currently in the sequence
								// will be removed.
								for (Entry<BoardSquare, Tile> entry : sequence.entrySet()) {
									Character shapeToRemove = null;
									for (Character shape : shapes) {
										if (shape == entry.getValue().getShape()) {
											shapeToRemove = shape;
										}
									}

									shapes.remove(shapeToRemove);
								}

								for (Character shape : shapes) {
									nominatedTiles.add(new Tile(firstInSequence.getColor(), shape));
								}

							} else {
								// If the sequence have same shapes the colors
								// will be removed
								for (Entry<BoardSquare, Tile> entry : sequence.entrySet()) {
									Character colorToRemove = null;
									for (Character color : colors) {
										if (color == entry.getValue().getColor()) {
											colorToRemove = color;
										}
									}

									colors.remove(colorToRemove);
								}

								for (Character color : colors) {
									nominatedTiles.add(new Tile(color, firstInSequence.getShape()));
								}
							}

							// Put the possible boardsquare mapped to the
							// nominated tiles into the map
							possibleTilesX.put(emptyLeft, nominatedTiles);
							possibleTilesX.put(emptyRight, nominatedTiles);
						} else if (sequence.size() == 1) {
							// If the sequence is of size 1, all the tiles with
							// the same color with the same shape will be added.
							for (Character color : colors) {
								if (firstInSequence.getColor() != color) {
									nominatedTiles.add(new Tile(color, firstInSequence.getShape()));
								}
							}

							for (Character shape : shapes) {
								if (firstInSequence.getShape() != shape) {
									nominatedTiles.add(new Tile(firstInSequence.getColor(), shape));
								}
							}

							possibleTilesX.put(emptyLeft, nominatedTiles);
							possibleTilesX.put(emptyRight, nominatedTiles);
						} else if (sequence.size() == 6) {
							// If the sequence is 6 tiles in size it is not
							// possible to place any tiles.
							possibleTilesX.put(emptyLeft, null);
							possibleTilesX.put(emptyRight, null);
						}
					}

				}
			}
		}

		// The same goes for the tiles in Y direction.
		Map<BoardSquare, List<Tile>> possibleTilesY = new HashMap<BoardSquare, List<Tile>>();
		igList.clear();

		if (!isRow) {

			for (Integer x : xList) {
				for (Integer y : yList) {

					BoardSquare current = this.getSquare(x, y);

					if (!current.isEmpty() && !igList.contains(current)) {

						Map<BoardSquare, Tile> sequence = new HashMap<BoardSquare, Tile>();

						BoardSquare upty = current.getNeighbour(0);

						while (!upty.isEmpty()) {
							sequence.put(upty, upty.getTile());
							upty = upty.getNeighbour(0);
						}
						BoardSquare emptyUp = upty;

						igList.add(current);
						BoardSquare temp = this.getSquare(current.getX(), current.getY());

						while (!temp.isEmpty()) {
							sequence.put(temp, temp.getTile());
							igList.add(temp);

							temp = temp.getNeighbour(2);
						}

						BoardSquare emptyDown = temp;

						List<Tile> nominatedTiles = new ArrayList<Tile>();

						List<Character> colors = new ArrayList<Character>();
						for (char i = Tile.FIRSTCOLOR; i <= Tile.LASTCOLOR; i++) {
							colors.add(i);
						}

						List<Character> shapes = new ArrayList<Character>();
						for (char j = Tile.FIRSTSHAPE; j <= Tile.LASTSHAPE; j++) {
							shapes.add(j);
						}

						int count = 0;
						Tile firstInSequence = null;
						Tile secondInSequence = null;
						boolean isSameColor = false;
						for (Entry<BoardSquare, Tile> entry : sequence.entrySet()) {
							if (count == 0) {
								firstInSequence = entry.getValue();
							}

							if (count == 1) {
								secondInSequence = entry.getValue();
							}
							count++;
						}

						if (count > 1 && (firstInSequence.getColor() == secondInSequence.getColor())) {
							isSameColor = true;
						} else if (count > 1 && (firstInSequence.getShape() == secondInSequence.getShape())) {
							isSameColor = false;
						}

						if (sequence.size() > 1 && sequence.size() < 6) {
							if (isSameColor) {
								for (Entry<BoardSquare, Tile> entry : sequence.entrySet()) {
									Character shapeToRemove = null;
									for (Character shape : shapes) {
										if (shape == entry.getValue().getShape()) {
											shapeToRemove = shape;
										}
									}

									shapes.remove(shapeToRemove);
								}

								for (Character shape : shapes) {
									nominatedTiles.add(new Tile(firstInSequence.getColor(), shape));
								}

							} else {
								for (Entry<BoardSquare, Tile> entry : sequence.entrySet()) {
									Character colorToRemove = null;
									for (Character color : colors) {
										if (color == entry.getValue().getColor()) {
											colorToRemove = color;
										}
									}

									colors.remove(colorToRemove);
								}

								for (Character color : colors) {
									nominatedTiles.add(new Tile(color, firstInSequence.getShape()));
								}
							}

							possibleTilesY.put(emptyUp, nominatedTiles);
							possibleTilesY.put(emptyDown, nominatedTiles);
						} else if (sequence.size() == 1) {

							for (Character color : colors) {
								if (color != firstInSequence.getColor()) {
									nominatedTiles.add(new Tile(color, firstInSequence.getShape()));
								}
							}

							for (Character shape : shapes) {
								if (shape != firstInSequence.getShape()) {
									nominatedTiles.add(new Tile(firstInSequence.getColor(), shape));
								}
							}
							possibleTilesY.put(emptyUp, nominatedTiles);
							possibleTilesY.put(emptyDown, nominatedTiles);
						} else if (sequence.size() == 6) {
							possibleTilesY.put(emptyUp, null);
							possibleTilesY.put(emptyDown, null);
						}
					}

				}
			}
		}

		// Creating a map for all possible places.
		Map<BoardSquare, List<Tile>> possiblePlaces = new HashMap<BoardSquare, List<Tile>>();

		// Put the boardsquares found in the array
		possiblePlaces.putAll(possibleTilesX);
		possiblePlaces.putAll(possibleTilesY);

		// Here all possible tiles will be checked if both boarsquares got
		// duplicated entries. If so, only the tiles that are contained in the
		// both lists will stay in the array.
		for (Entry<BoardSquare, List<Tile>> entry : possibleTilesX.entrySet()) {

			if (possibleTilesY.containsKey(entry.getKey())) {

				List<Tile> tilesWithSameKey = new ArrayList<Tile>();

				if (possibleTilesY.get(entry.getKey()) != null && entry.getValue() != null) {
					for (Tile tilesInX : entry.getValue()) {
						for (Tile tilesInY : possibleTilesY.get(entry.getKey())) {

							if (tilesInX.getColor() == tilesInY.getColor()
									&& tilesInX.getShape() == tilesInY.getShape()) {
								tilesWithSameKey.add(tilesInY);
							}

						}
					}
				}
				possiblePlaces.put(entry.getKey(), tilesWithSameKey);
			}

		}

		// All boardsquares with no possible tiles will be removed.
		List<BoardSquare> removeNull = new ArrayList<BoardSquare>();
		for (Entry<BoardSquare, List<Tile>> entry : possiblePlaces.entrySet()) {
			if (entry.getValue() == null) {
				removeNull.add(entry.getKey());
			}
		}

		for (BoardSquare b : removeNull) {
			possiblePlaces.remove(b);
		}

		return possiblePlaces;

	}

	/**
	 * Returns a textual representation of the board.
	 */
	public String toString() {
		int[] minmax = getMinMax();

		String representation = "Displaying board from (" + minmax[0] + "," + minmax[2] + ") to (" + minmax[1] + ","
				+ minmax[3] + ")." + System.lineSeparator() + System.lineSeparator();

		representation = representation.concat("     |");

		String linesep = " ----+";
		for (int x = minmax[0] - 1; x <= minmax[1] + 1; x++) {
			representation = representation.concat(String.format("% 3d ", x) + "|");
			linesep = linesep.concat("----+");
		}

		representation = representation.concat(System.lineSeparator() + linesep + System.lineSeparator());

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
			representation = representation.concat(System.lineSeparator() + linesep + System.lineSeparator());
		}

		return representation;
	}

}
