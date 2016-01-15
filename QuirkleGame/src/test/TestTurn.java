package test;

// TODO Convert this to a decent JUnit test. :)
import exceptions.SquareOutOfBoundsException;
import exceptions.TileNotInBagException;
import exceptions.TooFewTilesInBagException;
import game.Bag;
import game.Board;
import game.BoardSquare;
import game.Game;
import game.Hand;
import game.Move;
import game.Player;
import game.Tile;
import game.Turn;
import networking.ConnectionHandler;
import networking.DummyHandler;

public class TestTurn {

	public static void main(String[] args) throws TooFewTilesInBagException, TileNotInBagException, SquareOutOfBoundsException {
		// TODO Auto-generated method stub
		ConnectionHandler connectionHandler = new DummyHandler();
		Game game = new Game(connectionHandler);
		Board board = new Board(game);
		
		// dit wou ik toevoegen System.out.println(board.length);
		
		Bag bag = new Bag(game);
		bag.fill();
		Player player = new Player("Peter", "");
		Turn turn = new Turn(player, board);
		
		Tile tile1 = new Tile(Tile.RED,Tile.CIRCLE);
		Tile tile2 = new Tile(Tile.RED,Tile.CIRCLE);
		Tile tile3 = new Tile(Tile.RED,Tile.CIRCLE);
		Tile tile4 = new Tile(Tile.RED,Tile.CIRCLE);

		BoardSquare square1 = board.getSquare(0, 1);
		BoardSquare square2 = board.getSquare(0, 2);
		BoardSquare square3 = board.getSquare(1, 1);
		BoardSquare square4 = board.getSquare(1, 2);
		
		board.placeTile(tile3, square3.getX(), square3.getY());
		board.placeTile(tile4, square4.getX(), square4.getY());

		Move move1 = new Move(tile1, square1);
		Move move2 = new Move(tile2, square2);
		
		turn.addMove(move1);
		turn.addMove(move2);
		
		System.out.println(turn.toString());
		
		System.out.println(turn.getScore());
	}

}

