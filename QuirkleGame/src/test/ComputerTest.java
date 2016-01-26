package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import application.Util;
import exceptions.IllegalMoveException;
import exceptions.IllegalTurnException;
import exceptions.QwirkleException;
import exceptions.SquareOutOfBoundsException;
import game.Board;
import game.BoardSquare;
import game.Move;
import game.Tile;
import game.Turn;
import players.Player;

public class ComputerTest {
	Player player;
	Board board;
	
	public ComputerTest(Player player, Board board) {
		this.player = player;
		this.board = board;
	}
	
	public List<Turn> possibleTurns = new ArrayList<Turn>();
	public Map<Integer, List<Move>> turnsByScore = new HashMap<Integer, List<Move>>();
	public List<Move> tilesToAdd = new ArrayList<Move>();
	
	public void calculateMoves() throws SquareOutOfBoundsException {
		for (Tile t : player.getHand().getTilesInHand()) {
			
			//Util.log("debug", "Selected first tile from hand: " + t);
			//Util.log("debug", "Possible places for " + t + ": "+ board.getPossiblePlaceByTile(t));
			
			Turn simTurn = new Turn(board, player);
			
				if (board.getPossiblePlaceByTile(t).size() > 0) {
					//Util.log("debug", "Possible places ("+ board.getPossiblePlaceByTile(t).size() + ") found for " + t);
					
					for (BoardSquare b : board.getPossiblePlaceByTile(t)) {
						try {
							board.placeTile(t, b.getX(), b.getY());
						} catch (SquareOutOfBoundsException e1) {
							Util.log(e1);
						}
						//Util.log("debug", "Possible place for " + t + " is " + b);
						Move move = new Move(t, b);
						try {
							simTurn.addMove(move);
						} catch (IllegalMoveException | IllegalTurnException e) {
							//Util.log(e);
							continue;
						}
						
						board.placeTile(move.getTile(), move.getPosition().getX(), move.getPosition().getY());
						recursiveMove(t, simTurn);
						
						board.removeTile(b.getX(), b.getY());
						//Util.log("debug", " Next tile in hand ");
						simTurn.removeMove(move);
					}
				} else {
					//Util.log("debug", "No possible places found for tile " + t);
				}
		
			//Util.log("debug", t + " has been fully checked, up to the next tile \n");
		}

	}

	public void recursiveMove(Tile tile, Turn turn) throws SquareOutOfBoundsException {
				
		//Util.log(board.toString());
		String indent = "";
		
		for(Move m : turn.getMoves()) {
			indent += "   ";
		}
		
		//Util.log("debug", "Enter recursiveMove function");
		for (Tile t : player.getHand().getTilesInHand()) {
			
			//Util.log("debug", indent + "Trying to use " + t + " to place");
			
			boolean ignore = false;
			for(Move m : turn.getMoves()) {
				if(m.getTile() == t) {
					ignore = true;
				}
			}
			
			if(ignore) {
				//Util.log("debug", indent + t.toString() + " is allready into previous moves so will be ignored.");
				continue;
			}
			
			//Util.log("debug", indent + t.toString() + " is not used yet, so we gonna try and use it.");
			//Util.log("debug", indent + t.toString() + " is tried on the board with the following previous moves in mind: " + turn.getMoves().toString());

				//Util.log("debug", "Possible places for " + t + ": "+ board.getPossiblePlaceByTile(t,turn.getMoves()));
				
				if (board.getPossiblePlaceByTile(t,turn.getMoves()).size() > 0) {
					//Util.log("debug", "New board: " + board.toString());
					
					//Util.log("debug", indent + "("+ board.getPossiblePlaceByTile(t,turn.getMoves()).size() + ") places are possible.");
					
					for (BoardSquare b : board.getPossiblePlaceByTile(t,turn.getMoves())) {
						//Util.log("debug", "Possible place for " + t + " is " + b);
						Move move = new Move(t, b);
						try {
							turn.addMove(move);
						} catch (IllegalMoveException | IllegalTurnException e) {
							//Util.log(e);
							continue;
						}
						
						board.placeTile(move.getTile(), move.getPosition().getX(), move.getPosition().getY());
						recursiveMove(t, turn);
						board.removeTile(b.getX(), b.getY());
						List<Move> copy = new ArrayList<Move>();
						for(Move m : turn.getMoves()) {
							copy.add(new Move(m.getTile(),m.getPosition()));
						}
						//Util.log("debug", "Score per turn: " + turn.calculateScore());
						//Util.log("debug", "Moves per turn: " + turn.getMoves());
						try {
							turnsByScore.put(turn.calculateScore(), copy);
						} catch (IllegalMoveException e) {
							continue;
						}
						turn.removeMove(move);
						
						//Util.log("debug", " Sequence found now: " + tilesToAdd);
						//Util.log("debug", " Back to previous function");
					}
					
					//Util.log("debug", indent + "All possible places are tried" + t);
				} else {
					//Util.log("debug", indent + "No places found for " + t);
				}

			
		}
		//Util.log("debug", "Leave recursiveMove function \n");
	}

}
