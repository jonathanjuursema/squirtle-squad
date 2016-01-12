package game;

import java.util.List;
import java.util.ArrayList;

public class Bag {
	
	private List<Tile> content;
	private Game game;

	/**
	 * Initializes a new, empty, bag.
	 * @param game The game to which this bag should be assigned.
	 */
	public Bag(Game game) {
		// TODO Implement body.
	}
	/**
	 * Initializes a new bag, and fills it with any number of existing tiles.
	 * @param game The game to which this bag should be assigned.
	 * @param tiles A List<Tile> of tiles. This List will be copied directly into the bag.
	 */
	public Bag(Game game, List<Tile> tiles) {
		// TODO Implement body.
	}
	
	/**
	 * Fills the bag with a complete set of tiles.
	 * For each color/shape combination, Game.tilesPerType tiles will be added.
	 */
	public void fill() {
		// TODO Implement body.
	}
	
	/**
	 * Empties the bag.
	 */
	public void empty() {
		// TODO Implement body;
	}
	
	/**
	 * Returns the current number of tiles in a bag.
	 * @return The number of tiles.
	 */
	public int getNumberOfTiles() {
		// TODO Implement body.
		return 0;
	}
	
	/**
	 * Swaps the given Tiles from a given Hand with random Tiles from the Bag.
	 * @param hand The hand which would like to swap.
	 * @param tiles The tiles that should be put back into the bag.
	 * @return True if the swap succeeded, false otherwise.
	 */
	public void swapTiles(Hand hand, List<Tile> tiles) {
		// TODO Implement body.
	}
	
	/**
	 * Move amount Tiles from the Bag into the given Hand.
	 * @param hand The Hand to which drawn Tiles should be added.
	 * @param amount The amount of Tiles that should be added.
	 */
	public void takeFromBag(Hand hand, int amount) {
		// TODO Implement body;
	}
	
	/**
	 * Adds the given Tile to the Bag.
	 * @param tile The Tile to be added.
	 */
	public void addToBag(Tile tile) {
		// TODO Implement body.
	}
	/**
	 * Adds the given Tiles to the Bag.
	 * @param tiles The Tiles to be added.
	 */
	public void addToBag(List<Tile> tiles) {
		// TODO Implement body.
	}
	
	/**
	 * Remove the given Tile from the Bag.
	 * @param tile The Tile to be removed.
	 */
	public void takeFromBag(Tile tile) {
		// TODO Implement body.
	}
	/**
	 * Remove the given Tiles from the Bag.
	 * @param tiles The Tiles to be removed.
	 */
	public void takeFromBag(List<Tile> tiles) {
		// TODO Implement body.
	}
	
	
}
