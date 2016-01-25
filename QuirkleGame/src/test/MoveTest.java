package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import application.Util;
import exceptions.IllegalMoveException;
import exceptions.IllegalTurnException;
import exceptions.SquareOutOfBoundsException;
import game.Board;
import game.BoardSquare;
import game.Move;
import game.Tile;
import game.Turn;
import players.ClientPlayer;
import players.DummyPlayer;
import players.HumanPlayer;
import players.Player;

public class MoveTest {

	DummyPlayer player;
	Board board;
	Turn turn;

	@Before
	public void setUp() throws Exception {
		this.board = new Board();
	}

	@Test
	public void intializeTest() throws SquareOutOfBoundsException {
		this.turn = new Turn(board, player);

		Tile redCircle = new Tile(Tile.RED, Tile.CIRCLE);
		Tile yellowCircle = new Tile(Tile.YELLOW, Tile.CIRCLE);
		Tile blueCircle = new Tile(Tile.BLUE, Tile.CIRCLE);
		Tile purpleCircle = new Tile(Tile.PURPLE, Tile.CIRCLE);
		Tile orangeCircle = new Tile(Tile.ORANGE, Tile.CIRCLE);
		Tile greenCircle = new Tile(Tile.GREEN, Tile.CIRCLE);
		Move move1 = new Move(redCircle, turn.getBoardCopy().getSquare(0, 0));
		Move move2 = new Move(yellowCircle, turn.getBoardCopy().getSquare(1, 0));
		Move move3 = new Move(blueCircle, turn.getBoardCopy().getSquare(2, 0));
		Move move4 = new Move(purpleCircle, turn.getBoardCopy().getSquare(3, 0));
		Move move5 = new Move(orangeCircle, turn.getBoardCopy().getSquare(4, 0));
		Move move6 = new Move(greenCircle, turn.getBoardCopy().getSquare(5, 0));

		try {
			turn.addMove(move1);
			turn.addMove(move2);
			turn.addMove(move3);
			turn.addMove(move4);
			turn.addMove(move5);
			turn.addMove(move6);

		} catch (IllegalMoveException | IllegalTurnException e) {
			e.printStackTrace();
		}

		for (Move m : turn.getMoves()) {
			turn.getBoardCopy().placeTile(m.getTile(), m.getPosition().getX(), m.getPosition().getY());
		}

		assertEquals(12, turn.getBoardCopy().getAllPossiblePlaces().size());
		turn.getMoves().clear();
		System.out.println(turn.getBoardCopy().toString());
		System.out.println(turn.getBoardCopy().getAllPossiblePlaces().toString());

	}

	@Test
	public void perMoveTest() throws SquareOutOfBoundsException {
		this.turn = new Turn(board, player);

		Tile redSQUARE = new Tile(Tile.RED, Tile.SQUARE);
		Tile redDiamond = new Tile(Tile.RED, Tile.DIAMOND);
		Tile redStar = new Tile(Tile.RED, Tile.STAR);

		Move move7 = new Move(redSQUARE, turn.getBoardCopy().getSquare(0, 0));
		Move move8 = new Move(redDiamond, turn.getBoardCopy().getSquare(0, 1));
		Move move9 = new Move(redStar, turn.getBoardCopy().getSquare(0, 2));

		try {
			turn.addMove(move7);
			turn.addMove(move8);
			turn.addMove(move9);

		} catch (IllegalMoveException | IllegalTurnException e) {
			e.printStackTrace();
		}

		for (Move m : turn.getMoves()) {
			turn.getBoardCopy().placeTile(m.getTile(), m.getPosition().getX(), m.getPosition().getY());
		}

		List<BoardSquare> selected = new ArrayList<BoardSquare>();
		selected.add(turn.getBoardCopy().getSquare(0, 2));
		selected.add(turn.getBoardCopy().getSquare(0, 1));

		turn.getMoves().clear();

		Move move10 = new Move(new Tile(Tile.RED, Tile.DIAMOND), turn.getBoardCopy().getSquare(1, 0));
		Move move11 = new Move(new Tile(Tile.RED, Tile.CIRCLE), turn.getBoardCopy().getSquare(2, 0));

		try {
			Util.log("special debug",new Tile(Tile.RED, Tile.DIAMOND) + "op " + turn.getBoardCopy().getPossiblePlaceByTile(new Tile(Tile.RED, Tile.DIAMOND),turn.getMoves()).toString());

			turn.addMove(move10);
			turn.addMove(move11);
		} catch (IllegalMoveException | IllegalTurnException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Util.log("special debug",new Tile(Tile.BLUE, Tile.CIRCLE) + "op " + turn.getBoardCopy().getPossiblePlaceByTile(new Tile(Tile.BLUE, Tile.CIRCLE),turn.getMoves()).toString());

		System.out.println(turn.getBoardCopy().toString());

	}

}
