package game;

import java.util.ArrayList;
import java.util.List;

import server.Player;

/**
 * This hand represents the 'hand' of a player in real life. A hand contains a
 * player's tiles and the contents of the hand can be manipulated through
 * various functions.
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
	 * 
	 * @param ownerPlayer
	 */
	public Hand(Player ownerPlayer) {
		this.ownerPlayer = ownerPlayer;
	}

	/**
	 * Check if the tile is currently in hand.
	 * @param tile
	 * @return true if tile is in hand.
	 */

	public boolean hasInHand(Tile tile) {
		return this.getTilesInHand().contains(tile);
	}

	/**
	 * Check if the list of tiles are currently in hand.
	 * Returns false if even 1 of the tiles in the list is
	 * not in hand.
	 * @param tiles The list of tiles to be checked
	 * @return true if all tiles are in hand.
	 */

	public boolean hasInHand(List<Tile> tiles) {
		for (Tile t : tiles) {
			if (!this.getTilesInHand().contains(t)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Reset the hand: it removes all the tiles an ensures that current hand is
	 * empty. Returns the tiles that are removed so that a bag of board can pick
	 * them.
	 * @return tiles that are removed for hand.
	 */
	public List<Tile> hardResetHand() {
		List<Tile> returnList = new ArrayList<Tile>();
		returnList.addAll(this.tilesInHand);
		this.tilesInHand.clear();
		return returnList;
	}

	/**
	 * Adds a single tile-object to the player's hand to a random empty place.
	 * 
	 * @param tile
	 *            The tile-object that needs to be added to the hand.
	 * @return boolean True if succesful added.
	 */

	public void addToHand(Tile tile) {
		if (this.getAmountOfTiles() + 1 <= Hand.LIMIT) {
			this.tilesInHand.add(tile);
		}
	}

	/**
	 * Adds multiple tiles to the hand.
	 * 
	 * @param tileArray
	 *            An array with tile-object that needs to be added to the hand.
	 * @return boolean True if succesful
	 */

	public void addTohand(List<Tile> tileList) {
		for (Tile t : tileList) {
			this.addToHand(t);
		}

	}

	/**
	 * Removes a tile object from hand.
	 * 
	 * @param The
	 *            tile-object that needs to be removed from the hand.
	 * @return true if succesful
	 */

	public void removeFromHand(Tile tile) {
		if (this.hasInHand(tile)) {
			this.tilesInHand.remove(tile);
		}
	}

	/**
	 * Removes multiple tiles from hand
	 * 
	 * @param tileList
	 *            The list of tiles that needs to be removed.
	 * @return true if succesful
	 */

	public void removeFromHand(List<Tile> tileList) {
		for (Tile t : tileList) {
			if (this.hasInHand(t)) {
				this.removeFromHand(t);
			}
		}
	}

	/**
	 * Returns the tiles that are currently in hand.
	 * 
	 * @return An array with tile-objects.
	 */

	public List<Tile> getTilesInHand() {
		return this.tilesInHand;
	}

	/**
	 * Get the owner of the hand
	 * 
	 * @return Player object
	 */
	public Player getOwnerOfHand() {
		return this.ownerPlayer;
	}

	/**
	 * 
	 * @return Returns the amount of tiles that are currently in hand.
	 */

	public int getAmountOfTiles() {
		return this.tilesInHand.size();
	}

	/**
	 * @return a textual representation of the current hand.
	 */

	public String toString() {
		String returnMessage = "Displaying hand (" + this.getAmountOfTiles() + ") from " + this.getOwnerOfHand().getName() + ": \n";
		
		returnMessage += "+";
		for(int i = 0; i < this.getAmountOfTiles() - 1; i++){
			returnMessage += "----|";
		}
		returnMessage += "----+";
		returnMessage += "\n|";
		int count = this.getAmountOfTiles() - 1;
		for (Tile t : this.getTilesInHand()) {
			returnMessage += " "+t.toString()+" ";
			if(count != 0){
				returnMessage += "|";
			}
			count--;
		}
		returnMessage += "|";
		returnMessage += "\n+";
		for(int i = 0; i < this.getAmountOfTiles() - 1; i++){
			returnMessage += "----|";
		}
		returnMessage += "----+";
		return returnMessage;
	}

}