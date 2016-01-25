package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import application.Util;

import java.util.Observable;

import exceptions.IllegalMoveException;
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
	
	public List<BoardSquare> getPossiblePlaceByTile(Tile tile) throws SquareOutOfBoundsException {
		List<BoardSquare> retList = new ArrayList<BoardSquare>();
		for(Entry<BoardSquare, List<Tile>> entry : this.getAllPossiblePlaces().entrySet()) {
			for(Tile t : entry.getValue()) {
				if(t.getColor() == tile.getColor() && t.getShape() == tile.getShape()) {
					retList.add(entry.getKey());
				}
			}
		}
		
		return retList;
	}
	
	public List<BoardSquare> getPossiblePlaceByTile(Tile tile, List<Move> selectedMoves) throws SquareOutOfBoundsException {
		List<BoardSquare> retList = new ArrayList<BoardSquare>();
		for(Entry<BoardSquare, List<Tile>> entry : getPossiblePlacesByMoves(selectedMoves).entrySet()) {
			for(Tile t : entry.getValue()) {
				if(t.getColor() == tile.getColor() && t.getShape() == tile.getShape()) {
					retList.add(entry.getKey());
				}
			}
		}
		
		return retList;
	}

	public Map<BoardSquare, List<Tile>> getAllPossiblePlaces() throws SquareOutOfBoundsException {
		int[] minmax = this.getMinMax();
		return getPossiblePlaces((minmax[0] - 1), (minmax[1] + 1), (minmax[2] - 1), (minmax[3] + 1));
	}
	
	public Map<BoardSquare, List<Tile>> getPossiblePlacesByMoves(List<Move> selectedMoves) throws SquareOutOfBoundsException{
		List<BoardSquare> retList = new ArrayList<BoardSquare>();
		for(Move m : selectedMoves) {
			retList.add(m.getPosition());
		}
		
		return getPossiblePlacesBySquares(retList);
	}

	public Map<BoardSquare, List<Tile>> getPossiblePlacesBySquares(List<BoardSquare> selected)
			throws SquareOutOfBoundsException {
		int maxX = this.board.length * -1;
		int minX = this.board.length;
		int maxY = this.board.length * -1;
		int minY = this.board.length;

		for (BoardSquare b : selected) {
			if (b.getX() > maxX) {
				maxX = b.getX();
			}
			if (b.getX() < minX) {
				minX = b.getX();
			}
			if (b.getY() > maxY) {
				maxY = b.getY();
			}
			if (b.getY() < minY) {
				minY = b.getY();
			}
		}
		return getPossiblePlaces(minX, maxX, minY, maxY);
	}

	public Map<BoardSquare, List<Tile>> getPossiblePlaces(int minx, int maxx, int miny, int maxy)
			throws SquareOutOfBoundsException {
		Map<BoardSquare, List<Tile>> retList = new HashMap<BoardSquare, List<Tile>>();

		if (this.getSquare(0, 0).isEmpty()) {
			List<Tile> allTiles = new ArrayList<Tile>();
			for (char i = Tile.FIRSTCOLOR; i <= Tile.LASTCOLOR; i++) {
				for (char j = Tile.FIRSTSHAPE; j <= Tile.LASTSHAPE; j++) {
					allTiles.add(new Tile(j, i));
				}
			}
			retList.put(this.getSquare(0, 0), allTiles);
			return retList;
		}

		Map<BoardSquare, List<Tile>> possibleTilesX = new HashMap<BoardSquare, List<Tile>>();
		List<BoardSquare> igList = new ArrayList<BoardSquare>();

		for (int y = maxy; y >= miny; y--) {

			for (int x = minx; x <= maxx; x++) {

				BoardSquare current = this.getSquare(x, y);

				if (!current.isEmpty() && !igList.contains(current)) {
					Map<BoardSquare, Tile> sequence = new HashMap<BoardSquare, Tile>();

					BoardSquare lefty = current.getNeighbour(3);
					
					while(!lefty.isEmpty()) {
						sequence.put(lefty, lefty.getTile());
						lefty = lefty.getNeighbour(3);
					}
					BoardSquare emptyLeft = lefty;
					
					sequence.put(current, current.getTile());

					igList.add(current);
					BoardSquare temp = this.getSquare(current.getX(), current.getY());

					while (!temp.isEmpty()) {
						sequence.put(temp, temp.getTile());
						igList.add(temp);

						temp = temp.getNeighbour(1);
					}

					BoardSquare emptyRight = temp;

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

						possibleTilesX.put(emptyLeft, nominatedTiles);
						possibleTilesX.put(emptyRight, nominatedTiles);
					} else if (sequence.size() == 1) {
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
						possibleTilesX.put(emptyLeft, null);
						possibleTilesX.put(emptyRight, null);
					}
				}

			}
		}


		Map<BoardSquare, List<Tile>> possibleTilesY = new HashMap<BoardSquare, List<Tile>>();
		igList.clear();
		
		for (int x = minx; x <= maxx; x++) {
			for (int y = maxy; y >= miny; y--) {

				BoardSquare current = this.getSquare(x, y);

				if (!current.isEmpty() && !igList.contains(current)) {
					Map<BoardSquare, Tile> sequence = new HashMap<BoardSquare, Tile>();

					BoardSquare upty = current.getNeighbour(0);
					
					while(!upty.isEmpty()) {
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

		Map<BoardSquare, List<Tile>> possiblePlaces = new HashMap<BoardSquare, List<Tile>>();

		possiblePlaces.putAll(possibleTilesX);
		possiblePlaces.putAll(possibleTilesY);

		for (Entry<BoardSquare, List<Tile>> entry : possibleTilesX.entrySet()) {

			if (possibleTilesY.containsKey(entry.getKey())) {

				List<Tile> tilesWithSameKey = new ArrayList<Tile>();

				for (Tile tilesInX : entry.getValue()) {
					for (Tile tilesInY : possibleTilesY.get(entry.getKey())) {

						if (tilesInX.getColor() == tilesInY.getColor() && tilesInX.getShape() == tilesInY.getShape()) {
							tilesWithSameKey.add(tilesInY);
						}

					}
				}

				possiblePlaces.put(entry.getKey(), tilesWithSameKey);
			}

		}

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
