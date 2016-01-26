package views;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Map;
import java.util.Observable;

import application.Util;
import client.Client;
import game.Board;
import game.Hand;
import game.Turn;
import protocol.Protocol;

/**
 * This is the TUI. The TUI makes no use of a graphical interface, and insteads
 * accepts input using the standard input.
 * 
 * @author Jonathan Juursema & Peter Wessels
 */
public class TextView extends Thread implements View {
	private Client client;
	
	private boolean running;

	public TextView(Client client) {
		this.client = client;
		this.start();
		this.running = true;
	}

	/**
	 * This is the threaded listener to client input. Input will be split by
	 * spaces and fed into the parser. The parser will then parse commands very
	 * similar to how the ConnectionHandler does, and delegate actions to the
	 * view or the client accordingly.
	 */
	@Override
	public void run() {

		while (this.running) {
			if (this.client.status == Client.Status.INITIALIZING) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					Util.log(e);
				}
			} else {
				String[] input = Util.readString("").split(" ");
				if (!this.running) {
					break;
				}
				String command = input[0];
				String[] args = Arrays.copyOfRange(input, 1, input.length);
				switch (command) {

				case "help":

					this.sendNotification(
									"game <int>: request the server for a game with a certain amount of players");
					this.sendNotification("move <tileno> <x> <y>: play a move.");
					this.sendNotification("swap <tileno>: request swap.");
					this.sendNotification("apply: send your move to the server.");
					this.sendNotification("revert: restart your turn.");
					this.sendNotification("stones: get the amount of stones in the game bag.");
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

					if (args.length != 1) {
						Util.println("usage: swap <tileno>");
						break;
					} else {
						this.client.requestSwap(args[0]);
					}

					break;

				case "stones":

					this.client.getTilesInBag();
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

	/**
	 * Handle changes to observed objects.
	 */
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

	@Override
	public void sendNotification(String message) {
		Util.println(message);
	}

	@Override
	public void sendNotification(String type, String message) {
		this.sendNotification("[" + type + "]: " + message);
	}

	@Override
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

	@Override
	public synchronized int askForPort() {

		int port = 0;

		while (port < 2000 || port > 3000) {
			port = Util.readInt("What port should we connect to? (2000..3000)"
							+ System.lineSeparator() + "> ");
		}

		return port;

	}

	@Override
	public synchronized String requestNickname() {
		return Util.readString(
						"What nickname would you like to use?" + System.lineSeparator() + "> ");
	}

	@Override
	public synchronized String askForPlayerType() {
		return Util.readString("What type of player would you like to be? (human, computer)"
						+ System.lineSeparator() + "> ");
	}

	@Override
	public void sendLeaderboard(String[] args) {
		Util.println("--- LEADERBOARD ---");
		for (int i = 0; i < args.length; i++) {
			String[] info = args[i].split("\\" + Protocol.Server.Settings.DELIMITER2);
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
		if (this.client.status == Client.Status.IN_GAME_INITIAL
						|| this.client.status == Client.Status.IN_TURN) {
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

	@Override
	public void sendScores(Map<String, Integer> scores) {
		this.sendNotification("Score:");
		for (String name : scores.keySet()) {
			this.sendNotification(name + ": " + scores.get(name));
		}
	}

	@Override
	public void stop(String message) {
		this.sendNotification("The TUI is shutting down.");
		this.sendNotification(message);
		this.running = false;
	}

}
