package views;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;

import application.Console;
import application.Util;
import client.Client;
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
				String[] input = Console.readString("").split(" ");
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

	public InetAddress askForHost() {

		InetAddress host = null;

		while (host == null) {
			try {
				host = InetAddress
								.getByName(Console.readString("What hostname should we connect to?"
												+ System.lineSeparator() + "> "));
			} catch (UnknownHostException e) {
				this.sendNotification("This hostname cannot be resolved.");
				Util.log(e);
			}
		}

		return host;

	}

	public int askForPort() {

		int port = 0;

		while (port < 2000 || port > 3000) {
			port = Console.readInt("What port should we connect to? (2000..3000)"
							+ System.lineSeparator() + "> ");
		}

		return port;

	}

	public String requestNickname() {
		return Console.readString(
						"What nickname would you like to use?" + System.lineSeparator() + "> ");
	}

	public String askForPlayerType() {
		return Console.readString("What type of player would you like to be? (human, computer)"
						+ System.lineSeparator() + "> ");
	}

	@Override
	public Turn requestMoves(Turn turn) {

		List<Tile> removedFromHand = new ArrayList<Tile>();

		boolean turndone = false;

		while (!turndone) {

			Util.println(this.client.getBoard().toString());
			Util.println(this.client.getPlayerHand().toString());

			String action = "";

			while (!action.equals("move") && !action.equals("turn")) {
				switch (Console.readString("Would you like to swap or play?"
								+ System.lineSeparator() + "> ")) {
				case "move":
					break;
				case "turn":
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
		Console.readInt("A new game has been started. Good luck!");
	}

	@Override
	public void giveTurn(Turn turn) {
		Util.println("It is your turn. You can type 'move' to start it.");
		if (this.client.getStatus() == Client.Status.IN_GAME_INITIAL) {
			Util.println("Attention: this is the first turn. The player who submits the highest scoring turn plays it.");
		}
	}

	@Override
	public void showChat(String message) {
		Util.println(message);
	}

}
