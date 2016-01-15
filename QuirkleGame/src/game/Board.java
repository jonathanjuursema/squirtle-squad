package game;

import exceptions.SquareOutOfBoundsException;

/**
 * A class representing a game board, used by the Game class. The board is represented as
 * a native 2D array of custom objects. We use a fixed array because of the relative small size
 * of the array and to keep things simplified. All public methods of this class working with 
 * coordinates assume board coordinates, allowing for negative integers. Internally, these are
 * converted back to array coordinates starting at 0.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
public class Board {
	
	private Game game;
	private BoardSquare[][] board;
	
	/**
	 * Initializes a game board for a given game.
	 * @param game The game. You lost it.
	 */
	public Board(Game game) {
		this.game = game;
		int arraySize = 2 * this.game.getTilesPerType()
						* Game.DIFFERENTCOLORS * Game.DIFFERENTSHAPES;
		this.board = new BoardSquare[arraySize][arraySize];
		for (int i = 0; i < arraySize; i++) {
			for (int j = 0; j < arraySize; j++) {
				this.board[i][j] = 
						new BoardSquare(this, i - (board.length / 2), j - (board[0].length / 2));
			}
		}
	}
	/**
	 * Initializes a game board for a given game, using a given board.
	 * Useful for copying a game board.
	 * @param game The game. You lost it.
	 * @param board A COPY of another game board.
	 */
	public Board(Game game, BoardSquare[][] board) {
		this(game);
		this.board = board;
	}
	
	/**
	 * Retrieves the BoardSquare on a specified position on the Board.
	 * @param xcoord The x coordinate of the BoardSquare.
	 * @param ycoord The y coordinate of the BoardSquare.
	 * @return The BoardSquare on the specified position.
	 * @throws SquareOutOfBoundsException When a coordinate is out of bounds.
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
	 * @param tile The Tile to be placed.
	 * @param x The x coordinate of the Tile.
	 * @param y The y coordinate of the Tile.
	 * @throws SquareOutOfBoundsException When a coordinate is out of bounds.
	 */
	public void placeTile(Tile tile, int x, int y) throws SquareOutOfBoundsException {
		// TODO Should throw exception when there is already a tile.
		this.getSquare(x, y).placeTile(tile);
	}
	
	/**
	 * Removes a Tile from a specific position on the game board.
	 * @param tile The Tile to be placed.
	 * @param x The x coordinate of the Tile.
	 * @param y The y coordinate of the Tile.
	 * @throws SquareOutOfBoundsException When a coordinate is out of bounds.
	 */
	public void removeTile(int x, int y) throws SquareOutOfBoundsException {
		// TODO Should throw exception when there is no tile.
		this.getSquare(x, y).removeTile();
	}
	
	/**
	 * Retrieves the Tile that is currently on a specific position on the Board.
	 * @param x The x coordinate of the Tile.
	 * @param y The y coordinate of the Tile.
	 * @return The Tile on the specified position.
	 * @throws SquareOutOfBoundsException When a coordinate is out of bounds.
	 */
	public Tile getTile(int x, int y) throws SquareOutOfBoundsException {
		return this.getSquare(x, y).getTile();
	}
	
	/**
	 * Returns a copy of the game board.
	 * @return A copy of the game board.
	 */
	public Board copy() {
		// We make a new 2D-array of board squares of the same size as our current board.
		BoardSquare[][] boardCopy = new BoardSquare[this.board.length][this.board[0].length];
		// We loop over every x-coordinate to copy every column.
		for (int i = 0; i < this.board.length; i++) {
			/*
			 * We cannot just assign, because this would barely duplicate the address.
			 * To truly copy, we use System.arraycopy on every column and assign this copy
			 * to the new column in boardCopy.
			 */
			for (int j = 0; j < this.board[i].length; j++) {
				boardCopy[i][j] = new BoardSquare(this, 
								board[i][j].getX(), board[i][j].getY(), board[i][j].getTile());
			}
		}
		// Finally, we construct a new Board using this copy of the board.
		return new Board(this.game, boardCopy);
	}
	
	/**
	 * Returns the game associated with the board.
	 * @return The Game. You lost it.
	 */
	public Game getGame() {
		return this.game;
	}
	
	

}
