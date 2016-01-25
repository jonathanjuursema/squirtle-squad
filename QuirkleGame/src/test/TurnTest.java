package test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import application.Util;
import exceptions.IllegalMoveException;
import exceptions.IllegalTurnException;
import exceptions.SquareOutOfBoundsException;
import exceptions.TileNotInHandException;
import game.Bag;
import game.Board;
import game.BoardSquare;
import game.Move;
import game.Tile;
import game.Turn;
import players.HumanPlayer;
import players.Player;
import views.TUIview;
/*
public class TurnTest {

	public Turn turn;
	public static Board board;
	public static Player player;
	public static Bag bag;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		player = new HumanPlayer("Test", null);
		board = new Board();
	}

	@Before
	public void setUp() {
		bag = new Bag();
		turn = new Turn(board, player);
	}
	
	@Test
	public void testScore() {
		try {
			Tile t1 = new Tile(Tile.BLUE, Tile.CIRCLE);
			player.getHand().addTohand(t1);
			Move m1 = new Move(, board.getSquare(0, 0));
		} catch (SquareOutOfBoundsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
*/