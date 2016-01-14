package test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import server.*;
import game.*;
import client.*;
import exceptions.*;

public class BoardTest {
	
	Server server;
	Game game;
	Board board;

	@Before
	public void setUp() throws Exception {
		server = new Server();
		game = new Game(server);
		board = new Board(game);
	}

	@Test
	public void testGetSquare() throws SquareOutOfBoundsException {
		assertEquals(2, board.getSquare(2, 3).getX());
		assertEquals(3, board.getSquare(2, 3).getY());
		assertEquals(0, board.getSquare(0, -75).getX());
		assertEquals(-75, board.getSquare(0, -75).getY());
	}

	@Test
	public void testNeighbourBoardSquare() throws SquareOutOfBoundsException {
		assertEquals(board.getSquare(0, 6), board.getSquare(0, 5).getNeighbour(BoardSquare.NORTH));
		assertEquals(board.getSquare(7, 3), board.getSquare(6, 3).getNeighbour(BoardSquare.EAST));
		assertEquals(board.getSquare(2, -1), board.getSquare(2, 0).getNeighbour(BoardSquare.SOUTH));
		assertEquals(board.getSquare(-5, -7), board.getSquare(-4, -7).getNeighbour(BoardSquare.WEST));
	}
	
	@Test
	public void testTilePlacement() throws SquareOutOfBoundsException {
		assertTrue(board.getSquare(2, 3).isEmpty());
		
		Tile t1 = new Tile(Tile.BLUE, Tile.CIRCLE);
		board.placeTile(t1, 2, 3);
		
		assertFalse(board.getSquare(2, 3).isEmpty());
		assertEquals(t1, board.getSquare(2, 3).getTile());
		assertEquals(t1, board.getTile(2, 3));
		
		board.removeTile(2, 3);
		assertTrue(board.getSquare(2, 3).isEmpty());
	}
	
	@Test
	public void testBoardCopy() {
		// TODO Implement;
	}
	
	@Test
	public void testGeneral() {
		assertEquals(game, board.getGame());
	}

}
