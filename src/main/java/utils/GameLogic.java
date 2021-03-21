package utils;

import models.Move;
import models.Queen;

import java.util.ArrayList;

import static utils.Constant.*;
import static utils.MatrixOperations._cloneMatrix;

public class GameLogic {
    /**
     * Generates a new coordinate position based on the direction to move in and the old coordinate position
     *
     * @param dir    the direction being travelled in
     * @param original the old coordinate position
     * @return A byte[] corresponding to the new coordinate position
     */
    public static byte[] _generateNewPosition(byte[] original, byte dir) {
        byte[] oldPos = original.clone();
        switch (dir) {
            case U:
                return new byte[]{--oldPos[0], oldPos[1]};
            case UR:
                return new byte[]{--oldPos[0], ++oldPos[1]};
            case R:
                return new byte[]{oldPos[0], ++oldPos[1]};
            case DR:
                return new byte[]{++oldPos[0], ++oldPos[1]};
            case D:
                return new byte[]{++oldPos[0], oldPos[1]};
            case DL:
                return new byte[]{++oldPos[0], --oldPos[1]};
            case L:
                return new byte[]{oldPos[0], --oldPos[1]};
            case UL:
                return new byte[]{--oldPos[0], --oldPos[1]};
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
     * Checks if this position is valid on the board (i.e. is not populated by a queen of either player or an arrow)
     *
     * @param position the position to be checked as an arraylist of 2 elements
     * @return If this position is on the board or not. True -> "This position is within the bounds of the board"
     */
    public static boolean _isValidPosition(byte[][] board, ArrayList<Integer> position) {
        byte[] convertedPosition = _arrayListToByteArray(position);
        return _isValidPosition(board, convertedPosition);
    }

    /**
     * Converts an integer array list into a byte array of the same length
     *
     * @param list the ArrayList to be converted
     * @return a byte array with the same element values of this ArrayList
     */
    public static byte[] _arrayListToByteArray(ArrayList<Integer> list) {
        byte[] result = new byte[list.size()];
        byte i = 0;

        for (int value : list)
            result[i++] = (byte) value;

        return result;
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

    /**
     * Read a board matrix to create a queens list based on player
     * @param board the board matrix
     * @param isWhitePlayer indicates if we want the white (true) or black (false) queens
     * @return an arraylist of Queens
     */
    public static ArrayList<Queen> _queensFromBoard(byte[][] board, boolean isWhitePlayer) {
        ArrayList<Queen> queens = new ArrayList<>();
        for (byte row = 0; row < N; row++) {
            for (byte col = 0; col < N; col++) {
                if(board[row][col] == (isWhitePlayer ? WHITE_QUEEN : BLACK_QUEEN)) {
                    Queen q = new Queen(new byte[]{row, col}, (byte) 1);
                    queens.add(q);
                }
            }
        }
        return queens;
    }

    public static byte[][] _makeTempMove(byte[][] oldBoard, Move move) {
        byte[][] newBoard = _cloneMatrix(oldBoard);
        byte[] oldPos = move.getOldPos();
        byte[] newPos = move.getNewPos();
        byte[] arrowPos = move.getArrowPos();

        newBoard[oldPos[0]][oldPos[1]] = BLANK;
        newBoard[newPos[0]][newPos[1]] = oldBoard[oldPos[0]][oldPos[1]];
        newBoard[arrowPos[0]][arrowPos[1]] = ARROW;

        return newBoard;
    }
}
