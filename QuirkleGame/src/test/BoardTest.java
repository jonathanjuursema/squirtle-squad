package test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import server.*;
import game.*;
import exceptions.*;

public class BoardTest {

	Server server;
	Game game;
	Board board;

	@Before
	public void setUp() throws Exception {
		board = new Board();
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
		assertEquals(null, board.getSquare(0, 5).getNeighbour(6));

		assertEquals(board.getSquare(0, 6), board.getSquare(0, 5).getNeighbour(BoardSquare.NORTH));
		assertEquals(board.getSquare(7, 3), board.getSquare(6, 3).getNeighbour(BoardSquare.EAST));
		assertEquals(board.getSquare(2, -1), board.getSquare(2, 0).getNeighbour(BoardSquare.SOUTH));
		assertEquals(board.getSquare(-5, -7),
						board.getSquare(-4, -7).getNeighbour(BoardSquare.WEST));

		assertEquals(board.getSquare(3, 4), board.getSquare(3, 4).getNeighbour(BoardSquare.NORTH)
						.getNeighbour(BoardSquare.SOUTH));
		assertEquals(board.getSquare(7, 3), board.getSquare(7, 3).getNeighbour(BoardSquare.SOUTH)
						.getNeighbour(BoardSquare.NORTH));
		assertEquals(board.getSquare(-8, 3), board.getSquare(-8, 3).getNeighbour(BoardSquare.EAST)
						.getNeighbour(BoardSquare.WEST));
		assertEquals(board.getSquare(0, 0), board.getSquare(0, 0).getNeighbour(BoardSquare.WEST)
						.getNeighbour(BoardSquare.EAST));
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
	public void testToString() throws SquareOutOfBoundsException {
		board.placeTile(new Tile(Tile.BLUE, Tile.CIRCLE), 0, 0);
		board.placeTile(new Tile(Tile.RED, Tile.SQUARE), 0, 1);
		board.placeTile(new Tile(Tile.ORANGE, Tile.DIAMOND), -1, 0);
		board.placeTile(new Tile(Tile.PURPLE, Tile.STAR), 3, 3);
		board.placeTile(new Tile(Tile.GREEN, Tile.CROSS), -2, 4);

		System.out.println(board.toString());
	}

	@Test
	public void testBoardCopy() throws SquareOutOfBoundsException {
		board.placeTile(new Tile(Tile.BLUE, Tile.CIRCLE), 0, 0);
		board.placeTile(new Tile(Tile.RED, Tile.SQUARE), 0, 1);
		board.placeTile(new Tile(Tile.ORANGE, Tile.DIAMOND), -1, 0);
		board.placeTile(new Tile(Tile.PURPLE, Tile.STAR), 3, 3);
		board.placeTile(new Tile(Tile.GREEN, Tile.CROSS), -2, 4);

		System.out.println(board.toString());

		Board board2 = new Board();
		board2.setBoard(board.copy(board2));

		System.out.println(board2.toString());
	}

	@Test
	public void testPossiblePlayes() throws SquareOutOfBoundsException {
		board.placeTile(new Tile(Tile.BLUE, Tile.CIRCLE), 0, 0);
		board.placeTile(new Tile(Tile.RED, Tile.SQUARE), 0, -1);
		board.placeTile(new Tile(Tile.ORANGE, Tile.DIAMOND), 0, -2);
		board.placeTile(new Tile(Tile.PURPLE, Tile.STAR), 0, -3);
		board.placeTile(new Tile(Tile.GREEN, Tile.CROSS), 0, -4);
		board.placeTile(new Tile(Tile.ORANGE, Tile.PLUS), 0, -5);

		assertEquals(12, board.getAllPossiblePlaces().size());
	}

}
