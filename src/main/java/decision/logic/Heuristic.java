package decision.logic;

import static utils.Constant.*;
import static utils.MatrixOperations.*;
import static utils.GameLogic.*;

public class Heuristic {
    private final byte[][] board; //N x N
    private final byte[][] myQueenPositions; //4 x 2
    private final byte[][] theirQueenPositions; //4 x 2

    private static final byte maxMoves = 30; // max number of moves to reach any position

    public Heuristic(byte[][] board, byte[][] myQueenPositions, byte[][] theirQueenPositions) {
        this.board = board;
        this.myQueenPositions = myQueenPositions;
        this.theirQueenPositions = theirQueenPositions;
        //TODO: should take arraylists of queen positions and convert into the 2D byte arrays
    }

    /**
     * Calculates the 'territory heuristic' for this board state
     *
     * @return An integer that encodes the board territory control as a value
     */
    public int territoryHeuristic() {
        byte[][] myTerritory = new byte[N][N];
        byte[][] theirTerritory = new byte[N][N];

        for (byte[] queenPosition : myQueenPositions) {
            byte[][] thisQueensTerritory = territoryHelper(queenPosition);
            myTerritory = _addMatrix(myTerritory, thisQueensTerritory);
        }

        for (byte[] queenPosition : theirQueenPositions) {
            byte[][] thisQueensTerritory = territoryHelper(queenPosition);
            theirTerritory = _addMatrix(theirTerritory, thisQueensTerritory);
        }

        byte[][] out = _subMatrix(myTerritory, theirTerritory);
        return _reduceMatrix(out);
    }

    /**
     * Helper function to recursively find the byte[][] territory() of a single queen
     *
     * @param queenPosition the position of this queen on the board
     * @return A byte[][] representing the territory values on each square
     */
    public byte[][] territoryHelper(byte[] queenPosition) {
        byte[][] out = new byte[N][N];
        territory(U, (byte) 1, queenPosition, out);
        return out;
    }

    /**
     * Recursively find a byte[][] territory of this queen (for the first square, i.e. the current queen's position)
     *
     * @param direction the direction being travelled in in this call
     * @param moveCount the number of moves from the original queen position required to get to the current position
     * @param currPos   the current position being visited
     */
    public void territory(byte direction, byte moveCount, byte[] currPos, byte[][] out) {
        // set the value of this square (i.e. maxMove if reachable in 1 move, maxMove - 1 if reachable in 2 moves, etc)
        out[currPos[0]][currPos[1]] = (byte) Math.max(out[currPos[0]][currPos[1]], maxMoves - moveCount);

        for (byte dir : DIRECTIONS) {
            byte[] curr = currPos.clone();
            byte[] newPos = _generateNewPosition(curr, dir);
            _territory(dir, moveCount, newPos, out);
        }
    }

    /**
     * Recursively populate a byte[][] of this queen's weighted territory
     *
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
