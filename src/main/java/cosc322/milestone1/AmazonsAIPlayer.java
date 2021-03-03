package cosc322.milestone1;

import java.util.ArrayList;
import java.util.Arrays;
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
		setUserName(userName);

		setPassword(passwd);
		setIsWhitePlayer(false);
		setGameGUI(new BaseGameGUI(this));
		setGameBoard(new GameBoard());
	}

	@Override
	public void onLogin() {
		System.out.println("Login successfull!\n");

		String uname = gameClient.getUserName();
		setUserName(uname);
		if (this.getGameGUI() != null) {
			this.getGameGUI().setRoomInformation(this.getGameClient().getRoomList());
		} else {
			System.err.println("Error: Could not load game UI");
			return; // Break out of program
		}
	}

	/**
	 * Sets the players username
	 *
	 * @param userName Must not be null, further constraints may need to be added
	 *
	 */
	public void setUserName(String userName) {
		if (userName.isEmpty()) {
			System.err.println("Error: Username cannot be empty");
			return;
		} else {
			this.userName = userName;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean handleGameMessage(String messageType, Map<String, Object> msgDetails) {

		try {
			switch (messageType) {

			// set gui/board
			case GameMessage.GAME_STATE_BOARD:
				gamegui.setGameState((ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.GAME_STATE));
				gameBoard.setBoardState((ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.GAME_STATE), false);
				break;

			case GameMessage.GAME_ACTION_START:
				this.handleStart(msgDetails);
				break;

			case GameMessage.GAME_ACTION_MOVE:
				/**
				 * 
				 * 
				 * Now the only place conversion from server to local occurs
				 * 
				 * 
				 */
				ArrayList<Integer> queenPosCurr = toLocalFormat((ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.QUEEN_POS_CURR));
				ArrayList<Integer> queenPosNext = toLocalFormat((ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.Queen_POS_NEXT));
				ArrayList<Integer> arrowPos 	= toLocalFormat((ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.ARROW_POS));
				
				gameBoard.updateBoard(queenPosCurr, queenPosNext, arrowPos);
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

	/**
	 * Does not currently work as expected, the possiblemoves search is a brute
	 * force approach and seems to break.
	 */
	public void move() {

		Minimax mm = new Minimax(gameBoard, isWhitePlayer);
		SearchTreeNode node = mm.iterativeDeepening(4);
	
		while(node!=null) {
			System.out.println("Node " + node);
			node = node.getNextNode();
		}
		
//		System.out.println("Next AlphaBeta Opponent Node " + node.getNextNode().getNextNode());
//		System.out.println("Next AlphaBeta Opponent Move " + node.getNextNode().getNextNode().getNextQueenPos());
//		
		boolean valid = false;

		ArrayList<byte[]> possibleMoves = gameBoard.getPossibleMoves(isWhitePlayer);

		while (!valid && !possibleMoves.isEmpty()) {

			ArrayList<Integer> queen = new ArrayList<>();
			ArrayList<Integer> newPos = new ArrayList<>();

			byte[] move = randomMove(possibleMoves); // pick move and remove it

			// add to appropriate arrayList
			newPos.add((int) move[0]); // new pos
			newPos.add((int) move[1]);
			queen.add((int) move[2]); // queen being moved y, x
			queen.add((int) move[3]);

			// from that position get possible arrow moves
			ArrayList<byte[]> possibleArrows = gameBoard.getPossibleMoves(move);
			while (!valid && !possibleArrows.isEmpty()) {

				byte[] arrowMove = randomMove(possibleArrows); // pick arrow and remove it

				// add to arraylist for server message
				ArrayList<Integer> arrowPos = new ArrayList<>();
				arrowPos.add((int) arrowMove[0]); // arrow position
				arrowPos.add((int) arrowMove[1]);

				gameBoard.updateBoard(queen, newPos, arrowPos);
				gamegui.updateGameState(queen, newPos, arrowPos);

				/**
				 * 
				 * 
				 * Only place where we have to convert to server format of (y, x) and 1 indexed is now here.
				 * 
				 * 
				 */
				queen = toServerFormat(queen);
				newPos = toServerFormat(newPos);
				arrowPos = toServerFormat(arrowPos);
				
				gameClient.sendMoveMessage(queen, newPos, arrowPos);
				valid = true;

			}
			if (possibleArrows.isEmpty()) {
				String player = isWhitePlayer ? "White Player" : "Black Player";
				System.out
						.println(player + " - This Queen has no more places to put arrows - " + Arrays.toString(move));
				System.out.println("moves");
				for (byte[] p : possibleMoves)
					System.out.println(Arrays.toString(p));
			}
		}

		if (possibleMoves.isEmpty()) {
			String player = isWhitePlayer ? "White Player" : "Black Player";
			System.out.println("Game over... " + player);
		}

	}

	public ArrayList<Integer> toServerFormat(ArrayList<Integer> pos) {
		ArrayList<Integer> serverPos = new ArrayList<Integer>();
		serverPos.add(pos.get(1) + 1);
		serverPos.add(pos.get(0) + 1);
		return serverPos;
	}
	
	public ArrayList<Integer> toLocalFormat(ArrayList<Integer> serverPos){
		ArrayList<Integer> pos = new ArrayList<Integer>();
		pos.add(serverPos.get(1) - 1);
		pos.add(serverPos.get(0) - 1);
		return pos;
	}

	// Will need a class / function
	public byte[] randomMove(ArrayList<byte[]> positions) {
		int idx = (int) (Math.random() * positions.size());
		byte[] pos = positions.get(idx);
		positions.remove(idx);
		return pos;
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

	public void setPassword(String pw) {
		this.passwd = pw;
	}

	public void setIsWhitePlayer(boolean isWhitePlayer) {
		this.isWhitePlayer = isWhitePlayer;
	}

	public void setGameGUI(BaseGameGUI gui) {
		this.gamegui = gui;
	}

	public void setGameBoard(GameBoard board) {
		this.gameBoard = board;
	}

}
