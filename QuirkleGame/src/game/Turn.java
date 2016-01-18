package game;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;

import exceptions.IllegalMoveException;
import exceptions.IllegalTurnException;
import exceptions.SquareOutOfBoundsException;

/**
 * The class represents the turn. During a turn the assignPlayer have the choice
 * to do several moves or to make an swapRequest. A move can only be added to
 * the turn if the move is according to the game rules.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
public class Turn {

	private List<Move> moves = new ArrayList<Move>();
	private Board boardCopy;

	private Swap swapRequest = null;
	private Player assignedPlayer;
	
	private Condition readySignal;
	
	private boolean isReady;

	private int score;

	/**
	 * Creates a turn with assigned Player and the board. This functions creates
	 * a deepcopy of the board.
	 * 
	 * @param player
	 */

	public Turn(Board board, Player currentPlayer, Condition readySignal) {
		this.boardCopy = new Board(board.getGame());
		this.boardCopy.setBoard(board.copy(this.boardCopy));		
		this.assignedPlayer = currentPlayer;
		this.isReady = false;
		// TODO: implement body
	}

	/**
	 * Add a move to the turn. If not possible it throws an exception.
	 * 
	 * @param move
	 *            the move that needs to
	 * @throws SquareOutOfBoundsException 
	 */

	public void addMove(Move move) throws SquareOutOfBoundsException, IllegalMoveException {
				
		if(move.isValidMove(this.getBoardCopy())) {
			this.moves.add(move);
			this.boardCopy.placeTile(move.getTile(), move.getPosition().getX(), move.getPosition().getY());
		} else {
			throw new IllegalMoveException(move);
		}
	
		// TODO: implement further
	}
	
	public void removeMove(Move move) throws SquareOutOfBoundsException {
		this.boardCopy.removeTile(move.getPosition().getX(), move.getPosition().getY());
		this.moves.remove(move);
	}

	/**
	 * Add a swap request to the turn.
	 * 
	 * @param swap
	 *            object with the tiles that needs to be swapped.
	 * @throws IllegalTurnException 
	 */
	public void addSwapRequest(Swap swap) throws IllegalTurnException {
		if (this.getMoves().size() != 0) {
			throw new IllegalTurnException();
		}
	}

	/**
	 * Function to check if current turn is possible. It is for
	 * example not possible to do a swap request and a set of 
	 * moves.
	 * 
	 * @return true if turn is according to the game rules.
	 * @throws SquareOutOfBoundsException
	 * @throws IllegalTurnException
	 * @throws IllegalMoveException
	 */

	public void applyTurn() throws IllegalTurnException {

		// Check if an swapRequest has been added
		if (this.swapRequest != null) {
			// TODO: check swapRequest
		}

		// If moves has been added
		if (this.getMoves().size() != 0) {
			// Check if swapRequest has been added too
			if (this.swapRequest != null) {
				throw new IllegalTurnException();
			}
			
			for(Move m : this.getMoves()) {
				/*
				try {
					this.boardOriginal.placeTile(m.getTile(), m.getPosition().getX(), m.getPosition().getY());
				} catch (SquareOutOfBoundsException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				*/
			}
			
		}
		
	}

	/**
	 * The function getScore() calculates the score of the current turn
	 * according to the game rules. First will determined if the moves form a
	 * row or a column. Of each tile the corresponding neighbours (in case of a
	 * row, the columns and viceversa) will be counted.
	 * 
	 * @return The score of the whole turn.
	 * @throws SquareOutOfBoundsException 
	 */

	public int calculateScore() throws SquareOutOfBoundsException {
		// We first create 2 sequences to
		// represent the horizontal line, the row
		// and the vertical line, the column
		List<Sequence> rows = new ArrayList<Sequence>();
		List<Sequence> columns = new ArrayList<Sequence>();
		
		boolean baseIsRow = true;
		int returnScore = 0;
		
		if(this.moves.size() == 1) {
			return 1;
		}
		
		if (this.getMoves().get(0).getPosition().getX() == this.getMoves().get(1).getPosition().getX()) {
			// If the sequence is a column, then the row needs to be checked
			baseIsRow = false;
		} else if (this.getMoves().get(0).getPosition().getY() == this.getMoves().get(1).getPosition().getY()) {
			// If the sequence is a row, then the columns needs to be checked
			//System.out.println("[debug] Base sequence is row");
			baseIsRow = true;
		}
		
		for(Move move : this.getMoves()) {
			BoardSquare currentSquare;
			
			for (int i = 0; i < 4; i++) {
				Sequence currentSequence = new Sequence();
				currentSquare = this.getBoardCopy().getSquare(move.getPosition().getX(), move.getPosition().getY());
				
				while (!currentSquare.getNeighbour(i).isEmpty()) {
					currentSequence.addTile(currentSquare.getNeighbour(i).getTile());
					currentSquare = currentSquare.getNeighbour(i);
				}
				
				if ((i & 1) == 0) {
					// Sequence is column
					columns.add(currentSequence);				
				} else {
					// Sequence is row
					rows.add(currentSequence);				
				}
			}
		}

		int counter = 0;
		for(Sequence row : rows) {
			counter++;
			if(baseIsRow && counter <= 2) {
				returnScore += row.getScore();
			} else if (!baseIsRow) {
				returnScore += row.getScore();
			}
			
		}
		
		counter = 0;
		for(Sequence column : columns) {
			counter++;
			if(!baseIsRow && counter <= 2) {
				returnScore += column.getScore();
			} else if (baseIsRow) {
				returnScore += column.getScore();
			}
		}
		
		// Because in the base sequence 1 tile is never added so the score must be increased with 1
		returnScore += 1;
		
		// Ugly, yes very ugly
		// TODO: make clever
		if(returnScore == 6) {
			return 12;
		}

		return returnScore;
	}

	/**
	 * Get current moves of the turn.
	 * 
	 * @return
	 */
	public List<Move> getMoves() {
		return moves;
	}

	/**
	 * Get the representation of the board of current turn.
	 * 
	 * @return
	 */
	public Board getBoardCopy() {
		return boardCopy;
	}

	public void setBoardCopy(Board boardCopy) {
		this.boardCopy = boardCopy;
	}
	
	public void setReady() {
		this.isReady = true;
		this.readySignal.notifyAll();
	}
	
	public boolean isReady() {
		return this.isReady;
	}
	
	public Player getPlayer() {
		return this.assignedPlayer;
	}

	public String toString() {
		String message = "The turn is for " + this.assignedPlayer.getName() + "\n";
		message += "With approved moves: \n ";
		for (Move m : this.getMoves()) {
			message += m.toString() + "\n";
		}
		return message;
	}

}
