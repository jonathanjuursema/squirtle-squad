package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Observable;

import application.Util;
import exceptions.HandLimitReachedExeption;
import exceptions.IllegalMoveException;
import exceptions.IllegalTurnException;
import exceptions.SquareOutOfBoundsException;
import exceptions.TileNotInBagException;
import exceptions.TileNotInHandException;
import exceptions.TooFewTilesInBagException;
import exceptions.TooManyTilesInBag;
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

	public void addMove(Move move)
					throws SquareOutOfBoundsException, IllegalMoveException, IllegalTurnException {
		if (this.swap.size() != 0) {
			throw new IllegalTurnException();
		}

		if (move.isValidMove(this.getBoardCopy(), this)) {
			this.moves.add(move);
			this.boardCopy.placeTile(move.getTile(), move.getPosition().getX(),
							move.getPosition().getY());
		} else {
			throw new IllegalMoveException(move);
		}

		setChanged();
		notifyObservers("moveAdded");
		// TODO: implement further
	}

	/**
	 * Removes a move from the turn and updates the copy of the board
	 * accordingly.
	 * 
	 * @param move
	 *            The move that needs to be removed.
	 * @throws SquareOutOfBoundsException
	 *             Is thrown when the given BoardSquare cannot be found on the
	 *             board.
	 */
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

		if (!this.swap.contains(t)) {
			this.swap.add(t);
		}

		setChanged();
		notifyObservers("swapAdded");
	}

	/**
	 * Removes swap request from the turn. Important to notice that the tile is
	 * not automatically added to the hand, yet.
	 * 
	 * @param t
	 *            The tile that needs to be removed.
	 * @throws IllegalTurnException
	 *             If tile is not in the turn. TODO: Make exception more
	 *             specific
	 */

	public void removeSwapRequest(Tile t) throws IllegalTurnException {
		if (this.getSwap().contains(t)) {
			this.getSwap().remove(t);
		} else {
			throw new IllegalTurnException();
		}
	}

	/**
	 * This function maps the rows or columns that are affiliated with the
	 * applied moves. These sequences are the basis of the check whether a
	 * sequence is valid and is the basis of the calculation of the score.
	 * 
	 * @return A map with each move mapped to the tiles in te same row and
	 *         coloumn.
	 * @throws SquareOutOfBoundsException
	 *             Thrown if the BoardSquare not exists
	 * @throws IllegalMoveException 
	 */

	public static Map<Move, Map<Integer, List<Tile>>> getSequencesByMovesAndBoard(Board board,
					List<Move> moves) throws SquareOutOfBoundsException, IllegalMoveException {

		Map<Move, Map<Integer, List<Tile>>> sequences = new HashMap<Move, Map<Integer, List<Tile>>>();

		for (Move move : moves) {
			BoardSquare currentSquare;

			Map<Integer, List<Tile>> directionMap = new HashMap<Integer, List<Tile>>();
			for (int i = 0; i < 4; i++) {
				List<Tile> currentList = new ArrayList<Tile>();

				currentSquare = board.getSquare(move.getPosition().getX(),
								move.getPosition().getY());

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

			
			int southLength = entry.getValue().get(0).size();
			int northLength = entry.getValue().get(2).size();
			int eastLength = entry.getValue().get(1).size();
			int westLength = entry.getValue().get(3).size();
			

			//Util.log("North length", entry.getValue().get(0).size() + "");
			//Util.log("South length", entry.getValue().get(2).size() + "");
			

			entry.getValue().get(0).removeAll(entry.getValue().get(2));
			entry.getValue().get(1).removeAll(entry.getValue().get(3));

			entry.getValue().get(0).addAll(entry.getValue().get(2));
			entry.getValue().get(1).addAll(entry.getValue().get(3));

			Map<Integer, List<Tile>> rowAndColumn = new HashMap<Integer, List<Tile>>();
			rowAndColumn.put(0, entry.getValue().get(0));
			rowAndColumn.put(1, entry.getValue().get(1));

			
			if((southLength + northLength - 1) != entry.getValue().get(0).size()) {
				throw new IllegalMoveException("This tile is allready in this column");
			}
			
			if((eastLength + westLength - 1) != entry.getValue().get(1).size()) {
				throw new IllegalMoveException("This tile is allready in this row");
			}
	
			cleanedMap.put(key, rowAndColumn);
		}
		return cleanedMap;
	}

	/**
	 * The function getScore() calculates the score of the current turn
	 * according to the game rules. First will determined if the moves form a
	 * row or a column. Of each tile the corresponding neighbours (in case of a
	 * row, the columns and viceversa) will be counted.
	 * 
	 * @return The score of the whole turn.
	 * @throws SquareOutOfBoundsException
	 * @throws IllegalMoveException 
	 */

	public int calculateScore() throws SquareOutOfBoundsException, IllegalMoveException {
		// We first create 2 sequences to
		// represent the horizontal line, the row
		// and the vertical line, the column

		if (this.isSwapRequest()) {
			return 0;
		}

		boolean baseIsRow = true;

		if (this.getMoves().size() > 1) {
			if (this.getMoves().get(0).getPosition().getX() == this.getMoves().get(1).getPosition()
							.getX()) {
				// If the sequence is a column, then the row needs to be checked
				baseIsRow = false;
			} else if (this.getMoves().get(0).getPosition().getY() == this.getMoves().get(1)
							.getPosition().getY()) {
				// If the sequence is a row, then the columns needs to be
				// checked
				// System.out.println("[debug] Base sequence is row");
				baseIsRow = true;
			}
		}

		int returnScore = 0;

		Map<Move, Map<Integer, List<Tile>>> cleanedMap = getSequencesByMovesAndBoard(this.boardCopy,
						this.getMoves());

		int rowScore = 0;
		int columnScore = 0;

		for (Move m : this.getMoves()) {
			int rowScoreTemp = cleanedMap.get(m).get(1).size();
			int columnScoreTemp = cleanedMap.get(m).get(0).size();

			// If 0, the row only contains the initial tile
			if (rowScoreTemp == 1) {
				rowScoreTemp = 0;
			}

			// If 0, the row only contains the initial tile
			if (columnScoreTemp == 1) {
				columnScoreTemp = 0;
			}

			// If 6, the score will be doubled.
			if (rowScoreTemp == 6) {
				rowScoreTemp = 12;
			}

			// If 6, the score will be doubled.
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
	 * This function will apply the current turn to a specified board.
	 * 
	 * @param board
	 *            The board to which the turn should be applied.
	 * @param bag
	 *            The bag which this turn uses.
	 * 
	 * @return The list of tiles the player gets back from the bag.
	 * 
	 * @throws TileNotInHandException
	 * @throws TooManyTilesInBag
	 * @throws TileNotInBagException
	 * @throws TooFewTilesInBagException
	 * @throws IllegalTurnException
	 * @throws SquareOutOfBoundsException
	 * @throws HandLimitReachedExeption
	 */
	public List<Tile> applyTurn(Board board, Bag bag) throws TooFewTilesInBagException,
					TileNotInBagException, TooManyTilesInBag, TileNotInHandException,
					IllegalTurnException, SquareOutOfBoundsException, HandLimitReachedExeption {

		if (this.isSwapRequest()) {
			return bag.swapTiles(this.assignedPlayer.getHand(), this.getSwap());
		} else if (this.isMoveRequest()) {
			for (Move m : this.getMoves()) {
				board.placeTile(m.getTile(), m.getPosition().getX(), m.getPosition().getY());
				this.assignedPlayer.getHand().removeFromHand(m.getTile());
			}
			if (moves.size() > bag.getNumberOfTiles()) {
				return bag.takeFromBag(this.assignedPlayer.getHand(), bag.getNumberOfTiles());
			} else {
				return bag.takeFromBag(this.assignedPlayer.getHand(), moves.size());
			}
		} else {
			throw new IllegalTurnException();
		}

	}

	/**
	 * Returns the tiles that are added as a swap request.
	 * 
	 * @return The list of tiles that are contained in the list.
	 */
	public List<Tile> getSwap() {
		return this.swap;
	}

	/**
	 * Returns whether this turn is a swap request or not.
	 * 
	 * @return True if the turn contains any tiles to swap.
	 */
	public boolean isSwapRequest() {
		return this.swap.size() > 0;
	}

	/**
	 * Returns whether this turn contained any moves.
	 * 
	 * @return True if the turn contains moves.
	 */
	public boolean isMoveRequest() {
		return this.getMoves().size() > 0;
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
	 * Get the representation of the board of current turn. This board also
	 * contains any moves applied to the board.
	 * 
	 * @return The copy of the board with the moves
	 */
	public Board getBoardCopy() {
		return boardCopy;
	}

	/**
	 * Returns the player that is in possession of the turn and is allowed to
	 * add a move or swap request.
	 * 
	 * @return The player
	 */
	public Player getPlayer() {
		return this.assignedPlayer;
	}

	/**
	 * The toString function that returns a textual representation of the turn
	 * with the added moves and tiles to request.
	 */
	public String toString() {
		String message = getPlayer().getName() + " played the following approved actions: \n";
		for (Move m : this.getMoves()) {
			message += m.toString() + "\n";
		}
		for (Tile t : this.getSwap()) {
			message += t.toString() + "\n";
		}
		return message;
	}

}
