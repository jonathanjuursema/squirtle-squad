package game;

import exceptions.SquareOutOfBoundsException;

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

	public boolean isValidMove(Board board) throws SquareOutOfBoundsException {
		// Check move in relation with the board
		board.placeTile(this.getTile(), this.getPosition().getX(), this.getPosition().getY());
		
		// TODO: Check if sequence is closed (so that there is a solid row)
		
		// We first create 2 sequences to
		// represent the horizontal line, the row
		// and the vertical line, the column
		Sequence row = new Sequence();
		Sequence column = new Sequence();
		
		// Added the move to both the row and column
		row.addTile(getTile());
		column.addTile(getTile());
		
		// Then we loop over the 4 directions
		// to check the tiles on both 4 directions.
		BoardSquare currentSquare;
		for (int i = 0; i < 4; i++) {
			//System.out.println("[debug] Checking direction " + i);
			
			try {
				// The initial BoardSquare is set here
				currentSquare = board.getSquare(getPosition().getX(), getPosition().getY());
				//System.out.println("[debug] Checking neighbours of " + currentSquare.toString());
				
				// The while loop checks if the current move
				// has an neighbour in the current direction.
				// System.out.println("[debug] Trying to find neighbours.");
				//System.out.println("[debug] Status of neighbour: " + this.boardCopy.getSquare(m.getPosition().getX(), m.getPosition().getY()).getNeighbour(i).toString());
	
				//System.out.println(this.boardCopy);
				
				while (!currentSquare.getNeighbour(i).isEmpty()) {
	
					//System.out.println("[debug] Neighbour found: \n " + currentSquare.getNeighbour(i).toString());
					// If the neighbour is not empty,
					// the tile will be added to the
					// corresponding row or column.
					if ((i & 1) == 0) {
						row.addTile(currentSquare.getNeighbour(i).getTile());
						//System.out.println("[debug] Neighbour added to row");
						
					} else {
						column.addTile(currentSquare.getNeighbour(i).getTile());
						//System.out.println("[debug] Neighbour added to column");
						
					}
					// Setting the next currentMove
					currentSquare = currentSquare.getNeighbour(i);
					//System.out.println("[debug] New current move is assigned");
				}
			} catch (SquareOutOfBoundsException e) {
				// If the neighbour is not existing the
				// next direction will be checked by i++;
				System.out.println(e.getMessage());
			}
	
			//System.out.println("[debug] Direction ended. \n");
		}
		
		//System.out.println(this.boardCopy);
		board.removeTile(this.getPosition().getX(), this.getPosition().getY());
		
		// When all four the directions are checked,
		// both the row and column will be checked
		// if the move is legal.
		if (!row.checkSequence() || !column.checkSequence()) {
			return false;
		} else {
			return true;
		}
	}
	
}
