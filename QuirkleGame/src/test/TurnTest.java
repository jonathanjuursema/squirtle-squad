package test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import exceptions.IllegalMoveException;
import exceptions.IllegalTurnException;
import exceptions.SquareOutOfBoundsException;
import game.Board;
import game.Game;
import game.Move;
import game.Player;
import game.Tile;
import game.Turn;
import server.Server;

public class TurnTest {
	public static Server server;
	public static Game game;
	public Turn turn;
	public static Board board;
	public static Player player;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		server = new Server();
		game = new DummyGame();
		player = new Player("Peter", "");
		board = new Board(game);
	}

	@Before
	public void setUp() throws Exception {

	}

	@Test
	public void testInitialScore() throws SquareOutOfBoundsException, IllegalMoveException, IllegalTurnException {

		board = new Board(game);
		turn = new Turn(board, player);

		Tile redCircle = new Tile(Tile.RED, Tile.CIRCLE);
		Tile yellowCircle = new Tile(Tile.YELLOW, Tile.CIRCLE);
		Tile blueCircle = new Tile(Tile.BLUE, Tile.CIRCLE);
		Tile purpleCircle = new Tile(Tile.PURPLE, Tile.CIRCLE);
		Tile orangeCircle = new Tile(Tile.ORANGE, Tile.CIRCLE);
		Tile greenCircle = new Tile(Tile.GREEN, Tile.CIRCLE);

		Move move1 = new Move(redCircle, turn.getBoardCopy().getSquare(0, 1));
		Move move2 = new Move(yellowCircle, turn.getBoardCopy().getSquare(0, 2));
		Move move3 = new Move(blueCircle, turn.getBoardCopy().getSquare(0, 3));
		Move move4 = new Move(purpleCircle, turn.getBoardCopy().getSquare(0, 4));
		Move move5 = new Move(orangeCircle, turn.getBoardCopy().getSquare(0, 5));
		Move move6 = new Move(greenCircle, turn.getBoardCopy().getSquare(0, 6));

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
		turn.applyTurn();
	}

	@Test
	public void testMultipleTurnes() throws SquareOutOfBoundsException, IllegalMoveException, IllegalTurnException {

		board = new Board(game);
		turn = new Turn(board, player);

		Move move1 = new Move(new Tile(Tile.RED, Tile.CIRCLE), turn.getBoardCopy().getSquare(0, 1));
		Move move2 = new Move(new Tile(Tile.YELLOW, Tile.CIRCLE), turn.getBoardCopy().getSquare(0, 2));
		Move move3 = new Move(new Tile(Tile.BLUE, Tile.CIRCLE), turn.getBoardCopy().getSquare(0, 3));
		Move move4 = new Move(new Tile(Tile.PURPLE, Tile.CIRCLE), turn.getBoardCopy().getSquare(0, 4));
		Move move5 = new Move(new Tile(Tile.ORANGE, Tile.CIRCLE), turn.getBoardCopy().getSquare(0, 5));
		Move move6 = new Move(new Tile(Tile.GREEN, Tile.CIRCLE), turn.getBoardCopy().getSquare(0, 6));

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
		turn.applyTurn();

		turn = new Turn(board, player);

		Move move_1 = new Move(new Tile(Tile.RED, Tile.SQUARE), turn.getBoardCopy().getSquare(1, 1));
		Move move_2 = new Move(new Tile(Tile.RED, Tile.CROSS), turn.getBoardCopy().getSquare(2, 1));
		Move move_3 = new Move(new Tile(Tile.RED, Tile.DIAMOND), turn.getBoardCopy().getSquare(3, 1));
		Move move_4 = new Move(new Tile(Tile.RED, Tile.PLUS), turn.getBoardCopy().getSquare(4, 1));
		Move move_5 = new Move(new Tile(Tile.RED, Tile.STAR), turn.getBoardCopy().getSquare(5, 1));

		turn.addMove(move_1);
		assertEquals(2, turn.calculateScore());

		turn.addMove(move_2);
		assertEquals(3, turn.calculateScore());

		turn.addMove(move_3);
		assertEquals(4, turn.calculateScore());

		turn.addMove(move_4);
		assertEquals(5, turn.calculateScore());

		turn.addMove(move_5);
		assertEquals(12, turn.calculateScore());

		turn.calculateScore();
		turn.applyTurn();
		
		turn = new Turn(board, player);

		Move newMove1 = new Move(new Tile(Tile.RED, Tile.STAR), turn.getBoardCopy().getSquare(3, 2));
		turn.addMove(newMove1);
		
		System.out.println(turn.getBoardCopy());
		assertEquals(2, turn.calculateScore());
		
		turn.applyTurn();
		
		turn = new Turn(board, player);

		Move newMove2 = new Move(new Tile(Tile.GREEN, Tile.CIRCLE), turn.getBoardCopy().getSquare(1, 3));
		turn.addMove(newMove2);
		
		Move newMove3 = new Move(new Tile(Tile.PURPLE, Tile.CIRCLE), turn.getBoardCopy().getSquare(2, 3));
		turn.addMove(newMove3);
		
		Move newMove4 = new Move(new Tile(Tile.RED, Tile.CIRCLE), turn.getBoardCopy().getSquare(3, 3));
		turn.addMove(newMove4);
		
		System.out.println(turn.getBoardCopy());
		assertEquals(7, turn.calculateScore());
		
		turn.applyTurn();
		
		turn = new Turn(board, player);

		Move newMove5 = new Move(new Tile(Tile.RED, Tile.CIRCLE), turn.getBoardCopy().getSquare(4, 2));
		turn.addMove(newMove5);
		
		Move newMove6 = new Move(new Tile(Tile.RED, Tile.SQUARE), turn.getBoardCopy().getSquare(5, 2));
		turn.addMove(newMove6);
		
		System.out.println(turn.getBoardCopy());
		assertEquals(7, turn.calculateScore());
		
		turn.applyTurn();
		
		turn = new Turn(board, player);

		Move newMove7 = new Move(new Tile(Tile.YELLOW, Tile.SQUARE), turn.getBoardCopy().getSquare(-1, 2));
		turn.addMove(newMove7);
		
		Move newMove8 = new Move(new Tile(Tile.YELLOW, Tile.STAR), turn.getBoardCopy().getSquare(-2, 2));
		turn.addMove(newMove8);
		
		System.out.println(turn.getBoardCopy());
		assertEquals(3, turn.calculateScore());
		
		turn.applyTurn();
		
		turn = new Turn(board, player);

		Move newMove9 = new Move(new Tile(Tile.RED, Tile.CROSS), turn.getBoardCopy().getSquare(3, 0));
		turn.addMove(newMove9);
		
		Move newMove10 = new Move(new Tile(Tile.RED, Tile.SQUARE), turn.getBoardCopy().getSquare(3, -1));
		turn.addMove(newMove10);
		
		Move newMove11 = new Move(new Tile(Tile.RED, Tile.PLUS), turn.getBoardCopy().getSquare(3, -2));
		turn.addMove(newMove11);
		
		System.out.println(turn.getBoardCopy());
		assertEquals(12, turn.calculateScore());
		
		turn.applyTurn();
	}

}