package cosc322.milestone1;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

import java.sql.SQLOutput;
import java.util.ArrayList;

public class ActionFactory {

    private ArrayList<byte[]> whiteQueens;
    private ArrayList<byte[]> blackQueens;
    private ArrayList<byte[]> arrows;
    byte[][] boardMatrix;

    ActionFactory(GameBoard gameBoard) {
        boardMatrix = gameBoard.getMatrix();
        whiteQueens = gameBoard.getWhiteQueens();
        blackQueens = gameBoard.getBlackQueens();
        arrows = gameBoard.getArrows();

    }

    public ArrayList<byte[]> getPossibleMoves(boolean isWhitePlayer) {
        ArrayList<byte[]> moves = new ArrayList<>();
        if (isWhitePlayer) {
            for (byte[] queenPos : whiteQueens) {
                moves.addAll(getPossibleMoves(queenPos));
            }
        } else {
            for (byte[] queenPos : blackQueens) {
                moves.addAll(getPossibleMoves(queenPos));
            }
        }

        return moves;
    }

    /**
     * Likely to be replaced with the recursive approach but updated nonetheless.
     * Simply moves in all 8 directions from a position and checks for possible moves.
     *
     * @param pos coordinates of current position?
     * @return the set of all possible moves from pos
     */
    public ArrayList<byte[]> getPossibleMoves(byte[] pos) {
        ArrayList<byte[]> moves = new ArrayList<>();
        byte y = (byte) (pos[0] - 1);
        byte x = (byte) (pos[1] - 1);


        while (++x < Constant.COLS && isBlank(boardMatrix[x][y])) {
            byte[] newPos = new byte[4];
            addMove(pos, moves, y, x, newPos);
        }

        y = (byte) (pos[0] - 1);
        x = (byte) (pos[1] - 1);

        // go left
        while (--x > 0 && isBlank(boardMatrix[x][y])) {
            byte[] newPos = new byte[4];
            addMove(pos, moves, y, x, newPos);
        }


        y = (byte) (pos[0] - 1);
        x = (byte) (pos[1] - 1);

        // go up
        while (++y < Constant.COLS && isBlank(boardMatrix[x][y])) {
            byte[] newPos = new byte[4];
            addMove(pos, moves, y, x, newPos);
        }


        y = (byte) (pos[0] - 1);
        x = (byte) (pos[1] - 1);

        // go down
        while (--y > 0 && isBlank(boardMatrix[x][y])) {
            byte[] newPos = new byte[4];
            addMove(pos, moves, y, x, newPos);
        }


        y = (byte) (pos[0] - 1);
        x = (byte) (pos[1] - 1);

        // go diagonal up right
        while (++y < Constant.ROWS && ++x < Constant.COLS && isBlank(boardMatrix[x][y])) {
            byte[] newPos = new byte[4];
            addMove(pos, moves, y, x, newPos);
        }


        y = (byte) (pos[0] - 1);
        x = (byte) (pos[1] - 1);

        // go diagonal up left
        while (++y < Constant.ROWS && --x > 0 && isBlank(boardMatrix[x][y])) {
            byte[] newPos = new byte[4];
            addMove(pos, moves, y, x, newPos);
        }


        y = (byte) (pos[0] - 1);
        x = (byte) (pos[1] - 1);

        // go diagonal down left
        while (--y > 0 && --x > 0 && isBlank(boardMatrix[x][y])) {
            byte[] newPos = new byte[4];
            addMove(pos, moves, y, x, newPos);
        }


        y = (byte) (pos[0] - 1);
        x = (byte) (pos[1] - 1);

        // go diagonal down right
        while (--y > 0 && ++x < Constant.COLS && isBlank(boardMatrix[x][y])) {
            byte[] newPos = new byte[4];
            addMove(pos, moves, y, x, newPos);
        }
        return moves;
    }

    private void addMove(byte[] pos, ArrayList<byte[]> moves, byte y, byte x, byte[] newPos) {
        newPos[0] = (byte) (y+1);
        newPos[1] = (byte) (x+1);
        newPos[2] = (byte) (pos[0]); // queen original position
        newPos[3] = (byte) (pos[1]);
        moves.add(newPos);
    }

    private boolean isBlank(byte boardMatrix1) {
        return boardMatrix1 == Constant.BLANK;
    }

    public ArrayList<byte[]> getWhiteQueens() {
        return whiteQueens;
    }

    public ArrayList<byte[]> getBlackQueens() {
        return blackQueens;
    }

}