package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Observable;

import exceptions.IllegalMoveException;
import exceptions.IllegalTurnException;
import exceptions.SquareOutOfBoundsException;
import players.Player;

/**
 * The class represents the turn. During a turn the assignPlayer have the choice
 * to do several moves or to make an swapRequest. A move can only be added to
 * the turn if the move is according to the game rules.
 * 
 * @author Jonathan Juursema & Peter Wessels
 *
 */
public class Turn extends Observable {

	private List<Move> moves = new ArrayList<Move>();
	public List<Tile> swap = new ArrayList<Tile>();

	private Board boardCopy;

	public Player assignedPlayer;

	/**
	 * Creates a turn with assigned Player and the board. This functions creates
	 * a deepcopy of the board.
	 * 
	 * @param player
	 */

	public Turn(Board board, Player currentPlayer) {
		this.boardCopy = new Board();
		this.boardCopy.setBoard(board.copy(this.boardCopy));
		this.assignedPlayer = currentPlayer;
	}

	/**
	 * Add a move to the turn. If not possible it throws an exception.
	 * 
	 * @param move
	 *            the move that needs to
	 * @throws SquareOutOfBoundsException
	 * @throws IllegalTurnException
	 */

	public void addMove(Move move) throws SquareOutOfBoundsException, IllegalMoveException, IllegalTurnException {
		if (this.swap.size() != 0) {
			throw new IllegalTurnException();
		}

		boolean unvalid = true;

		if (this.getMoves().size() > 0) {

			Move lastMove = this.getMoves().get(0);

			if (lastMove.getPosition().getX() == move.getPosition().getX()) {

				if (move.getPosition().getY() > lastMove.getPosition().getY()) {

					BoardSquare temp = move.getPosition();
					unvalid = false;
					while (!temp.getNeighbour(2).isEmpty()) {
						if (temp.getNeighbour(2) == lastMove.getPosition()) {
							unvalid = true;
						}
						temp = temp.getNeighbour(2);
					}
				}

				if (move.getPosition().getY() < lastMove.getPosition().getY()) {
					BoardSquare temp = move.getPosition();
					unvalid = false;
					while (!temp.getNeighbour(0).isEmpty()) {
						if (temp.getNeighbour(0) == lastMove.getPosition()) {
							unvalid = true;
						}
						temp = temp.getNeighbour(0);
					}
				}
			}

			if (lastMove.getPosition().getY() == move.getPosition().getY()) {

				if (move.getPosition().getX() > lastMove.getPosition().getX()) {

					BoardSquare temp = move.getPosition();
					unvalid = false;
					while (!temp.getNeighbour(3).isEmpty()) {
						if (temp.getNeighbour(3) == lastMove.getPosition()) {
							unvalid = true;
						}
						temp = temp.getNeighbour(3);
					}
				}

				if (move.getPosition().getX() < lastMove.getPosition().getX()) {

					BoardSquare temp = move.getPosition();
					unvalid = false;
					while (!temp.getNeighbour(1).isEmpty()) {
						if (temp.getNeighbour(1) == lastMove.getPosition()) {
							unvalid = true;
						}
						temp = temp.getNeighbour(1);
					}
				}
			}
		}
		
		if(!unvalid) {
			throw new IllegalMoveException(move, "This move is not placed in the same row or column as the previous move.");
		}

		if (move.isValidMove(this.getBoardCopy())) {
			this.moves.add(move);
			this.boardCopy.placeTile(move.getTile(), move.getPosition().getX(), move.getPosition().getY());
		} else {
			throw new IllegalMoveException(move);
		}
		
		setChanged();
		notifyObservers("turn");
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
	public void addSwapRequest(Tile t) throws IllegalTurnException {
		if (this.getMoves().size() != 0) {
			throw new IllegalTurnException();
		}
		
		if(!this.swap.contains(t)){
			this.swap.add(t);
		}
		
		setChanged();
		notifyObservers("turn");
	}

	public List<Tile> getSwap() {
		return this.swap;
	}
	
	public boolean isSwapRequest() {
		return this.swap.size() > 0;
	}
	
	public boolean isMoveRequest() {
		return this.getMoves().size() > 0;
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

		boolean baseIsRow = true;

		if (this.getMoves().size() > 1) {
			if (this.getMoves().get(0).getPosition().getX() == this.getMoves().get(1).getPosition().getX()) {
				// If the sequence is a column, then the row needs to be checked
				baseIsRow = false;
			} else if (this.getMoves().get(0).getPosition().getY() == this.getMoves().get(1).getPosition().getY()) {
				// If the sequence is a row, then the columns needs to be
				// checked
				// System.out.println("[debug] Base sequence is row");
				baseIsRow = true;
			}
		}

		int returnScore = 0;

		Map<Move, Map<Integer, List<Tile>>> cleanedMap = getSequencesByMovesAndBoard(this.boardCopy, this.getMoves());

		int rowScore = 0;
		int columnScore = 0;

		for (Move m : this.getMoves()) {
			int rowScoreTemp = cleanedMap.get(m).get(1).size();
			int columnScoreTemp = cleanedMap.get(m).get(0).size();

			if (rowScoreTemp == 1) {
				rowScoreTemp = 0;
			}
			if (columnScoreTemp == 1) {
				columnScoreTemp = 0;
			}
			if (rowScoreTemp == 6) {
				rowScoreTemp = 12;
			}
			if (columnScoreTemp == 6) {
				columnScoreTemp = 12;
			}

			if (baseIsRow) {
				rowScore = rowScoreTemp;
				columnScore += columnScoreTemp;
			} else {
				rowScore += rowScoreTemp;
				columnScore = columnScoreTemp;
			}
		}

		returnScore = rowScore + columnScore;

		return returnScore;
	}

	/**
	 * @return
	 * @throws SquareOutOfBoundsException
	 */
	public static Map<Move, Map<Integer, List<Tile>>> getSequencesByMovesAndBoard(Board board, List<Move> moves)
			throws SquareOutOfBoundsException {
		Map<Move, Map<Integer, List<Tile>>> sequences = new HashMap<Move, Map<Integer, List<Tile>>>();

		for (Move move : moves) {
			BoardSquare currentSquare;

			Map<Integer, List<Tile>> directionMap = new HashMap<Integer, List<Tile>>();
			for (int i = 0; i < 4; i++) {
				List<Tile> currentList = new ArrayList<Tile>();

				currentSquare = board.getSquare(move.getPosition().getX(), move.getPosition().getY());

				currentList.add(currentSquare.getTile());

				while (!currentSquare.getNeighbour(i).isEmpty()) {
					currentList.add(currentSquare.getNeighbour(i).getTile());
					currentSquare = currentSquare.getNeighbour(i);

				}

				directionMap.put(i, currentList);
			}

			sequences.put(move, directionMap);
		}

		Map<Move, Map<Integer, List<Tile>>> cleanedMap = new HashMap<Move, Map<Integer, List<Tile>>>();

		for (Map.Entry<Move, Map<Integer, List<Tile>>> entry : sequences.entrySet()) {

			Move key = entry.getKey();

			entry.getValue().get(0).removeAll(entry.getValue().get(2));
			entry.getValue().get(1).removeAll(entry.getValue().get(3));

			entry.getValue().get(0).addAll(entry.getValue().get(2));
			entry.getValue().get(1).addAll(entry.getValue().get(3));

			Map<Integer, List<Tile>> rowAndColumn = new HashMap<Integer, List<Tile>>();
			rowAndColumn.put(0, entry.getValue().get(0));
			rowAndColumn.put(1, entry.getValue().get(1));

			cleanedMap.put(key, rowAndColumn);
		}
		return cleanedMap;
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

	public Player getPlayer() {
		return this.assignedPlayer;
	}

	public String toString() {
		String message = getPlayer().getName() + " played the following approved moves: \n";
		for (Move m : this.getMoves()) {
			message += m.toString() + "\n";
		}
		return message;
	}

}
