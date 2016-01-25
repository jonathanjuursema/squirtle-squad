package views;

import java.util.Observable;
import java.util.Observer;

import application.Console;
import application.Util;
import client.Client;
import exceptions.HandLimitReachedExeption;
import exceptions.IllegalMoveException;
import exceptions.IllegalTurnException;
import exceptions.SquareOutOfBoundsException;
import exceptions.TileNotInHandException;
import game.Board;
import game.BoardSquare;
import game.Hand;
import game.Move;
import game.Tile;
import game.Turn;

public class TUIview extends Thread implements Observer, View {
	private Client client;

	public TUIview(Client client) {
		this.client = client;
		this.start();
	}

	public void run() {
		printMessage("game", "Welcome to the TUIview!");
		printMessage("game", "Waiting for commands of the server..");

		// TODO: Lobby commands afvangen
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg == "board" && o instanceof Board) {
			Board board = (Board) o;

			// If it is your turn, then the board should update every move
			if (client.getPlayer().getTurn() != null) {
				printMessage("game", board.toString());
			}
		} else if (arg == "turn" && o instanceof Turn) {
			Turn turn = (Turn) o;
			printMessage("game", turn.toString());
			printMessage("game", turn.getBoardCopy().toString());

		} else if (arg == "hand" && o instanceof Hand) {
			printMessage("game", o.toString());
		} else {
			printMessage("debug", "Help, I don't know what to do with the update of " + o.toString());
		}
	}

	public void requestMoves(String requestMessage) {

		printMessage("game", requestMessage);

		int action = 0;
		action = printActions();

		Tile dummyFeedbackTile = new Tile(Tile.BLACK, Tile.DUMMY);

		while (action != 3
				|| !(client.getPlayer().getTurn().isSwapRequest() || client.getPlayer().getTurn().isMoveRequest())) {
			if (action == 2 && !client.getPlayer().getTurn().isSwapRequest()) {
				try {
					Move moveToPlay = askForMove();
					client.getPlayer().getTurn().addMove(moveToPlay);
					int index = client.getPlayer().getHand().getTilesInHand().indexOf(moveToPlay.getTile());

					client.getPlayer().getHand().removeFromHand(moveToPlay.getTile());
					client.getPlayer().getHand().getTilesInHand().add(index, dummyFeedbackTile);
				} catch (SquareOutOfBoundsException | IllegalMoveException | IllegalTurnException
						| TileNotInHandException | NullPointerException e) {
					if (e instanceof IllegalMoveException) {
						this.printMessage("error", "This move is not possible");
					} else if (e instanceof IllegalTurnException) {
						this.printMessage("error", "This turn is not possible");
					} else if (e instanceof NullPointerException) {
						this.printMessage("error", "This tile is not possible");
					}
					Util.log(e);
					action = 0;
				}

			}

			if (action == 1 && !client.getPlayer().getTurn().isMoveRequest()) {
				try {
					Tile tileToSwap = askForSwap();
					client.getPlayer().getTurn().addSwapRequest(tileToSwap);
					int index = client.getPlayer().getHand().getTilesInHand().indexOf(tileToSwap);

					client.getPlayer().getHand().removeFromHand(tileToSwap);
					client.getPlayer().getHand().getTilesInHand().add(index, dummyFeedbackTile);

				} catch (IllegalTurnException | NumberFormatException | NullPointerException
						| TileNotInHandException e) {
					if (e instanceof IllegalMoveException) {
						this.printMessage("error", "This move is not possible");
					} else if (e instanceof IllegalTurnException) {
						this.printMessage("error", "This turn is not possible");
					} else if (e instanceof TileNotInHandException) {
						this.printMessage("error", "This turn is not possible");
					} else if (e instanceof NullPointerException) {
						this.printMessage("error", "This tile is not possible");
					}
					action = 0;
					Util.log(e);
				}
			}

			action = printActions();
		}

		if (client.getPlayer().getTurn().isMoveRequest()) {
			for (Move m : client.getPlayer().getTurn().getMoves()) {
				try {
					client.getPlayer().getHand().removeFromHand(dummyFeedbackTile);
					client.getPlayer().getHand().addToHand(m.getTile());
				} catch (HandLimitReachedExeption | TileNotInHandException e) {
					Util.log(e);
				}
			}
		}

		if (client.getPlayer().getTurn().isSwapRequest()) {
			for (Tile t : client.getPlayer().getTurn().getSwap()) {
				try {
					client.getPlayer().getHand().removeFromHand(dummyFeedbackTile);

				} catch (TileNotInHandException e) {
					// Util.log(e);
				}

				try {
					client.getPlayer().getHand().addToHand(t);
				} catch (HandLimitReachedExeption e) {
					Util.log(e);
				}
			}
		}

		client.sendTurnToServer();
	}

	/**
	 * @return
	 */
	public int printActions() {
		Console.println(client.getPlayer().getTurn().getBoardCopy().toString());
		Console.println(client.getPlayer().getTurn().getPlayer().getHand().toString());

		if (!client.getPlayer().getTurn().isMoveRequest()) {
			printMessage("game", "[1] Add a tile to swap.");
		}

		if (!client.getPlayer().getTurn().isSwapRequest()) {
			printMessage("game", "[2] Add a move.");
		}

		if (client.getPlayer().getTurn().isSwapRequest() ^ client.getPlayer().getTurn().isMoveRequest()) {
			printMessage("game", "[3] Play this turn.");
		}

		int action = Console.readInt("What action do you want? \n> ");
		return action;
	}

	/**
	 * @throws NumberFormatException
	 * @throws IllegalTurnException
	 */
	public Move askForMove() throws NumberFormatException, IllegalTurnException {

		int tileFromHand = Console.readInt(
				"[game] Select a tile of your hand by typing the corresponding number. (or type \"cancel\")\n> ");
		Tile tile = selectTileFromHand(tileFromHand);

		if (selectTileFromHand(tileFromHand) == null) {
			this.printMessage("error", "This tile is not valid");
			throw new IllegalTurnException();
		}

		while (tile != null) {
			if (tile.getShape() == Tile.DUMMY) {
				this.printMessage("error", "This tile is not valid");
				break;
			}

			String input = Console
					.readString("[game] Select a place with \"x-coordinate,y-coordinate\". (or type \"cancel\")\n> ");

			String[] coordinates = input.split(",");
			int x = Integer.parseInt(coordinates[0]);
			int y = Integer.parseInt(coordinates[1]);

			BoardSquare b = parseBoardSquare(x, y);
			if (b != null) {
				return new Move(tile, b);
			} else {
				throw new IllegalTurnException();
			}
		}

		return null;
	}

	public Tile askForSwap() throws NumberFormatException {
		Tile tile = selectTileFromHand(
				Console.readInt("Please select a tile of your hand by typing the corresponding number.\n> "));
		while (tile != null) {
			if (tile.getShape() == Tile.DUMMY) {
				this.printMessage("error", "This tile is not valid");
				break;
			}
			return tile;
		}
		return null;
	}

	public BoardSquare parseBoardSquare(int x, int y) {
		try {
			if (client.getPlayer().getTurn().getBoardCopy().getPossiblePlaces()
					.contains(client.getPlayer().getTurn().getBoardCopy().getSquare(x, y))) {
				return client.getPlayer().getTurn().getBoardCopy().getSquare(x, y);
			} else {
				printMessage("game", "It is not possible to place a tile on this place.");
			}
		} catch (SquareOutOfBoundsException e) {
			printMessage("game", "This boardsquare does not exists");
		}
		return null;
	}

	/**
	 * Select a tile in hand.
	 * 
	 * @requires numberInHand >= 1 && numberInHand <= Hand.Limit
	 * @param numberInHand
	 * @return
	 */
	public Tile selectTileFromHand(int numberInHand) {
		if (numberInHand > 0 && numberInHand < Hand.LIMIT + 1) {
			if (client.getPlayer().getHand().getTilesInHand().get(numberInHand - 1) != null) {
				return client.getPlayer().getHand().getTilesInHand().get(numberInHand - 1);
			} else {
				Util.log("debug", "Tile not in hand");
			}
		} else {
			Util.log("debug", "Please make a valid choice");
		}

		Util.log("debug", "Please make a valid choice");
		return null;
	}

	public void printHand(Hand hand) {
		Console.println(hand.toString());
	}

	public void printBoard(Board board) {
		Console.println(client.getPlayer().getHand().toString());
	}

	public void printTurn(Turn turn, String playerName) {
		Console.println(client.getBoardCopy().toString());
		Console.println(turn.toString());
	}

	public void printMessage(String type, String string) {
		Console.println("[" + type + "]: " + string);
	}

	public String askForInput(String type, String message) {
		if (!message.equals("")) {
			Console.println("[" + type + "]: " + message);
		}
		
		String input = Console.readString("> ");
		printMessage("chat", input);
		return input;
	}
}
