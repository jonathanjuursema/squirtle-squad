package test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import exceptions.IllegalMoveException;
import exceptions.SquareOutOfBoundsException;
import game.Board;
import game.Game;
import game.Move;
import game.Player;
import game.Tile;
import game.Turn;
import server.Server;

public class TestMove {

	Server server;
	Game game;
	Turn turn;
	
	@Before
	public void setUp() throws Exception {
		server = new Server();
		game = new Game(server);
		Player player = new Player("Peter", "");
		game.addPlayer(player);
		Board board = new Board(game);
		turn = new Turn(board, game);
	}
	
	@Test
	public void testValidMove() throws SquareOutOfBoundsException, IllegalMoveException {
		Tile redCircle = new Tile(Tile.RED,Tile.CIRCLE);
		Tile yellowCircle = new Tile(Tile.YELLOW,Tile.CIRCLE);
		Tile blueCircle = new Tile(Tile.BLUE,Tile.CIRCLE);
		Tile purpleCircle = new Tile(Tile.PURPLE,Tile.CIRCLE);
		
		Move move1 = new Move(redCircle, turn.getBoardCopy().getSquare(0,1));
		Move move2 = new Move(yellowCircle, turn.getBoardCopy().getSquare(0,2));
		Move move3 = new Move(blueCircle, turn.getBoardCopy().getSquare(0,3));
		Move move4 = new Move(purpleCircle, turn.getBoardCopy().getSquare(0,4));
		
		assertTrue(move1.isValidMove(turn.getBoardCopy()));
		turn.addMove(move1);
		
		assertTrue(move2.isValidMove(turn.getBoardCopy()));
		turn.addMove(move2);
		
		assertTrue(move3.isValidMove(turn.getBoardCopy()));
		turn.addMove(move3);
		
		assertTrue(move4.isValidMove(turn.getBoardCopy()));
		turn.addMove(move4);
		
		System.out.println(turn.getBoardCopy());
	}
	
	@Test
	public void testUnValidMove() throws SquareOutOfBoundsException, IllegalMoveException {
		Tile redCircle = new Tile(Tile.RED,Tile.CIRCLE);
		Tile yellowCircle = new Tile(Tile.YELLOW,Tile.CIRCLE);
		Tile blueCircle = new Tile(Tile.BLUE,Tile.CIRCLE);
		Tile purpleSquare = new Tile(Tile.PURPLE,Tile.SQUARE);
		
		Move column1 = new Move(redCircle, turn.getBoardCopy().getSquare(0,1));
		Move column2 = new Move(yellowCircle, turn.getBoardCopy().getSquare(0,2));
		Move column3 = new Move(blueCircle, turn.getBoardCopy().getSquare(0,3));
		Move column4 = new Move(purpleSquare, turn.getBoardCopy().getSquare(0,4));
		Move row1 = new Move(purpleSquare, turn.getBoardCopy().getSquare(1,1));
		Move row2 = new Move(purpleSquare, turn.getBoardCopy().getSquare(1,2));
		
		//System.out.println(this.board);
		assertTrue(column1.isValidMove(turn.getBoardCopy()));
		turn.addMove(column1);
		
		assertTrue(column2.isValidMove(turn.getBoardCopy()));
		turn.addMove(column2);
		
		assertTrue(column3.isValidMove(turn.getBoardCopy()));
		turn.addMove(column3);
		
		assertFalse(column4.isValidMove(turn.getBoardCopy()));
		
		assertFalse(row1.isValidMove(turn.getBoardCopy()));
		
		assertFalse(row2.isValidMove(turn.getBoardCopy()));
		
		System.out.println(turn.getBoardCopy());
	
	}

}
