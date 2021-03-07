package cosc322.milestone1;

import static utils.Constant.*;
import static utils.GameLogic.*;

import java.util.ArrayList;
import java.util.Arrays;

public class ActionFactoryRecursive {
    private ArrayList<Queen> whiteQueens;
    private ArrayList<Queen> blackQueens;
    private ArrayList<Arrow> arrows;
    byte[][] boardMatrix;
    ArrayList<byte[]> moves;

    ActionFactoryRecursive(GameBoard gameBoard) {
        boardMatrix = gameBoard.getMatrix();
        whiteQueens = gameBoard.getWhiteQueens();
        blackQueens = gameBoard.getBlackQueens();
        arrows = gameBoard.getArrows();
        moves = new ArrayList<>();

        //TODO: remove this test print string
        System.out.println(Arrays.deepToString(boardMatrix));
    }

    public ArrayList<byte[]> getPossibleMoves(boolean isWhitePlayer) {
        ArrayList<byte[]> moves = new ArrayList<>();
        ArrayList<Queen> ourQueens = isWhitePlayer ? whiteQueens : blackQueens;

        for (Queen queen : ourQueens) {
            moves.addAll(getPossibleMoves(queen.getPosition()));
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
        ArrayList<byte[]> possibleMoves = generateMovesHelper(oldPos);

        for (byte[] possibleMove : possibleMoves) {
            addMove(oldPos, possibleMove[0], possibleMove[1]);
        }

        return moves;
    }

    private void addMove(byte[] oldPos, byte newPosX, byte newPosY) {
        byte[] move = new byte[4];
        // the "+1" is because the server indexes from 1-10 and we index from 0-9
        // the 2 lines after this represent the new position coordinates of the queen
        move[0] = newPosX;
        move[1] = newPosY;
        // queen original position
        move[2] = oldPos[0];
        move[3] = oldPos[1];

        moves.add(move);
    }

    public ArrayList<byte[]> generateMovesHelper(byte[] currPos) {
        ArrayList<byte[]> possibleMoves = new ArrayList<>();
        for (byte dir : DIRECTIONS) {
            byte[] newPos = generateNewPosition(currPos.clone(), dir);
            _generateMoves(dir, newPos, possibleMoves);
        }
        return possibleMoves;
    }

    public void _generateMoves(byte dir, byte[] currPos, ArrayList<byte[]> possibleMoves) {
        // add the current move to the set of all possible moves
        possibleMoves.add(currPos);
        byte[] newPos = generateNewPosition(currPos.clone(), dir);

        // if the new position is valid, explore it
        if (isValidPosition(boardMatrix, currPos))
            _generateMoves(dir, newPos, possibleMoves);
    }
}