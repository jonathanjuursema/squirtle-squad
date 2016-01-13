package game;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO Write file header.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
public class Move {
	public Tile tileToPlay;
	public BoardSquare position;
	
	public Move(Tile tile, BoardSquare boardsquare) {
		this.tileToPlay = tile;
		this.position = boardsquare;
	}

	public Tile getTileToPlay() {
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
	
	
	
}
