package game;

import java.util.ArrayList;
import java.util.List;

import exceptions.SquareOutOfBoundsException;

/**
 * TODO Write file header.
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
	
	private static enum sort { SWAP, MOVE, UNDEFINED };
	
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
		} catch (IllegalMoveException e) {
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
	
	private boolean isPossibleTurn() {
		if(this.swapRequest == null) {
			if(this.moves.size() != 0) {
				throw new IllegalTurnExeption(""); 
			}
		} else if (this.moves.size() != 0) {
			if(this.swapRequest != null) {
				throw new IllegalTurnExeption("");
			}
			
			for(Move m : this.moves){
				this.boardCopy.placeTile(m.getTileToPlay(), 
						m.getPosition().getX(), 
						m.getPosition().getY());
				if(!this.isValidMove(m)) {
					throw new IllegalMoveException("");
				}
				// TODO: Implement further
			}
		}
	}
	
	public boolean isValidMove(Move m) throws SquareOutOfBoundsException {
		Sequence row = new Sequence();
		Sequence column = new Sequence(); 
		
		for(int i = 0; i < 4; i++){
			Move currentMove = m;
			
			while(!currentMove.getPosition().getNeighbour(i).isEmpty()) {
				if ((i & 1) == 0) { 
					row.addTile(currentMove.getPosition().getNeighbour(i).getTile());
				} else {
					column.addTile(currentMove.getPosition().getNeighbour(i).getTile());
				}
			}
		}
		
		if(!row.checkSequence() || !column.checkSequence()) {
			return false;
		} else { return true; }
	}
	
	
	/**
	 * Get current moves of the turn.
	 * @return
	 */
	
	public List<Move> getMove() {
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
