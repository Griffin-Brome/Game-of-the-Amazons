package decision.logic;

import cosc322.amazons.GameBoard;
import models.Queen;

import java.util.ArrayList;

import static utils.Constant.*;
import static utils.MatrixOperations.*;
import static utils.GameLogic.*;

public class Heuristic {
    private final byte[][] board; //N x N
    private final ArrayList<Queen> myQueenPositions; //4 x 2
    private final ArrayList<Queen> theirQueenPositions; //4 x 2

    private static final byte maxMoves = 30; // max number of moves to reach any position

    public Heuristic(GameBoard gameBoard, boolean isWhitePlayer) {
        this.board = gameBoard.getMatrix();
        this.myQueenPositions = isWhitePlayer ? gameBoard.getWhiteQueens() : gameBoard.getBlackQueens();
        this.theirQueenPositions = isWhitePlayer ? gameBoard.getBlackQueens() : gameBoard.getWhiteQueens();
    }

    // this constructor is used for temporary board states, which are encoded as byte matrices
    public Heuristic(byte[][] board, boolean isWhitePlayer) {
        this.board = board;
        this.myQueenPositions = _queensFromBoard(board, isWhitePlayer);
        this.theirQueenPositions = _queensFromBoard(board, !isWhitePlayer);
    }

    public int getUtility() {
//        return territoryHeuristic();
        return mobilityHeuristic();
        //TODO: remove this random utility thing
//        return (int) (1 + Math.random() * 100);
    }

    /**
     * Calculates the 'territory heuristic' for this board state
     * @return An integer that encodes the board territory control as a value
     */
    public byte mobilityHeuristic() {
        byte[][] out = new byte[N][N];

        for (Queen queen : myQueenPositions) {
            byte[] oldPos = queen.getPosition();
            for (byte dir : DIRECTIONS) {
                byte[] newPos = _generateNewPosition(oldPos, dir);
                while (_isValidPosition(board, newPos)){
                    out[newPos[0]][newPos[1]] = 1;
                    newPos = _generateNewPosition(newPos, dir);
                }
            }
        }

        return (byte) (_reduceMatrix(out) + 1);
    }

    /**
     * Calculates the 'territory heuristic' for this board state
     * @return An integer that encodes the board territory control as a value
     */
    public int territoryHeuristic() {
        byte[][] myTerritory = new byte[N][N];
        byte[][] theirTerritory = new byte[N][N];

        for (Queen queen : myQueenPositions) {
            byte[][] thisQueensTerritory = territoryHelper(queen.getPosition());
            myTerritory = _addMatrix(myTerritory, thisQueensTerritory);
        }

        for (Queen queen : theirQueenPositions) {
            byte[][] thisQueensTerritory = territoryHelper(queen.getPosition());
            theirTerritory = _addMatrix(theirTerritory, thisQueensTerritory);
        }

        byte[][] out = _subMatrix(myTerritory, theirTerritory);
        return _reduceMatrix(out);
    }

    /**
     * Helper function to recursively find the byte[][] territory() of a single queen
     * @param queenPosition the position of this queen on the board
     * @return A byte[][] representing the territory values on each square
     */
    public byte[][] territoryHelper(byte[] queenPosition) {
        byte[][] out = new byte[N][N];
        out[queenPosition[0]][queenPosition[1]] = (byte) Math.max(out[queenPosition[0]][queenPosition[1]], maxMoves);

        for (byte dir : DIRECTIONS) {
            byte[] curr = queenPosition.clone();
            byte[] newPos = _generateNewPosition(curr, dir);
            _territory(dir, (byte) 1, newPos, out);
        }
        return out;
    }

    /**
     * Recursively populate a byte[][] of this queen's weighted territory
     * @param direction the direction being travelled in in this call
     * @param moveCount the number of moves from the original queen position required to get to the current position
     * @param currPos   the current position being visited
     */
    public void _territory(byte direction, byte moveCount, byte[] currPos, byte[][] out) {
        if (!_isValidPosition(board, currPos)) return;

        // set the value of this square (i.e. maxMove if reachable in 1 move, maxMove - 1 if reachable in 2 moves, etc)
        out[currPos[0]][currPos[1]] = (byte) Math.max(out[currPos[0]][currPos[1]], maxMoves - moveCount);

        // if this current traversal didn't increase the value of this square, there's no point in continuing
        if (out[currPos[0]][currPos[1]] != maxMoves - moveCount) return;

        for (byte dir : DIRECTIONS) {
            byte[] curr = currPos.clone();
            byte[] newPos = _generateNewPosition(curr, dir);

            if (dir == direction)
                _territory(dir, moveCount, newPos, out);
            else
                _territory(dir, (byte) (moveCount+1), newPos, out);
        }
    }
}
