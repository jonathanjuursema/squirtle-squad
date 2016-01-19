package test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import game.Board;
import game.BoardSquare;
import game.Tile;
import server.Server;

public class BoardSquareTest {
	
	Server server;
	Board board;
	
	BoardSquare bs1, bs2, bs3;
	Tile t1, t2;

	@Before
	public void setUp() throws Exception {
		
		board = new Board();
		
		t1 = new Tile(Tile.ORANGE, Tile.CROSS);
		t2 = new Tile(Tile.PURPLE, Tile.DIAMOND);

		bs1 = new BoardSquare(board, -3, 77);
		bs2 = new BoardSquare(board, -3, 78, t2);
		bs3 = new BoardSquare(board, -2, 78);
		
	}
	
	@Test
	public void testCoordinates() {
		assertEquals(-3, bs1.getX());
		assertEquals(77, bs1.getY());
		assertEquals(-3, bs2.getX());
		assertEquals(78, bs2.getY());
	}
	
	@Test
	public void testTiles() {

		assertTrue(bs1.isEmpty());
		assertFalse(bs2.isEmpty());
		assertTrue(bs3.isEmpty());
		
		assertEquals(t2, bs2.getTile());
		assertEquals(null, bs1.getTile());
		
		bs2.placeTile(t1);
		
		assertEquals(t1, bs2.getTile());
		
		bs1.removeTile();
		
		assertEquals(null, bs1.getTile());
		
		assertFalse(bs2.isEmpty());
		assertTrue(bs1.isEmpty());
		
		assertEquals(t1, bs2.getTile());

	}
	
	@Test
	public void testToString() {
		assertTrue(bs1.toString().toLowerCase().contains("empty"));
		assertTrue(bs2.toString().toLowerCase().contains("tile"));
		assertTrue(bs3.toString().toLowerCase().contains("empty"));
		assertFalse(bs1.toString().toLowerCase().contains("tile"));
		assertFalse(bs2.toString().toLowerCase().contains("empty"));
		assertFalse(bs3.toString().toLowerCase().contains("tile"));
	}

}
