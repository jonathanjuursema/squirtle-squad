package game;

import exceptions.SquareOutOfBoundsException;

/**
 * This class represents an layer between boards and tiles placed on these boards. It offers
 * some extra functionality related to neighbours and more structured control of tiles placed
 * on them.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
public class BoardSquare {
	
	public static final int NORTH = 0;	
	public static final int EAST = 1;	
	public static final int SOUTH = 2;	
	public static final int WEST = 3;  

	private Tile tile;
	private Board board;
	
	private int x;
	private int y;
	
	/**
	 * Construct an empty BoardSquare in a given position.
	 * @param board The game board.
	 * @param x The x position.
	 * @param y The y position.
	 */
	public BoardSquare(Board board, int x, int y) {
		this(board, x, y, null);
	}
	
	/**
	 * Constructs an empty BoarDSquare in a given position and place a Tile on it.
	 * @param board The game board.
	 * @param x The x position.
	 * @param y The y position.
	 * @param tile The Tile.
	 */
	public BoardSquare(Board board, int x, int y, Tile tile) {
		this.x = x;
		this.y = y;
		this.tile = tile;
		this.board = board;
	}
	
	/**
	 * Places a Tile on this BoardSquare.
	 * @param tile The Tile to be placed.
	 */
	public void placeTile(Tile theTile) {
		this.tile = theTile;
	}
	
	/**
	 * Removes the tile from this BoardSquare.
	 */
	public void removeTile() {
		this.tile = null;
	}
	
	/**
	 * Returns a BoardSquare next to this BoardSquare in the specified direction.
	 * @param A direction. See the public static integers of this class.
	 * @return The BoardSquare next to the current BoardSquare in the given direction.
	 * @throws SquareOutOfBoundsException When a coordinate is out of range. 
	 * This should never happen though.
	 */
	public BoardSquare getNeighbour(int direction) throws SquareOutOfBoundsException {
		switch (direction) {
			case BoardSquare.NORTH:
				return this.board.getSquare(this.x, this.y + 1);
			case BoardSquare.EAST:
				return this.board.getSquare(this.x + 1, this.y);
			case BoardSquare.SOUTH:
				return this.board.getSquare(this.x, this.y - 1);
			case BoardSquare.WEST:
				return this.board.getSquare(this.x - 1, this.y);
			default:
				return null;
		}
	}
	
	/**
	 * Returns whether or not there is a Tile placed in this BoardSquare.
	 * @return True when there is a Tile placed, false otherwise.
	 */
	public boolean isEmpty() {
		return this.tile == null;
	}
	
	/**
	 * Returns the tile on this BoardSquare.
	 * @return The Tile on this BoardSquare.
	 */
	public Tile getTile() {
		return this.tile;
	}

	/**
	 * Returns this BoardSquare's x position.
	 * @return The x position.
	 */
	public int getX() {
		return this.x;
	}
	/**
	 * Returns this BoardSquare's y position.
	 * @return The y position.
	 */
 	public int getY() {
		return this.y;
	}
 	
 	public String toString() {
 		if (this.tile != null) {
 			return "BoardSquare on position X:" + this.x + " Y:" + this.y + " "
 					+ "with tile " + this.tile.toString() + ".";
 		} else {

 	 		return "Empty BoardSquare on position X:" + this.x + " Y:" + this.y + ".";
 		}
 	}
}
