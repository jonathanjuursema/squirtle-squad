package game;

import java.util.ArrayList;
import java.util.List;

/**
 * This hand represents the 'hand' of a player in real life. A hand contains a player's tiles
 * and the contents of the hand can be manipulated through various functions.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
public class Hand {
	private List<Tile> tilesInHand = new ArrayList<Tile>();
	private Player ownerPlayer;
	private static final int LIMIT = 6;
	
	/**
	 * Constructor. Sets this.ownerPlayer to ownerPlayer.
	 * @param ownerPlayer
	 */
	public Hand(Player ownerPlayer) {
		// TODO Implement body.
	}
	
	/**
	 * Check if the tile is currently in hand.
	 * @param tile 
	 * @return true if tile is in hand.
	 */
	
	public boolean hasInHand(Tile tile) {
		// TODO Implement body.
		return true;
	}
	
	/**
	 * Check if the tiles is currently in hand.
	 * @param tiles 
	 * @return true if tile is in hand.
	 */
	
	public boolean hasInHand(List<Tile> tiles) {
		// TODO Implement body.
		return true;
	}
	
	/**
	 * Reset the hand: it removes all the tiles an ensures that current hand is empty.
	 * @return boolean, true if succesful
	 */
	public boolean resetHand() {
		// TODO Implement body.
		return true;
	}
	
	/**
	 * Adds a single tile-object to the player's hand to a random empty place.
	 * @param tile The tile-object that needs to be added to the hand.
	 * @return boolean True if succesful added.
	 */
	
	public boolean addToHand(Tile tile) {
		// TODO: Implement addTileToHand
		return true;
	}
	
	/**
	 * Adds multiple tiles to the hand.
	 * @param tileArray An array with tile-object that needs to be added to the hand.
	 * @return boolean True if succesful
	 */
	
	public void addTohand(List<Tile> tileList) {
		// TODO Implement body.
	}
	
	/**
	 * Removes a tile object from hand. In case two identical tiles are in hand, one will be randomly removed.
	 * @param The tile-object that needs to be removed from the hand. 
	 * @return true if succesful
	 */
	
	public void removeFromHand(Tile tile) {
		// TODO: Implement removeTileFromHand 
	}
	
	/**
	 * Removes multiple tiles from hand
	 * @param tileList The list of tiles that needs to be removed.
	 * @return true if succesful
	 */
	
	public boolean removeFromHand(List<Tile> tileList) {
		// TODO: Implement removeTileFromHand 
		return true;
	}
	
	/**
	 * Returns the tiles that are currently in hand.
	 * @return An array with tile-objects.
	 */
	
	public Tile[] getTilesInHand() {
		// TODO Implement body.
		return null;
	}
	
	/**
	 * Get the owner of the hand
	 * @return Player object
	 */
	public Player getOwnerOfHand() {
		// TODO Implement body.
		return null;
	}
	
	/**
	 * 
	 * @return Returns the amount of tiles that are currently in hand.
	 */
	
	public int getAmountOfTiles() {
		// TODO Implement body.
		return 0;
	}
	
	/**
	 * @return a textual representation of the current hand.
	 */
	
	public String toString() {
		return "String";
	}
	
	
}