package views;

import java.util.Observable;
import java.util.Observer;

import application.Console;
import client.Client;
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

		if (!client.getPlayer().getTurn().isMoveRequest()) {
			printMessage("game", "[1] Add a tile to swap.");
		} else if (!client.getPlayer().getTurn().isSwapRequest()) {
			printMessage("game", "[2] Add a move.");
		}
		
		printMessage("game", "[3] Play this turn.");

		int action = Console.readInt("What action do you want? \n > ");
		
		while (action != 3) {
			if (action == 2) {
				try {
					client.getPlayer().getTurn().addMove(askForMove());
				} catch (SquareOutOfBoundsException | IllegalMoveException | IllegalTurnException e) {
					if (e instanceof IllegalMoveException) {
						this.printMessage("error", "This move is not possible");
					} else if (e instanceof IllegalTurnException) {
						this.printMessage("error", "This turn is not possible");
					}
				}
			}

			if (action == 1) {
				try {
					client.getPlayer().getTurn().addSwapRequest(askForSwap());
				} catch (IllegalTurnException | NumberFormatException e) {
					if (e instanceof IllegalMoveException) {
						this.printMessage("error", "This move is not possible");
					} else if (e instanceof IllegalTurnException) {
						this.printMessage("error", "This turn is not possible");
					} else if (e instanceof TileNotInHandException) {
						this.printMessage("error", "This turn is not possible");
					}
				}
			}
		}

		client.sendTurnToServer();
	}

	/**
	 * @throws NumberFormatException
	 */
	public Move askForMove() throws NumberFormatException {
		Tile tile = selectTileFromHand(Console.readInt("Please select a tile of your hand by typing the corresponding number."));
		while (tile != null) {
			String input = Console.readString(
					"Please select the place where you want to place the tile by typing \"x-coordinate,y-coordinate\".");
			String[] coordinates = input.split(",");
			int x = Integer.parseInt(coordinates[0]);
			int y = Integer.parseInt(coordinates[0]);
			BoardSquare b = parseBoardSquare(x, y);
			while (b != null) {
				return new Move(tile, b);
			}
		}

		return null;
	}

	public Tile askForSwap() throws NumberFormatException {
		Tile tile = selectTileFromHand(Console.readInt("Please select a tile of your hand by typing the corresponding number."));
		while (tile != null) {
			return tile;
		}
		return null;
	}

	public BoardSquare parseBoardSquare(int x, int y) {
		try {
			if (client.getBoardCopy().getPossiblePlaces().contains(client.getBoardCopy().getSquare(x, y))) {
				return client.getBoardCopy().getSquare(x, y);
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
	 * @requires numberInHand > 0 && numberInHand <= Hand.Limit
	 * @param numberInHand
	 * @return
	 */
	public Tile selectTileFromHand(int numberInHand) {
		if (numberInHand > 0 && numberInHand <= 6) {
			if (client.getPlayer().getHand().getTilesInHand().get(numberInHand) != null) {
				return client.getPlayer().getHand().getTilesInHand().get(numberInHand);
			} else {
				return null;
			}
		} else {
			return null;
		}
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
		String input = Console.readString(message + System.lineSeparator() + "> ");
		printMessage("chat", input);
		return input;
	}
}
