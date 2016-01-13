package game;

import java.util.List;
import java.util.Random;

import exceptions.TooFewTilesInBagException;

import java.util.ArrayList;

public class Bag {
	
	private Random randomGenerator;
	
	private List<Tile> content;
	private Game game;

	/**
	 * Initializes a new, empty, bag.
	 * @param game The game to which this bag should be assigned.
	 */
	public Bag(Game game) {
		this.game = game;
		this.content = new ArrayList<Tile>();
	}
	/**
	 * Initializes a new bag, and fills it with any number of existing tiles.
	 * @param game The game to which this bag should be assigned.
	 * @param tiles A List<Tile> of tiles. This List will be copied directly into the bag.
	 */
	public Bag(Game game, List<Tile> tiles) {
		this.game = game;
		this.content = tiles;
	}
	
	/**
	 * Fills the bag with a complete set of tiles.
	 * For each color/shape combination, Game.tilesPerType tiles will be added.
	 * See protocol documentation for explaination of the characters.
	 */
	public synchronized void fill() {
		for (char i = Tile.FIRSTCOLOR; i <= Tile.LASTCOLOR; i++) {
			for (char j = Tile.FIRSTSHAPE; j <= Tile.LASTSHAPE; j++) {
				for (char k = 0; k < Game.DEFAULTTILESPERTYPE; k++) {
					this.content.add(new Tile(i, j));
				}
			}
		}
	}
	
	/**
	 * Empties the bag.
	 */
	public void empty() {
		for (Tile t : this.content) {
			this.content.remove(t);
		}
	}
	
	/**
	 * Returns the current number of tiles in a bag.
	 * @return The number of tiles.
	 */
	public int getNumberOfTiles() {
		return this.content.size();
	}
	
	/**
	 * Swaps the given Tiles from a given Hand with random Tiles from the Bag.
	 * @param hand The hand which would like to swap.
	 * @param tiles The tiles that should be put back into the bag.
	 * @return True if the swap succeeded, false otherwise.
	 */
	public synchronized void swapTiles(Hand hand, List<Tile> tiles)
			throws TooFewTilesInBagException {
		if (tiles.size() > this.getNumberOfTiles()) {
			throw new TooFewTilesInBagException(tiles.size(), this.getNumberOfTiles());
		}
		hand.removeFromHand(tiles);
		this.takeFromBag(hand, tiles.size());
		this.addToBag(tiles);
	}
	
	/**
	 * Move amount Tiles from the Bag into the given Hand.
	 * @param hand The Hand to which drawn Tiles should be added.
	 * @param amount The amount of Tiles that should be added.
	 * @throws TooFewTilesInBagException 
	 */
	public synchronized void takeFromBag(Hand hand, int amount) throws TooFewTilesInBagException {
		if (amount > this.getNumberOfTiles()) {
			throw new TooFewTilesInBagException(amount, this.getNumberOfTiles());
		}
		for (int i = 0; i < amount; i++) {
			Tile tile = this.content.get(randomGenerator.nextInt(this.getNumberOfTiles()));
			hand.addToHand(tile);
			this.takeFromBag(tile);
		}
 	}
	
	/**
	 * Adds the given Tile to the Bag.
	 * @param tile The Tile to be added.
	 */
	public synchronized void addToBag(Tile tile) {
		this.content.add(tile);
	}
	/**
	 * Adds the given Tiles to the Bag.
	 * @param tiles The Tiles to be added.
	 */
	public synchronized void addToBag(List<Tile> tiles) {
		for (Tile t : tiles) {
			this.addToBag(t);
		}
	}
	
	/**
	 * Remove the given Tile from the Bag.
	 * @param tile The Tile to be removed.
	 */
	public synchronized void takeFromBag(Tile tile) {
		this.content.remove(tile);
	}
	/**
	 * Remove the given Tiles from the Bag.
	 * @param tiles The Tiles to be removed.
	 */
	public synchronized void takeFromBag(List<Tile> tiles) {
		for (Tile t : tiles) {
			this.takeFromBag(t);
		}
	}
	
	/**
	 * Returns the game this bag is for.
	 * @return The game. You lost it.
	 */
	public Game getGame() {
		return this.game;
	}
	
	/**
	 * Returns a string representation of the bag, showing the amount of tiles left.
	 */
	public String toString() {
		return "Bag containing " + this.getNumberOfTiles() + " tiles.";
	}
	
	
}
