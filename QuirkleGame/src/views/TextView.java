package views;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;

import application.Util;
import client.Client;
import exceptions.HandLimitReachedExeption;
import exceptions.IllegalTurnException;
import exceptions.TileNotInHandException;
import game.Board;
import game.Hand;
import game.Tile;
import game.Turn;

public class TextView extends Thread implements View {
	private Client client;

	public TextView(Client client) {
		this.client = client;
		this.start();
	}

	public void run() {

		boolean running = true;
		while (running) {
			if (this.client.getStatus() == Client.Status.INITIALIZING) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					Util.log(e);
				}
			} else {
				String[] input = Util.readString("").split(" ");
				String command = input[0];
				String[] args = Arrays.copyOfRange(input, 1, input.length);
				switch (command) {
				case "help":
					this.sendNotification(
									"game <int>: request the server for a game with a certain amount of players");
					this.sendNotification("chat <msg>: send a chat message");
					break;
				case "game":
					if (args.length < 1) {
						this.sendNotification("error", "Usage: game <int>");
					} else {
						this.client.requestGame(Integer.parseInt(args[0]));
					}
					break;
				case "chat":
					if (args.length < 1) {
						this.sendNotification("error", "Usage: chat <msg>");
					} else {
						this.client.chatFromClient(args);
					}
					break;
				default:
					this.sendNotification("Invalid command. Please type 'help' for a list.");
					break;
				}
			}
		}

	}

	@Override
	public void update(Observable o, Object arg) {

	}

	public void sendNotification(String message) {
		Util.println(message);
	}

	public void sendNotification(String type, String message) {
		this.sendNotification("[" + type + "]: " + message);
	}

	public synchronized InetAddress askForHost() {

		InetAddress host = null;

		while (host == null) {
			try {
				host = InetAddress
								.getByName(Util.readString("What hostname should we connect to?"
												+ System.lineSeparator() + "> "));
			} catch (UnknownHostException e) {
				this.sendNotification("This hostname cannot be resolved.");
				Util.log(e);
			}
		}

		return host;

	}

	public synchronized int askForPort() {

		int port = 0;

		while (port < 2000 || port > 3000) {
			port = Util.readInt("What port should we connect to? (2000..3000)"
							+ System.lineSeparator() + "> ");
		}

		return port;

	}

	public synchronized String requestNickname() {
		return Util.readString(
						"What nickname would you like to use?" + System.lineSeparator() + "> ");
	}

	public synchronized String askForPlayerType() {
		return Util.readString("What type of player would you like to be? (human, computer)"
						+ System.lineSeparator() + "> ");
	}

	@Override
	public synchronized Turn requestMoves(Turn turn) {

		Hand hand = this.client.getPlayerHand();
		Board board = new Board();
		board.setBoard(this.client.getBoard().copy(board));

		List<Tile> removedFromHand = new ArrayList<Tile>();

		boolean turndone = false;

		while (!turndone) {

			Util.println(this.client.getBoard().toString());
			Util.println(this.client.getPlayerHand().toString());

			while (true) {
				String command = Util
								.readString("What would you like to do? Type 'help' for a list of possibilities."
												+ System.lineSeparator() + "> ");
				String[] args = command.split(" ");
				switch (args[0]) {

				case "swap":

					if (args.length < 2) {
						Util.println("usage: swap <tileno>");
						break;
					}

					int tileInt = Integer.parseInt(args[1]);
					if (tileInt > hand.getTilesInHand().size()) {
						Util.println("There are not so many tiles in your hand.");
						break;
					}

					Tile t = hand.getTilesInHand().get(tileInt);
					
					try {
						turn.addSwapRequest(t);
					} catch (IllegalTurnException e) {
						Util.log(e);
						Util.println("Invalid move: " + e.getMessage());
						break;
					}
					
					try {
						hand.removeFromHand(t);
					} catch (TileNotInHandException e) {
						Util.log(e);
						Util.println("Could not remove tile.");
						break;
					}
					
					removedFromHand.add(t);
					
					Util.println(this.client.getPlayerHand().toString());
					
					break;

				case "move":

					if (args.length < 4) {
						Util.println("usage: move <tileno> <x> <y>");
						break;
					}
					
					break;
					
				case "apply":
					
					return turn;

				case "help":

					Util.println("move <tileno> <x> <y>: Play a move.");
					Util.println("swap <tileno>: Schedule a tile for swapping.");
					Util.println("apply: Send your move to the server.");
					Util.println("revert: Restart your turn.");
					Util.println("stop: Stop your turn.");
					break;

				case "stop":

					try {
						hand.addTohand(removedFromHand);
					} catch (HandLimitReachedExeption e) {
						Util.log(e);
					}
					return null;

				case "revert":

					turn.getMoves().clear();
					turn.getSwap().clear();
					try {
						hand.addTohand(removedFromHand);
					} catch (HandLimitReachedExeption e) {
						Util.log(e);
					}
					removedFromHand.clear();
					board.setBoard(this.client.getBoard().copy(board));
					break;

				}
			}

		}

		return turn;
	}

	@Override
	public void sendLeaderboard(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startGame() {
		Util.println("A new game has been started. Good luck!");
	}

	@Override
	public void showChat(String message) {
		Util.println(message);
	}

	@Override
	public void connected() {
		Util.println("You are connected to the server, " + this.client.getName() + ".");
		Util.println("You can start a new game by typing 'game <int>'.");
	}

}
