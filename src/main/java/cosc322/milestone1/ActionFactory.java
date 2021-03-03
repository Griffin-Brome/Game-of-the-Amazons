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
	public static ArrayList<byte[]> getMoves(GameBoard gameBoard, byte[] pos) {
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

}
