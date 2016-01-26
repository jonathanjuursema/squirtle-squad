package players;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import application.Util;
import client.Client;
import exceptions.IllegalMoveException;
import exceptions.IllegalTurnException;
import exceptions.QwirkleException;
import exceptions.SquareOutOfBoundsException;
import game.BoardSquare;
import game.Move;
import game.Tile;
import game.Turn;

public class ComputerPlayer extends ClientPlayer {

	public ComputerPlayer(Client client, String name) {
		super(client, name);
	}

	public Map<Tile, List<Move>> possibleMoves = new HashMap<Tile, List<Move>>();
	public Map<Integer, List<Move>> movesByScore = new HashMap<Integer, List<Move>>();
	public List<Move> tilesToAdd = new ArrayList<Move>();

	@Override
	public void giveTurn() {

		this.calculateMoves();
		
		
	}
	
	public void calculateMoves() {
		for (Tile t : this.client.getPlayer().getHand().getTilesInHand()) {
			try {
				if (this.client.getBoard().getPossiblePlaceByTile(t).size() > 0) {
					for (BoardSquare b : this.client.getBoard().getPossiblePlaceByTile(t)) {
						this.client.getPlayer().getHand().removeFromHand(t);
						Move move = new Move(t, b);
						tilesToAdd.add(move);
						recursiveMove(t, tilesToAdd);
						possibleMoves.put(tilesToAdd.get(0).getTile(), tilesToAdd);
						this.client.getPlayer().getHand().addToHand(t);
					}
				}
			} catch (QwirkleException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		for(Entry<Tile, List<Move>> entry : possibleMoves.entrySet()) {
			Turn simulatedTurn = new Turn(client.getBoard(), client.getPlayer());
			for(Move m : entry.getValue()){
				try {
					simulatedTurn.addMove(m);
				} catch (SquareOutOfBoundsException | IllegalMoveException | IllegalTurnException e) {
					Util.log(e);
					break;
				}
			}
			try {
				movesByScore.put(simulatedTurn.calculateScore(), entry.getValue());
			} catch (SquareOutOfBoundsException e) {
				Util.log(e);
				continue;
			}
		}
		
		Util.log(possibleMoves.toString());
		Util.log(movesByScore.toString());
	}

	public void recursiveMove(Tile tile, List<Move> previousMoves) throws QwirkleException {
		for (Tile t : this.client.getPlayer().getHand().getTilesInHand()) {
			try {
				if (this.client.getBoard().getPossiblePlaceByTile(t,previousMoves).size() > 0) {
					for (BoardSquare b : this.client.getBoard().getPossiblePlaceByTile(t)) {
						this.client.getPlayer().getHand().removeFromHand(t);
						Move move = new Move(t, b);
						tilesToAdd.add(move);
						recursiveMove(t, tilesToAdd);
						possibleMoves.put(tilesToAdd.get(0).getTile(), tilesToAdd);
						this.client.getPlayer().getHand().addToHand(t);
					}

				}
			} catch (QwirkleException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void calculateScorePerMove(Tile t) {

	}

}
