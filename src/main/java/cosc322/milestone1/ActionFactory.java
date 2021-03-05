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
	
	private final static byte ROWS = 10;
	private final static byte COLS = 10;

	private final static byte[] directions = { U, UR, R, DR, D, DL, L, UL };

	/**
	 * Get all possible moves from a position on the board.
	 * 
	 * @param pos
	 * @return
	 */
	public static ArrayList<byte[]> getAvailableTiles(byte[][] gameBoard, byte[] pos) {
		ArrayList<byte[]> moves = new ArrayList<>();

		for (byte dir : directions) {

			byte x = pos[0];
			byte y = pos[1];

			switch (dir) {
			case U:
				while (++y < ROWS && gameBoard[x][y] == BLANK) {
					moves.add(new byte[] { x, y });
				}
				break;
			case UR:
				while (++x < COLS && ++y < ROWS && gameBoard[x][y] == BLANK) {
					moves.add(new byte[] { x, y });
				}
				break;
			case R:
				while (++x < COLS && gameBoard[x][y] == BLANK) {
					moves.add(new byte[] { x, y });
				}
				break;
			case DR:
				while (++x < COLS && --y >= 0 && gameBoard[x][y] == BLANK) {
					moves.add(new byte[] { x, y });
				}
				break;
			case D:
				while (--y >= 0 && gameBoard[x][y] == BLANK) {
					moves.add(new byte[] { x, y });
				}
				break;
			case DL:
				while (--x >= 0 && --y >= 0 && gameBoard[x][y] == BLANK) {
					moves.add(new byte[] { x, y });
				}
				break;
			case L:
				while (--x >= 0 && gameBoard[x][y] == BLANK) {
					moves.add(new byte[] { x, y });
				}
				break;
			case UL:
				while (--x >= 0 && ++y < ROWS && gameBoard[x][y] == BLANK) {
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
	public static SearchTreeNode getPossibleMoves(byte[][] gameBoard, ArrayList<Queen> queens) {
		SearchTreeNode root = new SearchTreeNode(gameBoard);

		for (Queen queen : queens) {
			for (byte[] newQueenPos : ActionFactory.getAvailableTiles(gameBoard, queen.getPosition())) {
				makeTempQueenMove(queen, newQueenPos, gameBoard);
				for (byte[] arrowPos : ActionFactory.getAvailableTiles(gameBoard, newQueenPos)) {
					byte[][] gameState = getNewGameState(queen, gameBoard, newQueenPos, arrowPos);
					
					SearchTreeNode child = new SearchTreeNode(gameState, queen.getPosition(), newQueenPos, arrowPos);
					child.setHeuristicValue((int)(Math.random() * Integer.MAX_VALUE));
					root.add(child);
					
				}
				undoTempQueenMove(queen, newQueenPos, gameBoard);
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
	public static byte[][] getNewGameState(Queen queen, byte[][] gameBoard, byte[] newQueenPos, byte[] arrowPos) {

		byte[] queenPos = queen.getPosition();
		byte[][] newState = new byte[gameBoard.length][gameBoard[0].length];
		for(int i = 0; i < gameBoard.length; i++) {
			for(int j = 0; j < gameBoard[0].length; j++) {
				newState[i][j] = gameBoard[i][j];
			}
		}
		newState[queenPos[0]][queenPos[1]] = BLANK;
		newState[arrowPos[0]][arrowPos[1]] = ARROW;
		newState[newQueenPos[0]][newQueenPos[1]] = queen.getId();

		return newState;

	}
	
}
