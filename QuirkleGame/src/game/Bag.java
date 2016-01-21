package game;

import java.util.List;
import java.util.Random;

import application.Util;
import exceptions.HandLimitReachedExeption;
import exceptions.TileNotInBagException;
import exceptions.TileNotInHandException;
import exceptions.TooFewTilesInBagException;
import exceptions.TooManyTilesInBag;
import server.Game;

import java.util.ArrayList;

/**
 * A Bag represents a bag of tiles in the game, with several methods to put
 * stuff in and take stuff out of the bag. Internally, tiles are not place
 * randomly in an array, but rather added at the end all the time. Only when
 * invoking the swapTiles and takeFromBag methods involving hand mutations query
 * random tiles from the bag, to simulate randomly drawing from a bag in real
 * life.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
public class Bag {

	private Random randomGenerator;

	private List<Tile> content;

	/**
	 * Initializes a new, empty, bag.
	 * 
	 * @param game
	 *            The game to which this bag should be assigned.
	 */
	public Bag() {
		this.content = new ArrayList<Tile>();
		randomGenerator = new Random();
	}

	/**
	 * Fills the bag with a complete set of tiles. For each color/shape
	 * combination, Game.tilesPerType tiles will be added. See protocol
	 * documentation for explaination of the characters.
	 */
	public synchronized void fill() {
		for (char i = Tile.FIRSTCOLOR; i <= Tile.LASTCOLOR; i++) {
			for (char j = Tile.FIRSTSHAPE; j <= Tile.LASTSHAPE; j++) {
				for (char k = 0; k < Game.TILESPERTYPE; k++) {
					this.content.add(new Tile(i, j));
				}
			}
		}
	}

	/**
	 * Empties the bag.
	 */
	public void empty() {
		this.content.clear();
	}

	/**
	 * Returns the current number of tiles in a bag.
	 * 
	 * @return The number of tiles.
	 */
	public int getNumberOfTiles() {
		return this.content.size();
	}

	/**
	 * Swaps the given Tiles from a given Hand with random Tiles from the Bag.
	 * 
	 * @param hand
	 *            The hand which would like to swap.
	 * @param tiles
	 *            The tiles that should be put back into the bag.
	 * @return True if the swap succeeded, false otherwise.
	 * @throws TooManyTilesInBag
	 * @throws TileNotInHandException
	 */
	public synchronized void swapTiles(Hand hand, List<Tile> tiles)
					throws TooFewTilesInBagException, TileNotInBagException, TooManyTilesInBag,
					TileNotInHandException {
		if (tiles.size() > this.getNumberOfTiles()) {
			throw new TooFewTilesInBagException(tiles.size(), this.getNumberOfTiles());
		}

		hand.removeFromHand(tiles);

		try {
			this.takeFromBag(hand, tiles.size());
		} catch (HandLimitReachedExeption e) {
			Util.log(e);
		}

		this.addToBag(tiles);
	}

	/**
	 * Move amount Tiles from the Bag into the given Hand.
	 * 
	 * @param hand
	 *            The Hand to which drawn Tiles should be added.
	 * @param amount
	 *            The amount of Tiles that should be added.
	 * @throws TooFewTilesInBagException
	 * @throws HandLimitReachedExeption
	 */
	public synchronized void takeFromBag(Hand hand, int amount) throws TooFewTilesInBagException,
					TileNotInBagException, HandLimitReachedExeption {
		if (amount > this.getNumberOfTiles()) {
			throw new TooFewTilesInBagException(amount, this.getNumberOfTiles());
		}
		for (int i = 0; i < amount; i++) {
			Tile tile = this.content.get(randomGenerator.nextInt(this.getNumberOfTiles()));
			hand.addToHand(tile);
			this.removeFromBag(tile);
		}
	}

	/**
	 * Adds the given Tile to the Bag.
	 * 
	 * @param tile
	 *            The Tile to be added.
	 * @throws TooManyTilesInBag
	 */
	public synchronized void addToBag(Tile tile) throws TooManyTilesInBag {
		if (this.getNumberOfTiles() + 1 <= Game.DIFFERENTCOLORS * Game.DIFFERENTSHAPES
						* Game.TILESPERTYPE) {
			this.content.add(tile);
		} else {
			throw new TooManyTilesInBag(this.getNumberOfTiles(), 1);
		}
	}

	/**
	 * Adds the given Tiles to the Bag.
	 * 
	 * @param tiles
	 *            The Tiles to be added.
	 * @throws TooManyTilesInBag
	 */
	public synchronized void addToBag(List<Tile> tiles) throws TooManyTilesInBag {
		if (this.getNumberOfTiles() + tiles.size() <= Game.DIFFERENTCOLORS * Game.DIFFERENTSHAPES
						* Game.TILESPERTYPE) {
			for (Tile t : tiles) {
				this.addToBag(t);
			}
		} else {
			throw new TooManyTilesInBag(this.getNumberOfTiles(), tiles.size());
		}
	}

	/**
	 * Remove the given Tile from the Bag.
	 * 
	 * @param tile
	 *            The Tile to be removed.
	 * @throws TileNotInBagException
	 */
	private synchronized void removeFromBag(Tile tile) throws TileNotInBagException {
		if (!this.content.contains(tile)) {
			throw new TileNotInBagException(tile);
		}
		this.content.remove(tile);
	}

	/**
	 * Returns a string representation of the bag, showing the amount of tiles
	 * left.
	 */
	public String toString() {
		return "Bag containing " + this.getNumberOfTiles() + " tiles.";
	}

}
