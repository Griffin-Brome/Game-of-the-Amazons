package utils;

import static utils.Constant.*;

public class GameLogic {
    /**
     * Generates a new coordinate position based on the direction to move in and the old coordinate position
     *
     * @param dir    the direction being travelled in
     * @param oldPos the old coordinate position
     * @return A byte[] corresponding to the new coordinate position
     */
    public static byte[] _generateNewPosition(byte[] oldPos, byte dir) {
        switch (dir) {
            case U:
                return new byte[]{oldPos[0], ++oldPos[1]};
            case UR:
                return new byte[]{++oldPos[0], ++oldPos[1]};
            case R:
                return new byte[]{++oldPos[0], oldPos[1]};
            case DR:
                return new byte[]{++oldPos[0], --oldPos[1]};
            case D:
                return new byte[]{oldPos[0], --oldPos[1]};
            case DL:
                return new byte[]{--oldPos[0], --oldPos[1]};
            case L:
                return new byte[]{--oldPos[0], oldPos[1]};
            case UL:
                return new byte[]{--oldPos[0], ++oldPos[1]};
            default:
                return new byte[]{-1, -1};
        }
    }

    /**
     * Checks if this position is valid on the board (i.e. is not populated by a queen of either player or an arrow)
     *
     * @param position the position to be checked
     * @return If this position is on the board or not. True -> "This position is within the bounds of the board"
     */
    public static boolean _isValidPosition(byte[][] board, byte[] position) {
        return position[0] >= 0 && position[0] < N && position[1] >= 0 && position[1] < N && !_isOccupied(board, position);
    }

    /**
     * Specifically checks if this position is previously occupied on the board (i.e. is populated by a queen of either player or an arrow)
     *
     * @param position the position to be checked
     * @return If this position is free or not. True -> "This position is occupied".
     */
    public static boolean _isOccupied(byte[][] board, byte[] position) {
        return board[position[0]][position[1]] != BLANK;
    }
}