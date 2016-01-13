package game;

import java.util.ArrayList;
import java.util.List;

public class Move {
	public Tile tileToPlay;
	public BoardSquare position;
	
	public Move(Tile tile, BoardSquare boardsquare) {
		this.tileToPlay = tile;
		this.position = boardsquare;
	}
	
}
