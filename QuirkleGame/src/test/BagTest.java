package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import application.Util;
import exceptions.HandLimitReachedExeption;
import exceptions.TileNotInBagException;
import exceptions.TileNotInHandException;
import exceptions.TooFewTilesInBagException;
import exceptions.TooManyTilesInBag;
import game.Bag;
import game.Hand;
import game.Tile;
import server.Game;

public class BagTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testInitial() {
		Bag bag = new Bag();
		assertEquals(0, bag.getNumberOfTiles());
		bag.fill();
		assertEquals(108, bag.getNumberOfTiles());
	}

	@Test
	public void testAdd() throws TileNotInBagException, TooFewTilesInBagException,
					HandLimitReachedExeption {
		Bag bag = new Bag();
		bag.fill();
		Tile tile = new Tile(Tile.RED, Tile.CIRCLE);
		Tile tile1 = new Tile(Tile.RED, Tile.CIRCLE);
		Tile tile2 = new Tile(Tile.RED, Tile.CIRCLE);

		try {
			bag.addToBag(tile);
		} catch (TooManyTilesInBag e) {
			assertTrue(e.getMessage().contains("add 1 tiles to bag"));
			assertTrue(e.getMessage().contains("108/108"));
		}

		List<Tile> tilesToAdd = new ArrayList<Tile>();
		tilesToAdd.add(tile);
		tilesToAdd.add(tile1);
		tilesToAdd.add(tile2);

		Hand hand = new Hand();

		bag.takeFromBag(hand, 2);

		try {
			bag.addToBag(tilesToAdd);
		} catch (TooManyTilesInBag e) {
			assertTrue(e.getMessage().contains("add 3 tiles to bag"));
			assertTrue(e.getMessage().contains("106/108"));
		}
	}

	@Test
	public void testEmpty() throws TileNotInBagException, TooFewTilesInBagException {
		Bag bag = new Bag();
		bag.fill();
		assertEquals(108, bag.getNumberOfTiles());
		bag.empty();
		assertEquals(0, bag.getNumberOfTiles());
	}

	@Test(expected = TileNotInHandException.class)
	public void testInvalidSwap() throws TileNotInBagException, TooFewTilesInBagException,
					TooManyTilesInBag, TileNotInHandException, HandLimitReachedExeption {
		Bag bag = new Bag();
		List<Tile> tilesToAdd = new ArrayList<Tile>();

		Tile tile = new Tile(Tile.RED, Tile.CIRCLE);
		Tile tile1 = new Tile(Tile.YELLOW, Tile.CIRCLE);
		Tile tile2 = new Tile(Tile.BLUE, Tile.CIRCLE);

		tilesToAdd.add(tile);
		tilesToAdd.add(tile1);
		tilesToAdd.add(tile2);

		bag.addToBag(tilesToAdd);

		Hand hand = new Hand();

		bag.swapTiles(hand, tilesToAdd);
	}

	@Test
	public void testValidSwap() throws TooFewTilesInBagException, TileNotInBagException,
					TooManyTilesInBag, TileNotInHandException, HandLimitReachedExeption {
		Bag bag = new Bag();
		bag.fill();

		Hand hand = new Hand();
		bag.takeFromBag(hand, 6);

		for (int i = 0; i < 100; i++) {

			List<Tile> handList = new ArrayList<Tile>();
			handList.addAll(hand.getTilesInHand());

			bag.swapTiles(hand, hand.getTilesInHand());

			for (Tile t : hand.getTilesInHand()) {
				assertFalse(handList.contains(t));
			}

			assertEquals(102, bag.getNumberOfTiles());

		}
	}

	@Test
	public void testEmptyBag() throws TooFewTilesInBagException, TileNotInBagException,
					TooManyTilesInBag, TileNotInHandException, HandLimitReachedExeption {
		Bag bag = new Bag();
		bag.fill();

		Hand hand = new Hand();
		for (int i = 0; i < Game.DIFFERENTCOLORS * Game.DIFFERENTSHAPES * Game.TILESPERTYPE; i++) {
			bag.takeFromBag(hand, 1);
			hand.removeFromHand(hand.getTilesInHand());
		}

		assertTrue(bag.getNumberOfTiles() == 0);
	}

	@Test(expected = TooFewTilesInBagException.class)
	public void swapMoreThenInBagTest() throws TooManyTilesInBag, HandLimitReachedExeption,
					TooFewTilesInBagException, TileNotInBagException, TileNotInHandException {
		Bag bag = new Bag();

		Tile tile = new Tile(Tile.RED, Tile.CIRCLE);
		Tile tile1 = new Tile(Tile.YELLOW, Tile.CIRCLE);
		Tile tile2 = new Tile(Tile.BLUE, Tile.CIRCLE);

		bag.addToBag(tile);

		Hand hand = new Hand();
		hand.addToHand(tile1);
		hand.addToHand(tile2);

		bag.swapTiles(hand, hand.getTilesInHand());

		assertEquals(1, bag.getNumberOfTiles());
	}

	@Test(expected = TooFewTilesInBagException.class)
	public void takeMoreThenInBagTest() throws TooFewTilesInBagException, TileNotInBagException,
					HandLimitReachedExeption {
		Bag bag = new Bag();
		Hand hand = new Hand();
		bag.takeFromBag(hand, 6);

		assertEquals(0, bag.getNumberOfTiles());
		assertEquals(0, hand.getTilesInHand());
	}

	public void toStringTest() {
		Bag bag = new Bag();
		bag.fill();

		String returnMessage = bag.toString();
		assertTrue(returnMessage.contains("" + bag.getNumberOfTiles()));
	}
}
