package players;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.Util;
import client.Client;
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
	public List<Tile> tilesToAdd = new ArrayList<Tile>();

	@Override
	public void giveTurn() {
		
		int count = 0;
		for (Tile t : this.client.getPlayer().getHand().getTilesInHand()) {
			tilesToAdd.add(t);
			try {
				this.recursiveMove(t, t);
			} catch (QwirkleException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void recursiveMove(Tile tile, Tile startTile) throws QwirkleException {
		this.getHand().removeFromHand(tile);
		for(Tile t : this.client.getPlayer().getHand().getTilesInHand()) {
			if(this.client.getBoard().getPossiblePlaceByTile(tile).size() > 0) {
				for(BoardSquare b : this.client.getBoard().getPossiblePlaceByTile(tile)) {
					//this.possibleMoves.put(key, value)
					//this.recursiveMove(t, t);
				}
			}
		}
	}

	public void calculateScorePerMove(Tile t) {

	}

}
