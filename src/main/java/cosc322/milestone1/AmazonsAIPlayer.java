package cosc322.milestone1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GameMessage;
import ygraph.ai.smartfox.games.GamePlayer;
import ygraph.ai.smartfox.games.amazons.AmazonsGameMessage;

/**
 * Basic AI player class
 * 
 * @author Group 21
 * 
 */
public class AmazonsAIPlayer extends GamePlayer {

	private boolean isWhitePlayer;
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

	/**
	 *
	 * Login event handlers
	 *
	 */
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

	/**
	 *
	 * Handles the varying messages from the server
	 *
	 */
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

	/**
	 * Handles game start message and sets the isWhitePlayer flag.
	 * If so, perform move.
	 *
	 * @param msgDetails
	 */
	public void handleStart(Map<String, Object> msgDetails) {
		if (msgDetails.get(AmazonsGameMessage.PLAYER_BLACK).equals(this.userName)) {
			this.isWhitePlayer = false;
		} else if (msgDetails.get(AmazonsGameMessage.PLAYER_WHITE).equals(this.userName)) {
			this.isWhitePlayer = true;
			this.move();
		}
	}

	/**
	 * Does not currently work as expected, game loop should be replaced when proper search strategy is implemented.
	 */
	public void move() {
//		TODO: make a slowedDown parameter that activates this ==> "verbose" option
//		long start = System.currentTimeMillis();
//		while(System.currentTimeMillis() < start + 5000);

		boolean valid = false;

		ActionFactoryRecursive af = new ActionFactoryRecursive(gameBoard);
		ArrayList<byte[]> possibleMoves = af.getPossibleMoves(isWhitePlayer);

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
			ArrayList<byte[]> possibleArrows = af.getPossibleMoves(move);

			while(!valid && !possibleArrows.isEmpty()) {

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
				System.out.println(player + " - This Queen has no more places to put arrows - " + Arrays.toString(move));
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

	/**
	 * Convert from 0 indexed (x, y) to 1 indexed (y, x)
	 *
	 * @param pos
	 * @return
	 */
	public ArrayList<Integer> toServerFormat(ArrayList<Integer> pos) {
		ArrayList<Integer> serverPos = new ArrayList<Integer>();
		serverPos.add(pos.get(1) + 1);
		serverPos.add(pos.get(0) + 1);
		return serverPos;
	}

	/**
	 * Convert from 1 indexed (y, x) coordinates to 0 indexed (x, y)
	 *
	 * @param serverPos
	 * @return
	 */
	public ArrayList<Integer> toLocalFormat(ArrayList<Integer> serverPos){
		ArrayList<Integer> pos = new ArrayList<Integer>();
		pos.add(serverPos.get(1) - 1);
		pos.add(serverPos.get(0) - 1);
		return pos;
	}

	/**
	 * Random move picked from list - old
	 *
	 * @param positions
	 * @return
	 */
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

	public boolean isWhitePlayer() {
		return this.isWhitePlayer;
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
