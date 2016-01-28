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
		assertEquals("B5", t1.toString());
		assertEquals("O4", t2.toString());
	}
	
	@Test
	public void createAllTiles() {
		for (char i = Tile.FIRSTCOLOR; i <= Tile.LASTCOLOR; i++) {
			for (char j = Tile.FIRSTSHAPE; j <= Tile.LASTSHAPE; j++) {
				Tile t = new Tile(i,j);
				t.getColor(); 
			}
		}
	}

}
