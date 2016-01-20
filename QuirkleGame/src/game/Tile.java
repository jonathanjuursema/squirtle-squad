package game;

/**
 * A Tile represents a woorden tile in the real game. In the real game, there exist multiple
 * copies of a tile with the same properties (same color, same shape). This is also true with 
 * this implementation, because different tiles with the same properties have different addresses.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
public class Tile {
	
	public static final char RED = 'A';
	public static final char ORANGE = 'B';
	public static final char YELLOW = 'C';
	public static final char GREEN = 'D';
	public static final char BLUE = 'E';
	public static final char PURPLE = 'F'; 
	
	public static final char BLACK = 'Z'; 

	public static final char FIRSTCOLOR = 'A';
	public static final char LASTCOLOR = 'F';
	
	public static final char CIRCLE = 'A';
	public static final char CROSS = 'B';
	public static final char DIAMOND = 'C';
	public static final char SQUARE = 'D';
	public static final char STAR = 'E';
	public static final char PLUS = 'F'; 
	
	public static final char DUMMY = 'Z'; 
	
	public static final char FIRSTSHAPE = 'A';
	public static final char LASTSHAPE = 'F';
	
	private char color;
	private char shape;
	
	/**
	 * Constructor of Tile. Requires the color and shape on the tile.
	 * @param color The tile color.
	 * @param shape The tile shape.
	 */
	public Tile(char color, char shape) {
		this.color = color;
		this.shape = shape;
	}
	
	/**
	 * Returns the color of the tile.
	 * @return The color character of the tile.
	 */
	public char getColor() {
		return this.color;
	}
	
	/**
	 * Returns the shape of the tile.
	 * @return The shape character of the tile.
	 */
	public char getShape() {
		return this.shape;
	}
	
	/**
	 * Returns a textual representation of the tile.
	 * @return The textual representation.
	 */
	public String toString() {
		
		String response = "";
		
		if (this.getColor() == Tile.RED) {
			response += "R";
		} else if (this.getColor() == Tile.ORANGE) {
			response += "O";
		} else if (this.getColor() == Tile.YELLOW) {
			response += "Y";
		} else if (this.getColor() == Tile.GREEN) {
			response += "G";
		} else if (this.getColor() == Tile.BLUE) {
			response += "B";
		} else if (this.getColor() == Tile.PURPLE) {
			response += "P";
		} else if (this.getColor() == Tile.BLACK) {
			response += " ";
		} 
		
		// Editted getColor to getShape
		if (this.getShape() == Tile.CIRCLE) {
			response += "0";
		} else if (this.getShape() == Tile.CROSS) {
			response += "1";
		} else if (this.getShape() == Tile.DIAMOND) {
			response += "5";
		} else if (this.getShape() == Tile.SQUARE) {
			response += "2";
		} else if (this.getShape() == Tile.STAR) {
			response += "3";
		} else if (this.getShape() == Tile.PLUS) {
			response += "4";
		} else if (this.getShape() == Tile.DUMMY) {
			response += "x";
		}
		
		return response;
		
	}

}
