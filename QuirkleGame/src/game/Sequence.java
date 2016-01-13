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
		this.color = beginTile.getColor();
		this.shape = beginTile.getShape();
	}
	
	public Sequence() {
		
	}
	
	/**
	 * Add Tile to sequence.
	 * If this.sequence.size() > 2 then the identity of the sequence will be determined.
	 * @param tile Add tile to array
	 */
	
	public void addTile(Tile tile) {
		if(this.sequence.size() == 2) {
			// If 2 tiles are placed, the identity of the sequence can be determined
			if(this.sequence.get(0).getColor() == this.sequence.get(1).getColor()) {
				this.color = this.sequence.get(0).getColor();
			} else if(this.sequence.get(0).getShape() == this.sequence.get(1).getShape()) {
				this.shape = this.sequence.get(0).getShape();
			} else {
				//throw new IllegalMoveException("");
			}
		}
		
		this.sequence.add(tile);
	}
	
	/**
	 * Checks if the current sequence is 
	 * according to the game rules.
	 * @return true if sequence if according to the game rules
	 */
		
	public boolean checkSequence() {
		List<Character> shape = new ArrayList<Character>();
		List<Character> color = new ArrayList<Character>();
		
		for(Tile t: this.sequence) {
			
			if(this.color != null) {
				if(shape.contains(t.getShape())) {
					return false;
				} else {
					shape.add(t.getShape());
				}
			}
			
			if(this.shape != null) {
				if(color.contains(t.getColor())) {
					return false;
				} else {
					color.add(t.getColor());
				}
			}
		}	
		
		return true;
	}

}
