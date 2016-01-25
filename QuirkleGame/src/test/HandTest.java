package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import exceptions.HandLimitReachedExeption;
import exceptions.TileNotInHandException;
import game.Hand;
import game.Tile;
import players.ClientPlayer;
import players.HumanPlayer;
import players.Player;

public class HandTest {
	public ClientPlayer player;
	public Hand hand;

	@Before
	public void setUp() throws Exception {
		player = new HumanPlayer("Peter", null);
	}

	@Test
	public void setUpHandTest() {
		hand = new Hand();

		assertEquals(0, hand.getAmountOfTiles());
		assertFalse(hand.hasInHand(new Tile(Tile.BLUE, Tile.CIRCLE)));

	}

	@Test(expected = HandLimitReachedExeption.class)
	public void addTilesTest() throws HandLimitReachedExeption {
		hand = new Hand();

		List<Tile> tilesToAdd = new ArrayList<Tile>();

		Tile redCircle = new Tile(Tile.RED, Tile.CIRCLE);
		Tile yellowCircle = new Tile(Tile.YELLOW, Tile.CIRCLE);
		Tile blueCircle = new Tile(Tile.BLUE, Tile.CIRCLE);
		Tile purpleCircle = new Tile(Tile.PURPLE, Tile.CIRCLE);
		Tile orangeCircle = new Tile(Tile.ORANGE, Tile.CIRCLE);
		Tile greenCircle = new Tile(Tile.GREEN, Tile.CIRCLE);

		Tile dummyTile = new Tile(Tile.PURPLE, Tile.SQUARE);

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

		List<Tile> tilesNotAllInHand = new ArrayList<Tile>();
		tilesNotAllInHand.add(orangeCircle);
		tilesNotAllInHand.add(greenCircle);
		tilesNotAllInHand.add(greenCircle);
		tilesNotAllInHand.add(dummyTile);

		assertFalse(hand.hasInHand(tilesNotAllInHand));

		// Sorting tiles to add and checking again.
		Collections.shuffle(tilesToAdd);
		assertTrue(hand.hasInHand(tilesToAdd));
	}

	@Test
	public void removeTilesTest() throws HandLimitReachedExeption, TileNotInHandException {
		hand = new Hand();

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

		hand.addTohand(tilesToAdd);

		assertTrue(hand.hasInHand(redCircle));
		assertTrue(hand.hasInHand(tilesToAdd));
		assertEquals(3, hand.getAmountOfTiles());

		hand.removeFromHand(redCircle);
		tilesToAdd.remove(redCircle);
		assertEquals(2, hand.getAmountOfTiles());
		assertFalse(hand.hasInHand(redCircle));

		hand.removeFromHand(tilesToAdd);
		assertEquals(0, hand.getAmountOfTiles());
	}

	@Test
	public void hardResetTest() throws HandLimitReachedExeption {
		hand = new Hand();

		List<Tile> tilesToAdd = new ArrayList<Tile>();

		Tile redCircle = new Tile(Tile.RED, Tile.CIRCLE);
		Tile yellowCircle = new Tile(Tile.YELLOW, Tile.CIRCLE);
		Tile blueCircle = new Tile(Tile.BLUE, Tile.CIRCLE);

		tilesToAdd.add(redCircle);
		tilesToAdd.add(yellowCircle);
		tilesToAdd.add(blueCircle);

		hand.addTohand(tilesToAdd);

		List<Tile> tilesFromHand = hand.hardResetHand();

		for (Tile t : tilesToAdd) {
			assertTrue(tilesFromHand.contains(t));
		}
	}

	@Test
	public void checkToString() throws HandLimitReachedExeption {
		hand = new Hand();

		List<Tile> tilesToAdd = new ArrayList<Tile>();

		Tile redCircle = new Tile(Tile.RED, Tile.CIRCLE);
		Tile yellowCircle = new Tile(Tile.YELLOW, Tile.SQUARE);
		Tile blueCircle = new Tile(Tile.BLUE, Tile.STAR);
		Tile purpleCircle = new Tile(Tile.PURPLE, Tile.CIRCLE);
		Tile orangeCircle = new Tile(Tile.ORANGE, Tile.PLUS);
		Tile greenCircle = new Tile(Tile.GREEN, Tile.DIAMOND);

		tilesToAdd.add(redCircle);
		tilesToAdd.add(yellowCircle);
		tilesToAdd.add(blueCircle);
		tilesToAdd.add(purpleCircle);
		tilesToAdd.add(orangeCircle);
		tilesToAdd.add(greenCircle);

		hand.addTohand(tilesToAdd);

		assertTrue(hand.toString().contains("(6)"));

	}
}
