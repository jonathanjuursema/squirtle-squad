package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import game.Hand;
import game.Player;
import game.Tile;

public class HandTest {
	public Player player;
	public Hand hand;

	@Before
	public void setUp() throws Exception {
		player = new Player("Peter", "");
	}

	@Test
	public void setUpHandTest() {
		hand = new Hand(player);
		
		assertEquals(0, hand.getAmountOfTiles());
		assertFalse(hand.hasInHand(new Tile(Tile.BLUE, Tile.CIRCLE)));
		
	}
	
	@Test
	public void addTilesTest() {
		hand = new Hand(player);
		
		List<Tile> tilesToAdd = new ArrayList<Tile>();
		
		Tile redCircle = new Tile(Tile.RED, Tile.CIRCLE);
		Tile yellowCircle = new Tile(Tile.YELLOW, Tile.CIRCLE);
		Tile blueCircle = new Tile(Tile.BLUE, Tile.CIRCLE);
		Tile purpleCircle = new Tile(Tile.PURPLE, Tile.CIRCLE);
		Tile orangeCircle = new Tile(Tile.ORANGE, Tile.CIRCLE);
		Tile greenCircle = new Tile(Tile.GREEN, Tile.CIRCLE);
		
		tilesToAdd.add(redCircle);
		tilesToAdd.add(yellowCircle);
		tilesToAdd.add(blueCircle);
		tilesToAdd.add(purpleCircle);
		tilesToAdd.add(orangeCircle);
		tilesToAdd.add(greenCircle);
		tilesToAdd.add(greenCircle);
		
		hand.addTohand(tilesToAdd);
		
		assertEquals(6, hand.getAmountOfTiles());
		assertTrue(hand.hasInHand(redCircle));
		assertTrue(hand.hasInHand(tilesToAdd));
		
		// Sorting tiles to add and checking again.
		Collections.shuffle(tilesToAdd);
		assertTrue(hand.hasInHand(tilesToAdd));
	}
	
	@Test
	public void removeTilesTest() {
		hand = new Hand(player);
		
		List<Tile> tilesToAdd = new ArrayList<Tile>();
		
		Tile redCircle = new Tile(Tile.RED, Tile.CIRCLE);
		Tile yellowCircle = new Tile(Tile.YELLOW, Tile.CIRCLE);
		Tile blueCircle = new Tile(Tile.BLUE, Tile.CIRCLE);
		Tile purpleCircle = new Tile(Tile.PURPLE, Tile.CIRCLE);
		Tile orangeCircle = new Tile(Tile.ORANGE, Tile.CIRCLE);
		Tile greenCircle = new Tile(Tile.GREEN, Tile.CIRCLE);
		
		tilesToAdd.add(redCircle);
		tilesToAdd.add(yellowCircle);
		tilesToAdd.add(blueCircle);
		tilesToAdd.add(purpleCircle);
		tilesToAdd.add(orangeCircle);
		tilesToAdd.add(greenCircle);
		
		hand.addTohand(tilesToAdd);
		
		assertEquals(6, hand.getAmountOfTiles());
		assertTrue(hand.hasInHand(redCircle));
		assertTrue(hand.hasInHand(tilesToAdd));
		
		// Sorting tiles to add and checking again.
		Collections.shuffle(tilesToAdd);
		assertTrue(hand.hasInHand(tilesToAdd));
	}

}
