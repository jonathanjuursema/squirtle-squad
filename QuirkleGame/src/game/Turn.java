package game;

import java.util.ArrayList;
import java.util.List;

import exceptions.IllegalMoveException;
import exceptions.IllegalTurnException;
import exceptions.SquareOutOfBoundsException;

/**
 * The class represents the turn. During a turn the assignPlayer have the choice to do several
 * moves or to make an swapRequest. A move can only be added to the turn if the move is 
 * according to the game rules.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
public class Turn {
	
	private List<Move> moves = new ArrayList<Move>();
	private Board boardCopy;
	
	private Swap swapRequest;
	private Player assignedPlayer;
	
	private int score;
		
	/**
	 * Creates a turn with assigned Player and the board. 
	 * This functions creates a deepcopy of the board.
	 * @param player
	 */
	
	public Turn(Player player, Board board) {
		this.boardCopy = board.copy();
		this.assignedPlayer = player;
		// TODO: implement body
	}
	
	/**
	 * Add a move to the turn.
	 * If not possible it throws an exception.
	 * @param move the move that needs to 
	 */
	
	public void addMove(Move move) {
		this.moves.add(move);
		try {
			this.isPossibleTurn();
		} catch (IllegalTurnException | IllegalMoveException e) {
			e.printStackTrace();
			this.moves.remove(move);
		}
		// TODO: implement further
	}
	
	/**
	 * Add a swap request to the turn.
	 * @param swap object with the tiles that needs to be swapped.
	 */
	public void addSwapRequest(Swap swap) {
		// TODO: implement further
	}
	
	/**
	 * Private function to check if current turn is possible.
	 * @return true if turn is according to the game rules.
	 */
	
	private boolean isPossibleTurn() throws IllegalTurnException, IllegalMoveException {
		
		// Check if an swapRequest has been added
		if(this.swapRequest != null) {
			// If an swap request has been added then no moves can be added
			if(this.getMoves().size() != 0) {
				throw new IllegalTurnException(); 
			}
			
			// TODO: check swapRequest
		} 
		
		// If moves has been added
		if (this.getMoves().size() != 0) {
			// Check if swapRequest has been added too
			if(this.swapRequest != null) {
				throw new IllegalTurnException();
			}
			// Loop over the moves
			for(Move m : this.getMoves()){
				// Try to place the tile's on the corresponding boardsquare
				try {
					this.getBoardCopy().placeTile(m.getTile(), 
							m.getPosition().getX(), 
							m.getPosition().getY());
				} catch (SquareOutOfBoundsException e) {
					// If the tile could not be placed, then return false
					return false;
				}
				
				// Check if every move is according to the game rules
				if(!this.isValidMove(m)) {
					throw new IllegalMoveException(m);
				}
				// TODO: Implement further
			}
		}
		
		return true;
	}
	
	public boolean isValidMove(Move m) {
		// We first create 2 sequences to 
		// represent the horizontal line, the row
		// and the vertical line, the column 
		Sequence row = new Sequence();
		Sequence column = new Sequence(); 
		
		// Then we loop over the 4 directions 
		// to check the tiles on both 4 directions.
		for(int i = 0; i < 4; i++){
			// The initial Move is set here
			Move currentMove = m;
			
			try {
				// The while loop checks if the current move 
				// has an neighbour in the current direction.
				
				while(!currentMove.getPosition().getNeighbour(i).isEmpty()) {
					
					// If the neighbour is not empty, 
					// the tile will be added to the 
					// corresponding row or column.
					if ((i & 1) == 0) { 
						row.addTile(currentMove.getPosition().getNeighbour(i).getTile());
					} else {
						column.addTile(currentMove.getPosition().getNeighbour(i).getTile());
					}
				}
			} catch (SquareOutOfBoundsException e) {
				// If the neighbour is not existing the
				// next direction will be checked by i++;
				i++;
				e.printStackTrace();
			}
		}
		
		// When all four the directions are check,
		// both the row and column will be checked
		// if the move is legal.
		if(!row.checkSequence() || !column.checkSequence()) {
			return false;
		} else { return true; }
	}
	
	public int calculateScore() {
		// Determine if moves are in same row or column
		List<Integer> directions = new ArrayList<Integer>();
		if(this.getMoves().get(0).getPosition().getX() == this.getMoves().get(1).getPosition().getX()) {
			directions.add(BoardSquare.EAST);
			directions.add(BoardSquare.WEST);
		} else if (this.getMoves().get(0).getPosition().getY() == this.getMoves().get(1).getPosition().getY()) {
			directions.add(BoardSquare.NORTH);
			directions.add(BoardSquare.SOUTH);
		} else {
			// Error
		}
		
		// The score of the moves will be determined by
		// checking the neighbours in opposite directions.
		// In a row, the score of all columns will be counted.
		// In a column, the score of all the rows will be counted.
		
		// Setting the score to 0;
		int score = 0;
		
		// Loop over the directions
		for(Integer direction : directions){
			
			// Loop over the moves that needs to be placed
			for(Move m : this.getMoves()){
				
				// Initialize the sequence 
				Sequence sequence = new Sequence();
				
				// Loop over the tiles on the board in opposite direction.
				int oppositeDirection = (direction + 1) % 4;
				
				try {
					// Get the initial BoardSquare
					BoardSquare currentSquare = m.getPosition();
					
					// Loop over the neighbours in opposite directions
					while(!this.getBoardCopy().getSquare(
							currentSquare.getX(), 
							currentSquare.getY()).getNeighbour(oppositeDirection).isEmpty())
					{
						// Add the tile to the sequence
						sequence.addTile(this.getBoardCopy().getSquare(
								currentSquare.getX(), 
								currentSquare.getY()).getNeighbour(oppositeDirection).getTile());
						
						// Set the new currentsquare
						currentSquare = this.getBoardCopy().getSquare(
								currentSquare.getX(), 
								currentSquare.getY()).getNeighbour(oppositeDirection);
					}
					
				} catch (SquareOutOfBoundsException e) {
					direction++;
				}
				
				// adding the score of the sequence to the total score
				score += sequence.getSize();
			}
		}
		
		// TODO: Add score of column/row of sequence
		
		return score;
	}
	
	/**
	 * Get current moves of the turn.
	 * @return
	 */
	public List<Move> getMoves() {
		return moves;
	}
	
	/**
	 * Get the representation of the board of current turn.
	 * @return
	 */
	public Board getBoardCopy() {
		return boardCopy;
	}
	
	
	public void setBoardCopy(Board boardCopy) {
		this.boardCopy = boardCopy;
	}

}
