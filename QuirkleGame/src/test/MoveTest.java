package test;

import static org.junit.Assert.*;


import org.junit.Before;
import org.junit.Test;
import exceptions.IllegalMoveException;
import exceptions.IllegalTurnException;
import exceptions.SquareOutOfBoundsException;
import game.Board;
import game.BoardSquare;
import game.Move;
import game.Tile;
import game.Turn;
import players.DummyPlayer;

public class MoveTest {

	DummyPlayer player;
	Board board;

	public static final Tile redCircle = new Tile(Tile.RED, Tile.CIRCLE);
	public static final Tile yellowCircle = new Tile(Tile.YELLOW, Tile.CIRCLE);
	public static final Tile blueCircle = new Tile(Tile.BLUE, Tile.CIRCLE);
	public static final Tile purpleCircle = new Tile(Tile.PURPLE, Tile.CIRCLE);
	public static final Tile orangeCircle = new Tile(Tile.ORANGE, Tile.CIRCLE);
	public static final Tile greenCircle = new Tile(Tile.GREEN, Tile.CIRCLE);

	public static final Tile redSQUARE = new Tile(Tile.RED, Tile.SQUARE);
	public static final Tile yellowSQUARE = new Tile(Tile.YELLOW, Tile.SQUARE);
	public static final Tile blueSQUARE = new Tile(Tile.BLUE, Tile.SQUARE);
	public static final Tile purpleSQUARE = new Tile(Tile.PURPLE, Tile.SQUARE);
	public static final Tile orangeSQUARE = new Tile(Tile.ORANGE, Tile.SQUARE);
	public static final Tile greenSQUARE = new Tile(Tile.GREEN, Tile.SQUARE);

	@Before
	public void setUp() throws Exception {
		this.board = new Board();
	}

	@Test
	public void initializeTest() throws Exception {
		Tile redCircle = new Tile(Tile.RED, Tile.CIRCLE);
		Move move = new Move(redCircle, board.getSquare(1, 2));

		assertEquals(1, move.getPosition().getX());
		assertEquals(2, move.getPosition().getY());

		assertEquals("R0", move.getTile().toString());
	}

	@Test
	public void setTest() throws Exception {
		BoardSquare bs1 = board.getSquare(1, 2);
		BoardSquare bs2 = board.getSquare(2, 3);

		Move move = new Move(redCircle, bs1);
		move.setPosition(bs2);
		move.setTileToPlay(purpleSQUARE);

		assertEquals(2, move.getPosition().getX());
		assertEquals(3, move.getPosition().getY());

		System.out.println(move.getTile());
	}

	@Test
	public void addToTurnTest() throws SquareOutOfBoundsException {
		Turn turn = new Turn(board, player);

		Move move1 = new Move(redCircle, turn.getBoardCopy().getSquare(0, 0));
		Move move2 = new Move(yellowCircle, turn.getBoardCopy().getSquare(1, 0));
		Move move3 = new Move(blueCircle, turn.getBoardCopy().getSquare(2, 0));
		Move move4 = new Move(purpleCircle, turn.getBoardCopy().getSquare(3, 0));
		Move move5 = new Move(orangeCircle, turn.getBoardCopy().getSquare(4, 0));
		Move move6 = new Move(greenCircle, turn.getBoardCopy().getSquare(5, 0));

		try {
			turn.addMove(move1);
			turn.addMove(move2);
			turn.addMove(move3);
			turn.addMove(move4);
			turn.addMove(move5);
			turn.addMove(move6);

		} catch (IllegalMoveException | IllegalTurnException e) {
			e.printStackTrace();
		}

		assertTrue(turn.getMoves().contains(move1));
		assertTrue(turn.getMoves().contains(move2));
		assertTrue(turn.getMoves().contains(move3));
		assertTrue(turn.getMoves().contains(move4));
		assertTrue(turn.getMoves().contains(move5));
		assertTrue(turn.getMoves().contains(move6));

		Move move7 = new Move(greenCircle, turn.getBoardCopy().getSquare(5, 0));
		assertFalse(turn.getMoves().contains(move7));
	}

