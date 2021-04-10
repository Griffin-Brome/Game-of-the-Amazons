package decision.logic;

import models.Queen;
import models.TerritoryState;

import java.util.ArrayList;
import java.util.LinkedList;

import static utils.Constant.*;
import static utils.MatrixOperations.*;
import static utils.GameLogic.*;

public class Heuristic {
    private final byte[][] board; //N x N
    private final ArrayList<Queen> myQueenPositions; //4 x 2
    private final ArrayList<Queen> theirQueenPositions; //4 x 2

    private byte maxDepth = Byte.MAX_VALUE;

    // this constructor is used for temporary board states, which are encoded as byte matrices
    public Heuristic(byte[][] board, boolean isWhitePlayer) {
        this.board = board;
        this.myQueenPositions = _queensFromBoard(board, isWhitePlayer);
        this.theirQueenPositions = _queensFromBoard(board, !isWhitePlayer);
    }

    public Heuristic(byte[][] board, boolean isWhitePlayer, byte maxDepth) {
        this(board, isWhitePlayer);
        this.maxDepth = maxDepth;
    }

    public int getUtility() {
        return 8 * territoryHeuristic() + 2 * mobilityHeuristic() + 10 * killSaveQueens();
    }

    public int killSaveQueens() {
        int total = 0;
        for (Queen queen : myQueenPositions) {
            byte[] oldPos = queen.getPosition();
            boolean free = false;
            for (byte dir : DIRECTIONS) {
                byte[] newPos = _generateNewPosition(oldPos, dir);
                if (_isValidPosition(board, newPos)) {
                    free = true;
                    break;
                }
            }
            if (free) total += 1;
        }

        for (Queen queen : theirQueenPositions) {
            byte[] oldPos = queen.getPosition();
            boolean free = false;
            for (byte dir : DIRECTIONS) {
                byte[] newPos = _generateNewPosition(oldPos, dir);
                if (_isValidPosition(board, newPos)) {
                    free = true;
                    break;
                }
            }
            if (free) total -= 1;
        }

        return total;
    }

    public int immediateMovesHeuristic() {
        int total = 0;
        for (Queen queen : myQueenPositions) {
            byte[] oldPos = queen.getPosition();
            for (byte dir : DIRECTIONS) {
                byte[] newPos = _generateNewPosition(oldPos, dir);
                if (_isValidPosition(board, newPos)) {
                    total += 1;
                }
            }
        }

        for (Queen queen : theirQueenPositions) {
            byte[] oldPos = queen.getPosition();
            for (byte dir : DIRECTIONS) {
                byte[] newPos = _generateNewPosition(oldPos, dir);
                if (_isValidPosition(board, newPos)) {
                    total -= 1;
                }
            }
        }
        return total;
    }

    /**
     * Calculates the 'territory heuristic' for this board state
     *
     * @return An integer that encodes the board territory control as a value
     */
    public int mobilityHeuristic() {
        byte[][] ours = new byte[N][N];
        byte[][] theirs = new byte[N][N];

        for (Queen queen : myQueenPositions) {
            byte[] oldPos = queen.getPosition();
            for (byte dir : DIRECTIONS) {
                byte[] newPos = _generateNewPosition(oldPos, dir);
                while (_isValidPosition(board, newPos)) {
                    ours[newPos[0]][newPos[1]] = (byte) Math.max(1, ours[newPos[0]][newPos[1]]);
                    newPos = _generateNewPosition(newPos, dir);
                }
            }
        }

        for (Queen queen : theirQueenPositions) {
            byte[] oldPos = queen.getPosition();
            for (byte dir : DIRECTIONS) {
                byte[] newPos = _generateNewPosition(oldPos, dir);
                while (_isValidPosition(board, newPos)) {
                    theirs[newPos[0]][newPos[1]] = (byte) Math.max(1, theirs[newPos[0]][newPos[1]]);
                    newPos = _generateNewPosition(newPos, dir);
                }
            }
        }

        return _reduceMatrix(_subMatrix(ours, theirs)) + 1;
    }

    /**
     * Calculates the 'territory heuristic' for this board state - Positive numbers mean more board control
     * (relative to the player Heuristic is constructed with)
     *
     * @return An integer that represents the board territory control as a value.
     */
    public int territoryHeuristic() {
        byte[][] myTerritory = new byte[N][N];
        _initializeMatrix(myTerritory, Byte.MAX_VALUE);
        byte[][] theirTerritory = new byte[N][N];
        _initializeMatrix(theirTerritory, Byte.MAX_VALUE);

        for (Queen queen : myQueenPositions) {
            territoryHelper(queen.getPosition(), myTerritory);
        }

        for (Queen queen : theirQueenPositions) {
            territoryHelper(queen.getPosition(), theirTerritory);
        }

        return territoryCompare(myTerritory, theirTerritory);
    }

    /**
     * Helper function to recursively find the byte[][] territory matrix of a single queen
     *
     * @param queenPosition the position of this queen on the board
     */
    public void territoryHelper(byte[] queenPosition, byte[][] out) {
        out[queenPosition[0]][queenPosition[1]] = (byte) 0;
        LinkedList<TerritoryState> queue = new LinkedList<>();

        for (byte dir : DIRECTIONS) {
            byte[] curr = queenPosition.clone();
            byte[] newPos = _generateNewPosition(curr, dir);
            if (_isValidPosition(board, newPos))
                queue.add(new TerritoryState(dir, (byte) 1, newPos));
        }

        while (!queue.isEmpty()) {
            TerritoryState curr = queue.removeLast();
            byte[] currPos = curr.getCurrPos();
            byte moveCount = curr.getMoveCount();

            out[currPos[0]][currPos[1]] = (byte) Math.min(
                    out[currPos[0]][currPos[1]],
                    moveCount
            );

            // if this current traversal didn't decrease the value of this square, there's no point in continuing
            if (out[currPos[0]][currPos[1]] < moveCount || out[currPos[0]][currPos[1]] == 0) continue;
            // if we're deeper in exploration than we planned to be, continue
            if (moveCount >= this.maxDepth) continue;

            for (byte dir : DIRECTIONS) {
                byte[] newPos = _generateNewPosition(curr.getCurrPos().clone(), dir);
                if (!_isValidPosition(board, newPos)) continue;

                // only increment the moveCount in the new state if the direction has changed
                if (dir == curr.getDir())
                    queue.add(new TerritoryState(dir, moveCount, newPos));
                else
                    queue.add(new TerritoryState(dir, (byte) (moveCount + 1), newPos));
            }
        }
    }

    public int territoryCompare(byte[][] ours, byte[][] theirs) {
        int total = 0;
        for (byte row = 0; row < N; row++) {
            for (byte col = 0; col < N; col++) {
                if (ours[row][col] == theirs[row][col]) continue;
                total += ours[row][col] < theirs[row][col] ? 1 : -1;
            }
        }
        return total;
    }
}
