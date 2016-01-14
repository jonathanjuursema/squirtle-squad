package test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import game.Tile;

public class TileTest {
	
	Tile t1, t2;

	@Before
	public void setUp() throws Exception {
		t1 = new Tile(Tile.BLUE, Tile.DIAMOND);
		t2 = new Tile(Tile.ORANGE, Tile.PLUS);
	}

	@Test
	public void testTile() {
		assertEquals(Tile.BLUE, t1.getColor());
		assertEquals(Tile.ORANGE, t2.getColor());
		assertEquals(Tile.PLUS, t2.getShape());
		assertEquals(Tile.DIAMOND, t1.getShape());
	}
	
	@Test
	public void testToString() {
		assertEquals("[BLUE DIAMOND]", t1.toString());
		assertEquals("[ORANGE PLUS]", t2.toString());
	}

}
