package test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import exceptions.IllegalMoveException;
import exceptions.IllegalTurnException;
import exceptions.SquareOutOfBoundsException;
import game.Bag;
import game.Board;
import game.Move;
import game.Tile;
import game.Turn;
import players.ClientPlayer;
import players.HumanPlayer;

public class TurnTest {

	public Turn turn;
	public Board board;
	public ClientPlayer player;
	public Bag bag;

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
	public void setUp() {
		player = new HumanPlayer("Test", null);
		board = new Board();

	}

	@Test
	public void testInitialze() {
		turn = new Turn(board, player);
		assertEquals(player, turn.getPlayer());
		// The copy should not be the same object
		assertFalse(board.equals(turn.getBoardCopy()));

		Board testBoard = new Board();
		assertFalse(testBoard.equals(turn.getBoardCopy()));

		assertEquals(0, turn.getMoves().size());
	}

	@Test
	public void addMovesTest() throws SquareOutOfBoundsException {
		turn = new Turn(board, player);
		Move move1 = new Move(redCircle, board.getSquare(0, 0));
		try {
			turn.addMove(move1);
		} catch (IllegalMoveException | IllegalTurnException e) {
			// This exception should never been throwed. If so, this assert will
			// fail.
			assertFalse(true);
		}

		Move move2 = new Move(yellowCircle, board.getSquare(0, 1));

		try {
			turn.addMove(move2);
		} catch (IllegalMoveException | IllegalTurnException e) {
			// This exception should never been throwed. If so, this assert will
			// fail.
			assertFalse(true);
		}
		assertTrue(turn.getMoves().contains(move2));
		assertTrue(turn.getMoves().contains(move1));

		assertTrue(turn.getBoardCopy().getSquare(move1.getPosition().getX(), move1.getPosition().getY()).getTile()
				.equals(move1.getTile()));

		// The original board should not be updated
		assertTrue(board.getSquare(move2.getPosition().getX(), move2.getPosition().getY()).isEmpty());
		assertTrue(board.getSquare(move1.getPosition().getX(), move1.getPosition().getY()).isEmpty());

	}

	@Test
	public void swapAndMoveTest() throws SquareOutOfBoundsException {
		turn = new Turn(board, player);
		Move move1 = new Move(redCircle, board.getSquare(0, 0));
		try {
			turn.addMove(move1);
		} catch (IllegalMoveException | IllegalTurnException e) {
			// This exception should never been throwed. If so, this assert will
			// fail.
			assertFalse(true);
		}

		try {
			turn.addSwapRequest(greenCircle);
			// This will always throw an exception so the next line will be
			// ignored.
			assertFalse(true);
		} catch (IllegalTurnException e) {
			assertTrue(true); // Not really neccesary.
		}

	}
	
	@Test
	public void moveAndSwapTest() throws SquareOutOfBoundsException {
		turn = new Turn(board, player);

		try {
			turn.addSwapRequest(greenCircle);
		} catch (IllegalTurnException e) {
			assertTrue(false); // Not really neccesary.
		}
		
		Move move1 = new Move(redCircle, board.getSquare(0, 0));
		try {
			turn.addMove(move1);
		} catch (IllegalMoveException | IllegalTurnException e) {
			assertTrue(true);
		}

	}
	
	@Test
	public void removeTest() throws SquareOutOfBoundsException {
		turn = new Turn(board, player);

		Move move1 = new Move(redCircle, board.getSquare(0, 0));
		try {
			turn.addMove(move1);
		} catch (IllegalMoveException | IllegalTurnException e) {
			assertTrue(true);
		}
		assertFalse(turn.isSwapRequest());
		assertTrue(turn.isMoveRequest());
		
		assertEquals(1, turn.getMoves().size());
		
		turn.removeMove(move1);
		
		assertEquals(0, turn.getMoves().size());
		
		assertTrue(turn.getBoardCopy().getSquare(0, 0).isEmpty());
		
	}
	
	@Test (expected = IllegalMoveException.class)
	public void illegalMoveTest() throws SquareOutOfBoundsException, IllegalTurnException, IllegalMoveException {
		turn = new Turn(board, player);
		Move move1 = new Move(redCircle, board.getSquare(1, 0));
		
		turn.addMove(move1);
		Move move2 = new Move(yellowCircle, board.getSquare(1, 1));

		turn.addMove(move2);
		assertFalse(turn.getMoves().contains(move2));
		assertFalse(turn.getMoves().contains(move1));
	}
	
