package views;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.ClientInfoStatus;
import java.util.Arrays;
import java.util.Observable;

import application.Util;
import client.Client;
import game.Board;
import game.Hand;
import game.Turn;
import protocol.Protocol;

public class TextView extends Thread implements View {
	private Client client;

	public TextView(Client client) {
		this.client = client;
		this.start();
	}

	public void run() {

		boolean running = true;
		while (running) {
			if (this.client.status == Client.Status.INITIALIZING) {
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
					this.sendNotification("move <tileno> <x> <y>: play a move.");
					this.sendNotification("swap <tileno> [tileno] [tileno] ...: request swap.");
					this.sendNotification("apply: send your move to the server.");
					this.sendNotification("revert: restart your turn.");
					this.sendNotification("");
					this.sendNotification("board: show the current board.");
					this.sendNotification("hand: show your hand.");
					this.sendNotification("leaderboard: request the leaderboard.");
					this.sendNotification("");
					this.sendNotification("invite <nickname>: invite a nickname.");
					this.sendNotification("accept: accept an invite.");
					this.sendNotification("decline: decline an invite.");
					this.sendNotification("");
					this.sendNotification("chat <msg>: send a chat message");
					break;

				case "board":

					if (this.client.status == Client.Status.IN_LOBBY) {
						this.sendNotification("You're not in a game.");
					} else {
						this.showBoard();
					}
					break;

				case "hand":

					if (this.client.status == Client.Status.IN_LOBBY) {
						this.sendNotification("You're not in a game.");
					} else {
						this.showHand();
					}
					break;

				case "move":

					if (args.length < 3) {
						Util.println("usage: move <tileno> <x> <y> ...");
					} else {
						this.client.addMove(Integer.parseInt(args[0]), Integer.parseInt(args[1]),
										Integer.parseInt(args[2]));
					}

					break;

				case "swap":

					if (args.length < 1) {
						Util.println("usage: swap <tileno> [tileno] [tileno] ...");
						break;
					} else {
						this.client.requestSwap(args);
					}

					break;

				case "apply":

					this.client.submitTurn();
					break;

				case "revert":

					this.client.revertTurn();
					break;

				case "invite":

					if (args.length < 1) {
						Util.println("usage: invite <nickname>");
						break;
					} else {
						this.client.invite(args[0]);
					}
					break;

				case "decline":
					this.client.declineInvite();
					break;

				case "accept":
					this.client.acceptInvite();
					break;

				case "leaderboard":

					this.client.requestLeaderboard();
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
		System.out.println(arg.toString());
		if (o instanceof Hand) {
			this.sendNotification("Your hand has changed:");
			this.showHand();
		} else if (o instanceof Board) {
			this.sendNotification("The board has changed:");
			this.showBoard();
		} else if (o instanceof Turn) {
			this.sendNotification("Your turn is now as follows:");
			this.showTurn();
		}
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
				host = InetAddress.getByName(Util.readString("What hostname should we connect to?"
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
	public void sendLeaderboard(String[] args) {
		Util.println("--- LEADERBOARD ---");
		for (int i = 0; i < args.length; i++) {
			String[] info = args[0].split("\\" + Protocol.Server.Settings.DELIMITER2);
			Util.println(i + ": " + info[0] + " (" + info[1] + ")");
		}
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

	@Override
	public void giveTurn() {
		Util.println("It is your turn! See 'help' for info on how to submit your turn.");
	}

	@Override
	public void showTurn() {
		Util.println(this.client.getTurn().toString());
	}

	@Override
	public void showBoard() {
		if (this.client.status == Client.Status.IN_GAME_INITIAL || this.client.status == Client.Status.IN_TURN) {
			Util.println(this.client.getTurn().getBoardCopy().toString());
		} else {
			Util.println(this.client.getBoard().toString());
		}
	}

	@Override
	public void showHand() {
		Util.println(this.client.getPlayerHand().toString());
	}

	@Override
	public void gotInvite(String string) {
		this.sendNotification("You got an invite from " + string + ".");
	}

}
