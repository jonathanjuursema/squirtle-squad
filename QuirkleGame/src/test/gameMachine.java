package test;

import exceptions.IllegalMoveException;
import exceptions.IllegalTurnException;
import exceptions.SquareOutOfBoundsException;
import game.Board;
import game.Game;
import game.Move;
import game.Player;
import game.Tile;
import game.Turn;
import server.Server;

public class gameMachine {
	public Server server;
	public Game game;
	public Turn turn;
	
	public gameMachine() throws SquareOutOfBoundsException, IllegalTurnException {
		
		// Setting up server
		server = new Server();
		
		// Setting up game
		game = new DummyGame();
		
		// create new board
		Board board = new Board(game);
		
		// Create players
		Player Peter = new Player("Peter", "");
		Player Jonathan = new Player("Jonathan", "");
		Player Lavalamp = new Player("Lavalamp", "");
		
		// Add players to game
		game.addPlayer(Peter);
		game.addPlayer(Jonathan);
		game.addPlayer(Lavalamp);
		
		// create initial turn
		this.turn = new Turn(board, Lavalamp);
		
		// TODO: get Tiles from hand
		Tile redCircle = new Tile(Tile.RED,Tile.CIRCLE);
		Tile yellowCircle = new Tile(Tile.YELLOW,Tile.CIRCLE);
		Tile blueCircle = new Tile(Tile.BLUE,Tile.CIRCLE);
		
		// make moves -> input by user
		Move move1 = new Move(redCircle, turn.getBoardCopy().getSquare(0,1));
		Move move2 = new Move(yellowCircle, turn.getBoardCopy().getSquare(0,2));
		Move move3 = new Move(blueCircle, turn.getBoardCopy().getSquare(0,3));

		// Add moves to board
		try {
			turn.addMove(move1);
		} catch (IllegalMoveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Add moves to board
		try {
			turn.addMove(move2);
		} catch (IllegalMoveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Add moves to board
		try {
			turn.addMove(move3);
		} catch (IllegalMoveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		turn.applyTurn();
		
	}
	
	public static void main(String[] args) {
	}

}
