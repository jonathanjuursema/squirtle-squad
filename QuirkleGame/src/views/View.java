package views;

import java.net.InetAddress;
import java.util.Map;
import java.util.Observer;

/**
 * This is the general view interface. It should be noted that this interface
 * tends a bit towards being text-based. This is probably due to the way the
 * Client and protocol have been set-up. It should, however, allow a GUI to
 * connect as well.
 * 
 * @author Jonathan Juursema & Peter Wessels
 */
public interface View extends Observer {

	/**
	 * Should ask the user for the hostname to which the client is to connect.
	 * 
	 * @return An InetAddress object to which the client can connect.
	 */
	public InetAddress askForHost();

	/**
	 * Should ask the user for the port to which the client is to connect.
	 * 
	 * @return A port number for the client to connect to.
	 */
	public int askForPort();

	/**
	 * Sends a single-string notification to the user.
	 * 
	 * @param message
	 *            The notification.
	 */
	public void sendNotification(String message);

	/**
	 * Similar to Util.log and sendNotifciation.
	 * 
	 * @param type
	 *            Message type.
	 * @param message
	 *            Message.
	 */
	public void sendNotification(String type, String message);

	/**
	 * Request the user nickname.
	 * 
	 * @return The user nickname.
	 */
	public String requestNickname();

	/**
	 * Asks the user for the player type when starting a game. Should return
	 * 'human' if the user wishes to play itself, or 'computer' when they want
	 * the AI to play for them.
	 * 
	 * @return Either 'computer' or 'human'.
	 */
	public String askForPlayerType();

	/**
	 * Updates the view with the current leaderboard. Contains a list of
	 * strings, where each string contains the player name and score, seperated
	 * by DELIMITER2.
	 * 
	 * @param args
	 *            The formatted strings.
	 */
	public void sendLeaderboard(String[] args);

	/**
	 * Indicates the view that the game has started.
	 */
	public void startGame();

	/**
	 * Sends a chat message to the view.
	 * 
	 * @param message
	 *            The formatted chat message.
	 */
	public void showChat(String message);

	/**
	 * Notifies the view that the cient is connected.
	 */
	public void connected();

	/**
	 * Notifies the view that it is the user's turn.
	 */
	public void giveTurn();

	/**
	 * Instructs the view to show the user the current turn.
	 */
	public void showTurn();

	/**
	 * Instructs the view to show the user the current board.
	 */
	public void showBoard();

	/**
	 * Instructs the view to show the user the current hand.
	 */
	public void showHand();

	/**
	 * Instructs the view to notify the user that they have a pending invite.
	 * 
	 * @param string
	 *            The name of the inviting player.
	 */
	public void gotInvite(String string);

	/**
	 * Updates the view with the scores of the current game.
	 * 
	 * @param scores
	 *            A hashmap of Strings to Integers which maps player names to
	 *            their scores.
	 */
	public void sendScores(Map<String, Integer> scores);

	/**
	 * Instructs the view that the client is to be closed.
	 * 
	 * @param message
	 *            A message for the user.
	 */
	public void stop(String message);

}
