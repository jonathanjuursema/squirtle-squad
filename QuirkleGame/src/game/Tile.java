package game;

import protocol.Protocol;

public class Tile {
	
	public static final char RED = 'A';
	public static final char ORANGE = 'B';
	public static final char YELLOW = 'C';
	public static final char GREEN = 'D';
	public static final char BLUE = 'E';
	public static final char PURPLE = 'F'; 
	
	public static final char CIRCLE = 'A';
	public static final char CROSS = 'B';
	public static final char DIAMOND = 'C';
	public static final char SQUARE = 'D';
	public static final char STAR = 'E';
	public static final char PLUS = 'F'; 
	
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
	 * Returns a textual representation of the tile in format [COLOR SHAPE].
	 * @return The textual representation.
	 */
	public String toString() {
		
		String response = "[";
		
		if (this.getColor() == Tile.RED) {
			response += "RED";
		} else if (this.getColor() == Tile.ORANGE) {
			response += "ORANGE";
		} else if (this.getColor() == Tile.YELLOW) {
			response += "YELLOW";
		} else if (this.getColor() == Tile.GREEN) {
			response += "GREEN";
		} else if (this.getColor() == Tile.BLUE) {
			response += "BLUE";
		} else if (this.getColor() == Tile.PURPLE) {
			response += "PURPLE";
		}
		
		response += " ";
		
		if (this.getColor() == Tile.CIRCLE) {
			response += "CIRCLE";
		} else if (this.getColor() == Tile.CROSS) {
			response += "CROSS";
		} else if (this.getColor() == Tile.DIAMOND) {
			response += "DIAMOND";
		} else if (this.getColor() == Tile.SQUARE) {
			response += "SQUARE";
		} else if (this.getColor() == Tile.STAR) {
			response += "STAR";
		} else if (this.getColor() == Tile.PLUS) {
			response += "PLUS";
		}
		
		response += "]";
		
		return response;
		
	}

}
