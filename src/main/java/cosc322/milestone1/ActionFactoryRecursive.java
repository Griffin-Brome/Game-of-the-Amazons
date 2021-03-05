package cosc322.milestone1;

//import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;

public class ActionFactoryRecursive {
    private static final byte U = 1;
    private static final byte UR = 2;
    private static final byte R = 3;
    private static final byte DR = 4;
    private static final byte D = 5;
    private static final byte DL = 6;
    private static final byte L = 7;
    private static final byte UL = 8;

    private static final byte[] directions = {U, UR, R, DR, D, DL, L, UL};

    public static final int N = 10; // N in an N x N board

    private ArrayList<byte[]> whiteQueens;
    private ArrayList<byte[]> blackQueens;
    private ArrayList<byte[]> arrows;
    byte[][] boardMatrix;
    ArrayList<byte[]> moves;

    ActionFactoryRecursive(GameBoard gameBoard) {
        boardMatrix = gameBoard.getMatrix();
        whiteQueens = gameBoard.getWhiteQueens();
        blackQueens = gameBoard.getBlackQueens();
        arrows = gameBoard.getArrows();
        moves = new ArrayList<>();

        System.out.println(Arrays.deepToString(boardMatrix));
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
     * @param oldPos coordinates of current position?
     * @return the set of all possible moves from pos
     */
    public ArrayList<byte[]> getPossibleMoves(byte[] oldPos) {
        ArrayList<byte[]> possibleMoves = generateMovesStart(oldPos);
//        System.out.println(Arrays.deepToString(possibleMoves.toArray()));


        for (byte[] possibleMove : possibleMoves) {
            addMove(oldPos, possibleMove[1], possibleMove[0]);
        }

        return moves;
    }

    private void addMove(byte[] oldPos, byte newPosY, byte newPosX) {
        byte[] move = new byte[4];
        // the "+1" is because the server indexes from 1-10 and we index from 0-9
        // the 2 lines after this represent the new position coordinates of the queen
        move[0] = (byte) (newPosY + 1);
        move[1] = (byte) (newPosX + 1);
        // queen original position
        move[2] = oldPos[0];
        move[3] = oldPos[1];

        moves.add(move);
    }

    public ArrayList<byte[]> generateMovesStart(byte[] currPos) {
        ArrayList<byte[]> possibleMoves = new ArrayList<>();
        for (byte dir : directions) {
            byte[] newPos = newPosition(dir, currPos.clone());
            _generateMoves(dir, newPos, possibleMoves);
        }
        return possibleMoves;
    }

    public void _generateMoves(byte direction, byte[] currPos, ArrayList<byte[]> possibleMoves) {
        if (!isValidPosition(currPos)) return;
        possibleMoves.add(currPos);
        byte[] newPos = newPosition(direction, currPos.clone());
        _generateMoves(direction, newPos, possibleMoves);

    }

    public static byte[] newPosition(byte dir, byte[] oldPos) {
        switch (dir) {
            case U:
                return new byte[]{oldPos[0], --oldPos[1]};
            case UR:
                return new byte[]{++oldPos[0], --oldPos[1]};
            case R:
                return new byte[]{++oldPos[0], oldPos[1]};
            case DR:
                return new byte[]{++oldPos[0], ++oldPos[1]};
            case D:
                return new byte[]{oldPos[0], ++oldPos[1]};
            case DL:
                return new byte[]{--oldPos[0], ++oldPos[1]};
            case L:
                return new byte[]{--oldPos[0], oldPos[1]};
            case UL:
                return new byte[]{--oldPos[0], --oldPos[1]};
            default:
                return new byte[]{-1, -1};
        }
    }

    private boolean isValidPosition(byte[] position) {
        return position[0] >= 0 && position[0] < N && position[1] >= 0 && position[1] < N && !isOccupied(position);
    }

    private boolean isOccupied(byte[] position) {
        return boardMatrix[position[0]][position[1]] != 0;
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