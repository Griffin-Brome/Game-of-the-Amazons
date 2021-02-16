package cosc322.milestone1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.BoardGameModel;
import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GameMessage;
import ygraph.ai.smartfox.games.GamePlayer;
import ygraph.ai.smartfox.games.amazons.AmazonsGameMessage;
import ygraph.ai.smartfox.games.amazons.HumanPlayer;

/**
 * 
 * Trying to implement a basic random move player AI. Milestone 1
 * 
 * @author Group 21
 * 
 */
public class AmazonsAIPlayer extends GamePlayer {

	private GameClient gameClient = null;
	private BaseGameGUI gamegui = null;
	private GameBoard gameBoard = null;

	private String userName = null;
	private String passwd = null;
	private boolean isWhitePlayer;

	public AmazonsAIPlayer(String userName, String passwd) {
		this.userName = userName;
		this.passwd = passwd;
		this.isWhitePlayer = false; 
		this.gamegui = new BaseGameGUI(this);
		this.gameBoard = new GameBoard();
	}

	@Override
	public void onLogin() {
		System.out.println("Login successfull!\n");

		this.userName = this.getGameClient().getUserName();
		if (this.getGameGUI() != null) {
			this.getGameGUI().setRoomInformation(this.getGameClient().getRoomList());

		}
		
		// auto join
//		this.gameClient.joinRoom(this.gameClient.getRoomList().get(0).getName());

	}


	@SuppressWarnings("unchecked")
	@Override
	public boolean handleGameMessage(String messageType, Map<String, Object> msgDetails) {

		try {
			switch (messageType) {

			// set gui/board
			case GameMessage.GAME_STATE_BOARD:
				gamegui.setGameState((ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.GAME_STATE));
				gameBoard.setBoardState((ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.GAME_STATE));
				break;

			case GameMessage.GAME_ACTION_START:
				this.handleStart(msgDetails);
				break;

			case GameMessage.GAME_ACTION_MOVE:
				gamegui.updateGameState(msgDetails);
				gameBoard.updateBoard(msgDetails);
				
				// handle opponent move here ~
				this.move();
				break;
			}

		} catch (Exception e) {
			System.out.println("Something went wrong handling a game message from the server:");
			e.printStackTrace();
		}

		return false;
	}

	public void handleStart(Map<String, Object> msgDetails) {
		if (msgDetails.get(AmazonsGameMessage.PLAYER_BLACK).equals(this.userName)) {
			this.isWhitePlayer = false;
		} else if (msgDetails.get(AmazonsGameMessage.PLAYER_WHITE).equals(this.userName)) {
			this.isWhitePlayer = true;
			this.move();
		}
	}
	
	public void move() {
//		gameBoard.getPossibleMoves(this);
		makeRandomMove();
	}

	// Will need a class / function ~> getPossibleMoves(queen, board) or something
	public void makeRandomMove() {
		//
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
		gameClient = new GameClient(userName, passwd, (GamePlayer) this);
	}

}
