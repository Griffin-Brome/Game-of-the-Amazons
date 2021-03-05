package cosc322.milestone1;

import java.util.ArrayList;

public class ActionFactory {
	private final static byte U = 1;
	private final static byte UR = 2;
	private final static byte R = 3;
	private final static byte DR = 4;
	private final static byte D = 5;
	private final static byte DL = 6;
	private final static byte L = 7;
	private final static byte UL = 8;

	private final static byte BLANK = 0;
	private final static byte WHITE_QUEEN = 1;
	private final static byte BLACK_QUEEN = 2;
	private final static byte ARROW = 3;

	private final static byte[] directions = { U, UR, R, DR, D, DL, L, UL };

	/**
	 * Get all possible moves from a position on the board.
	 * 
	 * @param pos
	 * @return
	 */
	public static ArrayList<byte[]> getAvailableTiles(GameBoard gameBoard, byte[] pos) {
		ArrayList<byte[]> moves = new ArrayList<>();

		for (byte dir : directions) {

			byte x = pos[0];
			byte y = pos[1];

			switch (dir) {
			case U:
				while (++y < gameBoard.ROWS && gameBoard.isBlank(x, y)) {
					moves.add(new byte[] { x, y });
				}
				break;
			case UR:
				while (++x < gameBoard.COLS && ++y < gameBoard.ROWS && gameBoard.isBlank(x, y)) {
					moves.add(new byte[] { x, y });
				}
				break;
			case R:
				while (++x < gameBoard.COLS && gameBoard.isBlank(x, y)) {
					moves.add(new byte[] { x, y });
				}
				break;
			case DR:
				while (++x < gameBoard.COLS && --y >= 0 && gameBoard.isBlank(x, y)) {
					moves.add(new byte[] { x, y });
				}
				break;
			case D:
				while (--y >= 0 && gameBoard.isBlank(x, y)) {
					moves.add(new byte[] { x, y });
				}
				break;
			case DL:
				while (--x >= 0 && --y >= 0 && gameBoard.isBlank(x, y)) {
					moves.add(new byte[] { x, y });
				}
				break;
			case L:
				while (--x >= 0 && gameBoard.isBlank(x, y)) {
					moves.add(new byte[] { x, y });
				}
				break;
			case UL:
				while (--x >= 0 && ++y < gameBoard.ROWS && gameBoard.isBlank(x, y)) {
					moves.add(new byte[] { x, y });
				}
				break;
			}
		}
		return moves;
	}

	/**
	 * Returns a SearchTreeNode who's children are all possible moves available for a given player.
	 * 
	 * @param gameBoard
	 * @param player
	 * @return
	 */
	public static SearchTreeNode getPossibleMoves(GameBoard gameBoard, AmazonsAIPlayer player) {
		ArrayList<Queen> queens = new ArrayList<>();
		SearchTreeNode root = new SearchTreeNode(gameBoard.getMatrix(), (byte) 1, player.isWhitePlayer());
		
		if (player.isWhitePlayer()) {
			queens = gameBoard.getWhiteQueens();
		} else {
			queens = gameBoard.getBlackQueens();
		}

		for (Queen queen : queens) {
			for (byte[] newQueenPos : ActionFactory.getAvailableTiles(gameBoard, queen.getPosition())) {
				makeTempQueenMove(queen, newQueenPos, gameBoard.getMatrix());
				for (byte[] arrowPos : ActionFactory.getAvailableTiles(gameBoard, newQueenPos)) {
					byte[][] gameState = getNewGameState(queen, gameBoard, newQueenPos, arrowPos);
					root.add(new SearchTreeNode(gameState, queen.getPosition(), newQueenPos, arrowPos));
				}
				undoTempQueenMove(queen, newQueenPos, gameBoard.getMatrix());
			}
		}
		return root;
	}
	
	
	/**
	 * Move the queen to a position in the matrix, moving it out of the way for the
	 * arrow shot.
	 * 
	 * @param queen
	 * @param tempQueenPos
	 */
	public static void makeTempQueenMove(Queen queen, byte[] tempQueenPos, byte[][] matrix) {
		byte[] queenPos = queen.getPosition();
		matrix[queenPos[0]][queenPos[1]] = BLANK;
		matrix[tempQueenPos[0]][tempQueenPos[1]] = queen.getId();
	}

	
	/**
	 * Move the queen back after checking the arrow shot.
	 * 
	 * @param queen
	 * @param tempQueenPos
	 */
	public static void undoTempQueenMove(Queen queen, byte[] tempQueenPos, byte[][] matrix) {
		byte[] queenPos = queen.getPosition();
		matrix[queenPos[0]][queenPos[1]] = queen.getId();
		matrix[tempQueenPos[0]][tempQueenPos[1]] = BLANK;
	}
	
	/**
	 * Move the queen and arrow to a position in the matrix.
	 * 
	 * @param queen
	 * @param tempQueenPos
	 */
	public static void makeTempMove(Queen queen, byte[] tempQueenPos, byte[] arrowPos, byte[][] matrix) {
		byte[] queenPos = queen.getPosition();
		matrix[queenPos[0]][queenPos[1]] = BLANK;
		matrix[tempQueenPos[0]][tempQueenPos[1]] = queen.getId();
		matrix[arrowPos[0]][arrowPos[1]] = ARROW;
	}

	/**
	 * Move the queen back and remove arrow
	 * 
	 * @param queen
	 * @param tempQueenPos
	 */
	public static void undoTempMove(Queen queen, byte[] tempQueenPos, byte[] arrowPos, byte[][] matrix) {
		byte[] queenPos = queen.getPosition();
		matrix[queenPos[0]][queenPos[1]] = queen.getId();
		matrix[tempQueenPos[0]][tempQueenPos[1]] = BLANK;
		matrix[arrowPos[0]][arrowPos[1]] = BLANK;
	}
	
	/**
	 * Generate the matrix for a given queen, new position and arrow position.
	 * 
	 * @param queen
	 * @param newQueenPos
	 * @param arrowPos
	 * @return
	 */
	public static byte[][] getNewGameState(Queen queen, GameBoard gameBoard, byte[] newQueenPos, byte[] arrowPos) {

		byte[] queenPos = queen.getPosition();
		byte[][] newState = gameBoard.getMatrixCopy();
		newState[queenPos[0]][queenPos[1]] = BLANK;
		newState[arrowPos[0]][arrowPos[1]] = ARROW;
		newState[newQueenPos[0]][newQueenPos[1]] = queen.getId();

		return newState;

	}
	
}
