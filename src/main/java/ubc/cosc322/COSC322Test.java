
package ubc.cosc322;

import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GameMessage;
import ygraph.ai.smartfox.games.GamePlayer;
import ygraph.ai.smartfox.games.amazons.AmazonsGameMessage;
import ygraph.ai.smartfox.games.amazons.HumanPlayer;

/**
 * An example illustrating how to implement a GamePlayer
 * 
 * @author Yong Gao (yong.gao@ubc.ca) Jan 5, 2021
 *
 */
public class COSC322Test extends GamePlayer {

	private GameClient gameClient = null;
	private BaseGameGUI gamegui = null;

	private String userName = null;
	private String passwd = null;

	/**
	 * The main method
	 * 
	 * @param args for name and passwd (current, any string would work)
	 */
	public static void main(String[] args) {
		HumanPlayer p1 = new HumanPlayer();
		HumanPlayer p2 = new HumanPlayer();

		COSC322Test player = new COSC322Test(args[0], args[1]);

		if (player.getGameGUI() == null) {
			player.Go();
		} else {
			BaseGameGUI.sys_setup();
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					p1.Go();
					p2.Go();
					player.Go();
				}
			});
		}
	}

	/**
	 * Any name and password
	 * 
	 * @param userName
	 * @param passwd
	 */
	public COSC322Test(String userName, String passwd) {
		this.userName = userName;
		this.passwd = passwd;

		// To make a GUI-based player, create an instance of BaseGameGUI
		// and implement the method getGameGUI() accordingly
		this.gamegui = new BaseGameGUI(this);
	}

	@Override
	public void onLogin() {
		System.out.println(
				"Congratulations!!! " + "I am called because the server indicated that the login is successfully\n");

		this.userName = this.getGameClient().getUserName();
		if (this.getGameGUI() != null) {
			this.getGameGUI().setRoomInformation(this.getGameClient().getRoomList());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean handleGameMessage(String messageType, Map<String, Object> msgDetails) {
		// This method will be called by the GameClient when it receives a game-related
		// message
		// from the server.
		System.out.println("\nMessage From Server : Type => " + messageType);
		System.out.println("\nMessage From Server : Details => " + msgDetails.get("game-state"));

		// For a detailed description of the message types and format,
		// see the method GamePlayer.handleGameMessage() in the game-client-api
		// document.
		try {
			switch (messageType) {
			case GameMessage.GAME_STATE_BOARD:
				gamegui.setGameState((ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.GAME_STATE));
				break;
			case GameMessage.GAME_ACTION_START:
				System.out.printf("Starting Game\n%s", msgDetails);
				break;
			case GameMessage.GAME_ACTION_MOVE:
				gamegui.updateGameState(msgDetails);
				break;
			}

		} catch (Exception e) {
			System.out.println("Something went wrong handling a game message from the server:");
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public String userName() {
		return userName;
	}

	@Override
	public GameClient getGameClient() {
		// TODO Auto-generated method stub
		return this.gameClient;
	}

	@Override
	public BaseGameGUI getGameGUI() {
		// TODO Auto-generated method stub
		return this.gamegui;
	}

	@Override
	public void connect() {
		// TODO Auto-generated method stub
		gameClient = new GameClient(userName, passwd, this);
	}

}// end of class
