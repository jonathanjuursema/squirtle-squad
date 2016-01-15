package game;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO Write file header.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
public class Sequence {
	private List<Tile> sequence = new ArrayList<Tile>();
		
	private Character color = null;
	private Character shape = null;
	
	/**
	 * Construct an sequence with the first Tile
	 * @param beginTile The first tile to add
	 */
	
	public Sequence(Tile beginTile) {
		this.addTile(beginTile);
	}
	
	/**
	 * Constructing the sequence with no beginTile
	 */
	
	public Sequence() {
		
	}
	
	/**
	 * Add Tile to sequence.
	 * If this.sequence.size() > 2 then the identity of the sequence will be determined.
	 * @param tile Add tile to array
	 */
	
	public void addTile(Tile tile) {
		
		// If the sequence has 2 tiles, the type of the sequence
		// can be determined.
		
		if(this.sequence.size() == 1) {
			System.out.println("Bepalen van de identity");
			// If the first and second tile got the same color
			// the sequence resembles in the same color.
			// If the first and second tile got the same shape
			// the sequence resembles in the same shape.
			
			if(this.sequence.get(0).getColor() == this.sequence.get(1).getColor()) {
				this.color = this.sequence.get(0).getColor();
			} else if(this.sequence.get(0).getShape() == this.sequence.get(1).getShape()) {
				this.shape = this.sequence.get(0).getShape();
			}
			
			// an extra else statement could be made if
			// both tiles do not corresponded. But I chose
			// to implement this in the checkSequence() method.
			// Otherwise this function throws an IllegalMoveException,
			// which is not desirable.
		}
		
		// Always add the tile to the sequence
		this.sequence.add(tile);
	}
	
	/**
	 * Checks if the current sequence is according to the game rules.
	 * The function checks the sequence according to the identity.
	 * @return true if sequence if according to the game rules
	 */
		
	public boolean checkSequence() {
		// If only 1 tile is representing the sequence,
		// the sequence does not have to be checked.
		if(this.sequence.size() == 1) {
			return true;
		}
		
		// The lists that are used to check the content of an sequence
		List<Character> shape = new ArrayList<Character>();
		List<Character> color = new ArrayList<Character>();
		
		// Loop over every tile in the sequence
		for(Tile t: this.sequence) {
			
			// If the sequence resembles in the same colour
			if(this.color != null) {
				// check if the same shape already exists.
				if(shape.contains(t.getShape())) {
					return false;
				} else {
					// If not, add the shape to the list
					shape.add(t.getShape());
				}
			} else if(this.shape != null) {
				// check if the same colour already exists.
				if(color.contains(t.getColor())) {
					return false;
				} else {
					// If not, add the colour to the list
					color.add(t.getColor());
				}
			} else { 
				// If the sequence has no identity
				return false;
			}
		}	
		
		return true;
	}
	
	public String toString() {
		String message;
		message = "Sequence with ";
		if(color != null && shape == null) {
			message += "the same COLOR as identity \n";
		} else if (color == null && shape != null) {
			message += "the same SHAPE as identity \n";
		} else {
			message += "an unknown identity";
		}
		
		if(this.sequence.size() > 0) {
			message += " with the following tiles:\n";
			message += this.sequence.toString();
		} else { 
			message += " is empty";
		}
		return message;
	}
	
	public int getScore() {
		if(this.sequence.size() == 6) {
			return 12;
		}
		return this.sequence.size();
	}

}
