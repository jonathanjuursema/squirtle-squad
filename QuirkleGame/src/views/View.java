package views;

import java.net.InetAddress;
import java.util.Observer;

import game.Turn;

public interface View extends Observer {
	
	public InetAddress askForHost();
	public int askForPort();

	public void sendNotification(String message);
	public void sendNotification(String type, String message);
	
	public String requestNickname();
	public String askForPlayerType();
	public Turn requestMoves(Turn turn);
	public void sendLeaderboard(String[] args);
	public void startGame();
	public void showChat(String message);
	public void connected();

}
