package game;

/**
 * TODO Write file header.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
public class Move {
	// TODO: Commenting
	public Tile tileToPlay;
	public BoardSquare position;
	
	public Move(Tile tile, BoardSquare boardsquare) {
		this.tileToPlay = tile;
		this.position = boardsquare;
	}

	public Tile getTile() {
		return tileToPlay;
	}

	public void setTileToPlay(Tile tileToPlay) {
		this.tileToPlay = tileToPlay;
	}

	public BoardSquare getPosition() {
		return position;
	}

	public void setPosition(BoardSquare position) {
		this.position = position;
	}
	
	/**
	 * This function will translate the object into 
	 * a textual representation. It will print the
	 * Tiles contained by the move and the play they
	 * will be placed
	 * @return String The message
	 */
	public String toString() {
		return "Move with Tile " + this.tileToPlay.toString() + " on " + this.position.toString();
	}
	
}
