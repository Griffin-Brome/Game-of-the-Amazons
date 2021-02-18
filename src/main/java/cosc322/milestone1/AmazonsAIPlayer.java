package cosc322.milestone1;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GameMessage;
import ygraph.ai.smartfox.games.GamePlayer;
import ygraph.ai.smartfox.games.amazons.AmazonsGameMessage;

/**
 * 
 * Trying to implement a basic random move player AI. Milestone 1
 * 
 * @author Group 21
 * 
 */
public class AmazonsAIPlayer extends GamePlayer {

	public boolean isWhitePlayer;
	private GameClient gameClient = null;
	private BaseGameGUI gamegui = null;
	private GameBoard gameBoard = null;

	private String userName = null;
	private String passwd = null;

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
				gameBoard.updateBoard(msgDetails);
				gamegui.updateGameState(msgDetails);

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
		ArrayList<Integer> queen = new ArrayList<>();
		ArrayList<Integer> newPos = new ArrayList<>();
		ArrayList<Integer> arrowPos = new ArrayList<>();

		// Should have a check if pieces are already blocked in?
		ArrayList<ArrayList<Integer>> possibleMoves = gameBoard.getPossibleMoves(isWhitePlayer);
		if (!possibleMoves.isEmpty()) {

			ArrayList<Integer> move = randomMove(possibleMoves);
			newPos.add(move.get(0)); // new pos
			newPos.add(move.get(1));
			queen.add(move.get(2)); // queen being moved y, x
			queen.add(move.get(3));

			ArrayList<ArrayList<Integer>> possibleArrows = gameBoard.getPossibleMoves(newPos);
//			if (!possibleArrows.isEmpty()) {
				ArrayList<Integer> arrowMove = randomMove(possibleArrows);
				arrowPos.add(arrowMove.get(0)); // arrow position
				arrowPos.add(arrowMove.get(1));

				gameBoard.updateBoard(queen, newPos, arrowPos);
				gamegui.updateGameState(queen, newPos, arrowPos);
				gameClient.sendMoveMessage(queen, newPos, arrowPos);

				System.out.println("Current Board Matrix:\n------------------------------");
				byte[][] matrix = gameBoard.getMatrix();
				for (int row = 0; row < 11; row++) {
					for (int col = 0; col < 11; col++) {
						System.out.printf("%d, ", matrix[row][col]);
					}
					System.out.println("");
				}

				System.out.println("");
				System.out.println("black: " + gameBoard.getBlackQueens());
				System.out.println("white: " + gameBoard.getWhiteQueens());
				System.out.println("arrows: " + gameBoard.getArrows());

				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
//			} else {
//				System.out.println("No more places to put arrows.");
//			}
		} else {
			String player = isWhitePlayer ? "White Player" : "Black Player";
			System.out.println("Game over... " + player);
		}

	}

	// Will need a class / function
	public ArrayList<Integer> randomMove(ArrayList<ArrayList<Integer>> positions) {
		int idx = (int) (Math.random() * positions.size());
		return positions.get(idx);
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

	public boolean handleMessage(String type, String msg) {
		return true;
	}

	public boolean handleMessage(String type) {
		return true;
	}

}
