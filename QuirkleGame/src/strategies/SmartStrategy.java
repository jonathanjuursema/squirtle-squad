package strategies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import application.Util;
import exceptions.IllegalMoveException;
import exceptions.IllegalTurnException;
import exceptions.SquareOutOfBoundsException;
import game.Board;
import game.BoardSquare;
import game.Move;
import game.Tile;
import game.Turn;
import players.Player;

public class SmartStrategy implements StrategyInterface {

	private Player player;
	private Board board;

	public List<Turn> possibleTurns = new ArrayList<Turn>();
	public Map<Integer, List<Move>> turnsByScore = new HashMap<Integer, List<Move>>();
	public List<Move> tilesToAdd = new ArrayList<Move>();

	public SmartStrategy() {
	}

	@Override
	public Turn requestTurn(Turn turn) {
		this.player = turn.getPlayer();
		this.board = turn.getBoardCopy();

		try {
			this.calculateMoves();
		} catch (SquareOutOfBoundsException e) {
			Util.log(e);
		}

		SortedSet<Integer> keys = new TreeSet<Integer>(turnsByScore.keySet());
		List<Move> highestScore = null;
		highestScore = turnsByScore.get(keys.last());

		turn.getMoves().clear();
		if (highestScore.size() == 0) {
			for (Tile t : player.getHand().getTilesInHand()) {
				try {
					turn.addSwapRequest(t);
				} catch (IllegalTurnException e) {
					Util.log(e);
				}
			}
		} else {
			turn.getMoves().addAll(highestScore);
		}

		return turn;
	}

	public void calculateMoves() throws SquareOutOfBoundsException {
		for (Tile t : player.getHand().getTilesInHand()) {

			Turn simTurn = new Turn(board, player);

			if (board.getPossiblePlaceByTile(t).size() > 0) {

				for (BoardSquare b : board.getPossiblePlaceByTile(t)) {
					try {
						board.placeTile(t, b.getX(), b.getY());
					} catch (SquareOutOfBoundsException e) {
						Util.log(e);
					}
					Move move = new Move(t, b);
					try {
						simTurn.addMove(move);
					} catch (IllegalMoveException | IllegalTurnException e) {
						Util.log(e);
						continue;
					}

					board.placeTile(move.getTile(), move.getPosition().getX(),
									move.getPosition().getY());
					recursiveMove(t, simTurn);

					board.removeTile(b.getX(), b.getY());
					simTurn.removeMove(move);
				}
			}

		}

	}

	public void recursiveMove(Tile tile, Turn turn) throws SquareOutOfBoundsException {

		for (Tile t : player.getHand().getTilesInHand()) {

			boolean ignore = false;
			for (Move m : turn.getMoves()) {
				if (m.getTile() == t) {
					ignore = true;
				}
			}

			if (ignore) {
				continue;
			}

			if (board.getPossiblePlaceByTile(t, turn.getMoves()).size() > 0) {

				for (BoardSquare b : board.getPossiblePlaceByTile(t, turn.getMoves())) {

					Move move = new Move(t, b);
					try {
						turn.addMove(move);
					} catch (IllegalMoveException | IllegalTurnException e) {
						Util.log(e);
						continue;
					}

					board.placeTile(move.getTile(), move.getPosition().getX(),
									move.getPosition().getY());
					recursiveMove(t, turn);
					board.removeTile(b.getX(), b.getY());
					List<Move> copy = new ArrayList<Move>();
					for (Move m : turn.getMoves()) {
						copy.add(new Move(m.getTile(), m.getPosition()));
					}

					try {
						turnsByScore.put(turn.calculateScore(), copy);
					} catch (IllegalMoveException e) {
						continue;
					}
					turn.removeMove(move);

				}

			}

		}
	}

}