	@Test
	public void inValidFirstMoveTest() throws SquareOutOfBoundsException {
		Turn turn = new Turn(board, player);
		Move move1 = new Move(redCircle, turn.getBoardCopy().getSquare(0, 1));

		// Try to add the move to the turn this must throw an exception
		try {
			turn.addMove(move1);
			// If the exception is not thrown, this assert will fail allways.
			assertFalse(true);
		} catch (IllegalMoveException | IllegalTurnException e) {
			assertFalse(false);
		}

	}

	@Test
	public void inValidNotConnectingMoveTest() throws SquareOutOfBoundsException {
		Turn turn = new Turn(board, player);
		Move move1 = new Move(redCircle, turn.getBoardCopy().getSquare(0, 0));
		Move move2 = new Move(yellowCircle, turn.getBoardCopy().getSquare(0, 2));

		// Try to add the move to the turn this must throw an exception
		try {
			turn.addMove(move1);
			turn.addMove(move2);
			// If the exception is not thrown, this assert will fail allways.
			assertFalse(true);
		} catch (IllegalMoveException | IllegalTurnException e) {
			// Assure that the correct move throws the exception
			assertTrue(e.getMessage().contains("Y0"));
		}

	}

	@Test
	public void CurvedMovesTest() throws SquareOutOfBoundsException {
		Turn turn = new Turn(board, player);
		Move move1 = new Move(redCircle, turn.getBoardCopy().getSquare(0, 0));
		Move move2 = new Move(yellowCircle, turn.getBoardCopy().getSquare(0, 1));
		Move move3 = new Move(greenCircle, turn.getBoardCopy().getSquare(1, 1));
		Move move4 = new Move(blueSQUARE, turn.getBoardCopy().getSquare(1, 1));

		// Try to add the move to the turn this must throw an exception
		try {
			turn.addMove(move1);
			turn.addMove(move2);
			turn.addMove(move3);
			// If the exception is not thrown, this assert will fail always,
			// really, always! (except when you are in another universe where
			// false could be true and vice versa.
			assertFalse(true);
		} catch (IllegalMoveException | IllegalTurnException e) {
			// Assure that the correct move throws the exception
			assertTrue(e.getMessage().contains("G0"));
		}

		// Try to add an tile that is invalid in the sequence and is not added
		// to the row.
		// Try to add the move to the turn this must throw an exception
		try {
			turn.addMove(move4);
			// If the exception is not thrown, this assert will fail always.
			assertFalse(true);
		} catch (IllegalMoveException | IllegalTurnException e) {
			// Assure that the correct move throws the exception
			System.out.println(e.getMessage());
			assertTrue(e.getMessage().contains("B2"));
		}
	}

	@Test
	public void IllegalSequenceTest() throws Exception {
		Turn turn = new Turn(board, player);
		Move move1 = new Move(redCircle, turn.getBoardCopy().getSquare(0, 0));
		Move move2 = new Move(yellowCircle, turn.getBoardCopy().getSquare(0, 1));
		Move move3 = new Move(greenCircle, turn.getBoardCopy().getSquare(0, 2));
		Move move4 = new Move(blueSQUARE, turn.getBoardCopy().getSquare(0, 3));

		// Try to add the move to the turn this must throw an exception
		try {
			turn.addMove(move1);
			turn.addMove(move2);
			turn.addMove(move3);
			turn.addMove(move4);
			// If the exception is not thrown, this assert will fail always,
			// really, always! (except when you are in another universe where
			// false could be true and vice versa.
			assertFalse(true);
		} catch (IllegalMoveException | IllegalTurnException e) {
			// Assure that the correct move throws the exception
			assertTrue(e.getMessage().contains("B2"));
		}
		
		Turn turn1 = new Turn(board, player);
		Move move5 = new Move(redCircle, turn.getBoardCopy().getSquare(0, 0));
		Move move6 = new Move(redSQUARE, turn.getBoardCopy().getSquare(0, 1));
		Move move8 = new Move(blueSQUARE, turn.getBoardCopy().getSquare(0, 3));

		// Try to add the move to the turn this must throw an exception
		try {
			turn1.addMove(move5);
			turn1.addMove(move6);
			turn1.addMove(move8);
			// If the exception is not thrown, this assert will fail always,
			// really, always! (except when you are in another universe where
			// false could be true and vice versa.
			assertFalse(true);
		} catch (IllegalMoveException | IllegalTurnException e) {
			// Assure that the correct move throws the exception
			assertTrue(e.getMessage().contains("B2"));
		}
	}
}
