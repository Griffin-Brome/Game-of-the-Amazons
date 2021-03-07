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
    ArrayList<Move> moves;

    ActionFactoryRecursive(GameBoard gameBoard) {
        boardMatrix = gameBoard.getMatrix();
        whiteQueens = gameBoard.getWhiteQueens();
        blackQueens = gameBoard.getBlackQueens();
        arrows = gameBoard.getArrows();
        moves = new ArrayList<>();

        //TODO: remove this test print string
        System.out.println(Arrays.deepToString(boardMatrix));
    }

    public ArrayList<Move> getPossibleMoves(boolean isWhitePlayer) {
        ArrayList<Move> moves = new ArrayList<>();
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
    public ArrayList<Move> getPossibleMoves(byte[] oldPos) {
        ArrayList<byte[]> possibleMoves = generateMovesHelper(oldPos);

        for (byte[] possibleMove : possibleMoves) {
            addMove(oldPos, possibleMove);
        }

        return moves;
    }

    private void addMove(byte[] oldPos, byte[] newMove) {
        Move move = new Move(oldPos);
        move.setNewPos(new byte[] { newMove[0], newMove[1] });
        move.setArrowPos(new byte[] { newMove[2], newMove[3] });

        moves.add(move);
    }

    public ArrayList<byte[]> generateMovesHelper(byte[] currPos) {
        ArrayList<byte[]> possibleMoves = new ArrayList<>();
        for (byte dir : DIRECTIONS) {
            byte[] newPos = _generateNewPosition(currPos.clone(), dir);
            generateMoves(dir, newPos, possibleMoves);
        }
        return possibleMoves;
    }

    public void generateMoves(byte dir, byte[] currPos, ArrayList<byte[]> possibleMoves) {
        ArrayList<byte[]> possibleArrowPositions = generateMovesHelper(currPos.clone());
        for(byte[] arrowPos: possibleArrowPositions) {
            // add the current move to the set of all possible moves
            possibleMoves.add(new byte[] {currPos[0], currPos[1], arrowPos[0], arrowPos[1]});
        }

        byte[] newPos = _generateNewPosition(currPos.clone(), dir);

        // if the new position is valid, explore it
        if (_isValidPosition(boardMatrix, currPos))
            generateMoves(dir, newPos, possibleMoves);
    }
}