package game;

import java.util.List;

public class Board {
	
	private Game game;
	private List<List<BoardSquare>> board;
	
	/**
	 * Initializes a game board for a given game.
	 * @param game The game. You lost it.
	 */
	public Board(Game game) {
		this.game = game;
		// TODO Implement constructor.
	}
	
	/**
	 * Places a Tile on a specific BoardSquare of the game board.
	 * @param tile The Tile to be placed.
	 * @param x The x coordinate of the BoardSquare.
	 * @param y The y coordinate of the BoardSquare.
	 */
	public void placeTile(Tile tile, int x, int y) {
		// TODO Implement body.
	}
	
	/**
	 * Removes a Tile from a specific BoardSquare of the game board.
	 * @param tile The Tile to be placed.
	 * @param x The x coordinate of the BoardSquare.
	 * @param y The y coordinate of the BoardSquare.
	 */
	public void removeTile(int x, int y) {
		// TODO Implement body.
	}
	
	/**
	 * Retrieves the Tile that is currently on a specific position on the Board.
	 * @param x The x coordinate of the BoardSquare.
	 * @param y The y coordinate of the BoardSquare.
	 * @return The Tile on the specified position.
	 */
	public Tile getTile(int x, int y) {
		// TODO Implement body;
		return null;
	}
	
	/**
	 * Returns a copy of the board.
	 * @return A copy of the board.
	 */
	public Board copy() {
		// TODO Implement body;
		return null;
	}
	
	/**
	 * Returns the game associated with the board.
	 * @return The Game. You lost it.
	 */
	public Game getGame() {
		return this.game;
	}
	
	

}
