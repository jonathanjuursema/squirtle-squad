package views;

import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

import application.Console;
import client.Client;
import game.Hand;

public class TUIview implements Observer, View {
	private Client client;
	
	public TUIview(Client client) {
		this.client = client;
	}
	
	public void start() {
		boolean run = true;
		while(run) {
            run = readCommand("\n> What is your command?");
		}
	}
	
	private boolean readCommand(String request){
		String answer;
		do {
			Console.println(request);
            Scanner in = new Scanner(System.in);
            answer = in.hasNextLine() ? in.nextLine() : null;

        } while (answer != "EXIT");
		
		return false;
	}

	@Override
	public void update(Observable o, Object arg) {
		
	}
	
	public void printHand(Hand hand){
		Console.println(hand.toString());
	}
	
	public void printMessage(String type, String string){
		Console.println("["+ type + "]: " + string);
	}
	
}
