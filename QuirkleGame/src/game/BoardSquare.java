package game;

public class BoardSquare {
	
	public static final int NORTH = 0;	
	public static final int EAST = 1;	
	public static final int SOUTH = 2;	
	public static final int WEST = 3;  

	private Tile tile;
	
	private int x;
	private int y;
	
	/**
	 * Construct an empty BoardSquare in a given position.
	 * @param x The x position.
	 * @param y The y position.
	 */
	public BoardSquare(int x, int y) {
		// TODO Implement body.
	}
	
	/**
	 * Constructs an empty BoarDSquare in a given position and place a Tile on it.
	 * @param x The x position.
	 * @param y The y position.
	 * @param tile The Tile.
	 */
	public BoardSquare(int x, int y, Tile tile) {
		// TODO Implement body.
	}
	
	/**
	 * Returns a BoardSquare next to this BoardSquare in the specified direction.
	 * @param direction
	 * @return
	 */
	public BoardSquare getNeighbour(int direction) {
		// TODO Implement body;
		return null;
	}
	
	/**
	 * Returns whether or not there is a Tile placed in this BoardSquare.
	 * @return True when there is a Tile placed, false otherwise.
	 */
	public boolean isEmpty() {
		// TODO Implement body.
		return false;
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
	
}
