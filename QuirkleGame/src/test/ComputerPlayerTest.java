package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

import application.Util;
import exceptions.HandLimitReachedExeption;
import exceptions.IllegalMoveException;
import exceptions.IllegalTurnException;
import exceptions.SquareOutOfBoundsException;
import exceptions.TileNotInBagException;
import exceptions.TileNotInHandException;
import exceptions.TooFewTilesInBagException;
import exceptions.TooManyTilesInBag;
import game.Bag;
import game.Board;
import game.BoardSquare;
import game.Hand;
import game.Move;
import game.Tile;
import game.Turn;
import players.DummyPlayer;
import players.Player;

public class ComputerPlayerTest {

	Board board;
	Player player;
	Hand hand;
	Bag bag;

	@Before
	public void setUp() throws Exception {
		this.board = new Board();
		this.player = new DummyPlayer("Peter");
		hand = new Hand();
		this.player.assignHand(hand);
		bag = new Bag();
		bag.fill();
		bag.takeFromBag(this.player.getHand(), 6);
		Util.log(hand.toString());
	}

	@Test
	public synchronized void test() throws SquareOutOfBoundsException {
		Tile redCircle = new Tile(Tile.RED, Tile.CIRCLE);
		Tile yellowCircle = new Tile(Tile.YELLOW, Tile.CIRCLE);
		Tile blueCircle = new Tile(Tile.BLUE, Tile.CIRCLE);

		Move move1 = new Move(redCircle, board.getSquare(0, 0));
		Move move2 = new Move(yellowCircle, board.getSquare(1, 0));
		Move move3 = new Move(blueCircle, board.getSquare(2, 0));

		List<Move> movesToAdd = new ArrayList<Move>();

		movesToAdd.add(move1);
		movesToAdd.add(move2);
		movesToAdd.add(move3);

		for (Move m : movesToAdd) {
			board.placeTile(m.getTile(), m.getPosition().getX(), m.getPosition().getY());
		}

		while (bag.getNumberOfTiles() > 0) {
			
			Util.log(hand.toString());
			Util.log(board.toString());
			Board boardCopy = new Board();
			boardCopy.setBoard(board.copy(board));
			ComputerTest computerTest = new ComputerTest(player, boardCopy);

			computerTest.calculateMoves();
			
			List<Tile> tilesToRemove = new ArrayList<Tile>();
			List<Move> highestScore = null;
			Util.log("Aantal mogelijke zetten: ", "" + computerTest.turnsByScore.size());
			if (computerTest.turnsByScore.size() > 0) {
				SortedSet<Integer> keys = new TreeSet<Integer>(computerTest.turnsByScore.keySet());

				highestScore = computerTest.turnsByScore.get(keys.last());

				Util.log("Highest score", keys.last() + " with following moves " + highestScore);

				for (Move m : highestScore) {
					tilesToRemove.add(m.getTile());
					board.placeTile(m.getTile(), m.getPosition().getX(), m.getPosition().getY());
					Util.log(m.getTile() + "TILE PLACED!!!");
				}
			}

			try {
				if(tilesToRemove.size() > 0) {
					bag.swapTiles(hand, tilesToRemove);
				} else if (computerTest.turnsByScore.size() == 0){ 
					Util.log("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!SWAP REQUEST!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
					bag.swapTiles(hand, hand.getTilesInHand());
				}
			} catch (TooFewTilesInBagException | TileNotInBagException e) {
				Util.log(e);
			} catch (TooManyTilesInBag e) {
				Util.log(e);
			} catch (TileNotInHandException e) {
				Util.log(e);
			}
			
			try {
				this.wait(1000);
			} catch (InterruptedException e) {
				Util.log(e);
			}
			Util.log("BEURT IS GEDAAN!!!");
			
			
		}

	}

}