	@Test
	public void removeSwapRequest() throws SquareOutOfBoundsException, IllegalTurnException {
		turn = new Turn(board, player);

		try {
			turn.addSwapRequest(greenCircle);
		} catch (IllegalTurnException e) {
			assertTrue(false); // Not really neccesary.
		}
		
		assertTrue(turn.isSwapRequest());
		assertFalse(turn.isMoveRequest());

		turn.removeSwapRequest(greenCircle);
		
		assertEquals(0, turn.getSwap().size());
		
		try {
			turn.removeSwapRequest(greenCircle);
			assertTrue(false); 
		} catch (IllegalTurnException e) {
			assertTrue(true);// Not really neccesary.
		}

	}
	
	@Test
	public void calculateScoreTest() throws SquareOutOfBoundsException, IllegalTurnException, IllegalMoveException {
		turn = new Turn(board, player);
		
		assertEquals(0, turn.calculateScore());
		
		turn.addSwapRequest(blueSQUARE);
		
		assertEquals(0, turn.calculateScore());
		
		turn = new Turn(board, player);

		Move move1 = new Move(new Tile(Tile.RED, Tile.CIRCLE), turn.getBoardCopy().getSquare(0, 0));
		Move move2 = new Move(new Tile(Tile.YELLOW, Tile.CIRCLE), turn.getBoardCopy().getSquare(0, 1));
		Move move3 = new Move(new Tile(Tile.BLUE, Tile.CIRCLE), turn.getBoardCopy().getSquare(0, 2));
		Move move4 = new Move(new Tile(Tile.PURPLE, Tile.CIRCLE), turn.getBoardCopy().getSquare(0, 3));
		Move move5 = new Move(new Tile(Tile.ORANGE, Tile.CIRCLE), turn.getBoardCopy().getSquare(0, 4));
		Move move6 = new Move(new Tile(Tile.GREEN, Tile.CIRCLE), turn.getBoardCopy().getSquare(0, 5));

		turn.addMove(move1);

		turn.addMove(move2);
		assertEquals(2, turn.calculateScore());

		turn.addMove(move3);
		assertEquals(3, turn.calculateScore());

		turn.addMove(move4);
		assertEquals(4, turn.calculateScore());

		turn.addMove(move5);
		assertEquals(5, turn.calculateScore());

		turn.addMove(move6);
		assertEquals(12, turn.calculateScore());

		turn.calculateScore();
		
		assertTrue(turn.toString().contains("R0"));
		
		for(Move m : turn.getMoves()){
			board.placeTile(m.getTile(), m.getPosition().getX(), m.getPosition().getY());
		}

		turn = new Turn(board, player);

		Move move_1 = new Move(new Tile(Tile.YELLOW, Tile.SQUARE), turn.getBoardCopy().getSquare(1, 1));
		Move move_2 = new Move(new Tile(Tile.YELLOW, Tile.CROSS), turn.getBoardCopy().getSquare(2, 1));
		Move move_3 = new Move(new Tile(Tile.YELLOW, Tile.DIAMOND), turn.getBoardCopy().getSquare(3, 1));
		Move move_4 = new Move(new Tile(Tile.YELLOW, Tile.PLUS), turn.getBoardCopy().getSquare(4, 1));
		Move move_5 = new Move(new Tile(Tile.YELLOW, Tile.STAR), turn.getBoardCopy().getSquare(5, 1));

		turn.addMove(move_1);
		assertEquals(2, turn.calculateScore());

		System.out.println(turn.getBoardCopy());

		turn.addMove(move_2);
		assertEquals(3, turn.calculateScore());

		turn.addMove(move_3);
		assertEquals(4, turn.calculateScore());

		turn.addMove(move_4);
		assertEquals(5, turn.calculateScore());

		turn.addMove(move_5);
		assertEquals(12, turn.calculateScore());

		turn.calculateScore();
		
		for(Move m : turn.getMoves()){
			board.placeTile(m.getTile(), m.getPosition().getX(), m.getPosition().getY());
		}
		
		turn = new Turn(board, player);

		Move newMove1 = new Move(new Tile(Tile.YELLOW, Tile.STAR), turn.getBoardCopy().getSquare(3, 2));
		turn.addMove(newMove1);

		assertEquals(2, turn.calculateScore());
		
		for(Move m : turn.getMoves()){
			board.placeTile(m.getTile(), m.getPosition().getX(), m.getPosition().getY());
		}

		turn = new Turn(board, player);

		Move newMove2 = new Move(new Tile(Tile.GREEN, Tile.CIRCLE), turn.getBoardCopy().getSquare(1, 3));
		turn.addMove(newMove2);

		Move newMove3 = new Move(new Tile(Tile.BLUE, Tile.CIRCLE), turn.getBoardCopy().getSquare(2, 3));
		turn.addMove(newMove3);

		Move newMove4 = new Move(new Tile(Tile.YELLOW, Tile.CIRCLE), turn.getBoardCopy().getSquare(3, 3));
		turn.addMove(newMove4);

		assertEquals(7, turn.calculateScore());
		
		for(Move m : turn.getMoves()){
			board.placeTile(m.getTile(), m.getPosition().getX(), m.getPosition().getY());
		}
		
		turn = new Turn(board, player);

		Move newMove5 = new Move(new Tile(Tile.YELLOW, Tile.CIRCLE), turn.getBoardCopy().getSquare(4, 2));
		turn.addMove(newMove5);

		Move newMove6 = new Move(new Tile(Tile.YELLOW, Tile.SQUARE), turn.getBoardCopy().getSquare(5, 2));
		turn.addMove(newMove6);

		assertEquals(7, turn.calculateScore());
		
		for(Move m : turn.getMoves()){
			board.placeTile(m.getTile(), m.getPosition().getX(), m.getPosition().getY());
		}

		turn = new Turn(board, player);

		Move newMove7 = new Move(new Tile(Tile.BLUE, Tile.SQUARE), turn.getBoardCopy().getSquare(-1, 2));
		turn.addMove(newMove7);

		Move newMove8 = new Move(new Tile(Tile.BLUE, Tile.STAR), turn.getBoardCopy().getSquare(-2, 2));
		turn.addMove(newMove8);

		assertEquals(3, turn.calculateScore());
		
		for(Move m : turn.getMoves()){
			board.placeTile(m.getTile(), m.getPosition().getX(), m.getPosition().getY());
		}

		turn = new Turn(board, player);

		Move newMove9 = new Move(new Tile(Tile.YELLOW, Tile.CROSS), turn.getBoardCopy().getSquare(3, 0));
		turn.addMove(newMove9);

		Move newMove10 = new Move(new Tile(Tile.YELLOW, Tile.SQUARE), turn.getBoardCopy().getSquare(3, -1));
		turn.addMove(newMove10);

		Move newMove11 = new Move(new Tile(Tile.YELLOW, Tile.PLUS), turn.getBoardCopy().getSquare(3, -2));
		turn.addMove(newMove11);

		assertEquals(12, turn.calculateScore());
		
		for(Move m : turn.getMoves()){
			board.placeTile(m.getTile(), m.getPosition().getX(), m.getPosition().getY());
		}

		turn = new Turn(board, player);

		Move newMove13 = new Move(new Tile(Tile.YELLOW, Tile.CIRCLE), turn.getBoardCopy().getSquare(5, 0));
		turn.addMove(newMove13);

		Move newMove14 = new Move(new Tile(Tile.YELLOW, Tile.PLUS), turn.getBoardCopy().getSquare(5, -1));
		turn.addMove(newMove14);
		
		for(Move m : turn.getMoves()){
			board.placeTile(m.getTile(), m.getPosition().getX(), m.getPosition().getY());
		}

		turn = new Turn(board, player);

		Move newMove15 = new Move(new Tile(Tile.YELLOW, Tile.CIRCLE), turn.getBoardCopy().getSquare(2, -1));
		turn.addMove(newMove15);

		assertEquals(2, turn.calculateScore());
		
		for(Move m : turn.getMoves()){
			board.placeTile(m.getTile(), m.getPosition().getX(), m.getPosition().getY());
		}

		System.out.println(turn.getBoardCopy());

	}

}
