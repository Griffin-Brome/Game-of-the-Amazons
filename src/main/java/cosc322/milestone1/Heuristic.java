package cosc322.milestone1;

public class Heuristic {
    private static final byte U = 1;
    private static final byte UR = 2;
    private static final byte R = 3;
    private static final byte DR = 4;
    private static final byte D = 5;
    private static final byte DL = 6;
    private static final byte L = 7;
    private static final byte UL = 8;

    private static final byte[] directions = {U, UR, R, DR, D, DL, L, UL};
    private byte[][] board; //N x N
    private byte[][] myQueenPositions; //4 x 2
    private byte[][] theirQueenPositions; //4 x 2

    public static final int N = 10; // N in an N x N board
    public static final byte maxMoves = 5; // max number of moves to reach any position

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
            addMatrix(myTerritory, thisQueensTerritory);
        }

        for (byte[] queenPosition : theirQueenPositions) {
            byte[][] thisQueensTerritory = territoryHelper(queenPosition);
            addMatrix(theirTerritory, thisQueensTerritory);
        }

        return reduceMatrix(subMatrix(myTerritory, theirTerritory));
    }

    /**
     * Adds 2 equal sized N x N matrices, saving the result into matrix 'a' (modifies the contents of 'a')
     *
     * @param a the first byte matrix
     * @param b the second byte matrix
     * @return The matrix 'a' that is now equal to a+b
     */
    private byte[][] addMatrix(byte[][] a, byte[][] b) {
        //assume a and b are the same size, N x N
        for (byte row = 0; row < N; row++) {
            for (byte col = 0; col < N; col++) {
                a[row][col] = (byte) (a[row][col] + b[row][col]);
            }
        }
        return a;
    }

    /**
     * Subtracts 2 equal sized N x N matrices, i.e. (a - b),  saving the result into matrix 'a' (modifies the contents of 'a')
     *
     * @param a the first byte matrix
     * @param b the second byte matrix
     * @return The matrix 'a' that is now equal to a-b
     */
    private byte[][] subMatrix(byte[][] a, byte[][] b) {
        //assume a and b are the same size, N x N
        for (byte row = 0; row < N; row++) {
            for (byte col = 0; col < N; col++) {
                a[row][col] = (byte) (a[row][col] - b[row][col]);
            }
        }
        return a;
    }

    /**
     * Adds all entries in a matrix and returns a single value totalling them
     *
     * @param mat the matrix to reduce
     * @return An integer representing the sum of all elements in the matrix
     */
    private int reduceMatrix(byte[][] mat) {
        int total = 0;
        for (byte row = 0; row < N; row++) {
            for (byte col = 0; col < N; col++) {
                total += mat[row][col];
            }
        }
        return total;
    }

    /**
     * Helper function to recursively find the byte[][] territory() of a single queen
     *
     * @param queenPosition the position of this queen on the board
     * @return A byte[][] representing the territory values on each square
     */
    public byte[][] territoryHelper(byte[] queenPosition) {
        //TODO: perhaps call this for each queen and collate their territories
        return territory(U, (byte) 0, queenPosition);
    }

    /**
     * Recursively find a byte[][] territory of this queen
     *
     * @param direction the direction being travelled in in this call
     * @param moveCount the number of moves from the original queen position required to get to the current position
     * @param currPos the current position being visited
     * @return A byte[][] representing the territory values on each square
     */
    public byte[][] territory(byte direction, byte moveCount, byte[] currPos) {
        byte[][] out = new byte[N][N];
        // do this the state-space search way (like Djikstra) with priority queue ordering by moveCount
        for (byte dir : directions) {
            byte[] newPos = newPosition(dir, currPos);
            if (!isValidPosition(newPos)) continue;

            // set the value of this square (i.e. maxMove if reachable in 1 move, maxMove - 1 if reachable in 2 moves, etc)
            out[newPos[0]][newPos[1]] = (byte) Math.max(out[newPos[0]][newPos[1]], maxMoves - moveCount);

            // if this current traversal didn't increase the value of this square, there's no point in continuing
            if (out[newPos[0]][newPos[1]] != maxMoves - moveCount) continue;

            if (dir == direction)
                territory(dir, moveCount, newPos);
            else
                territory(dir, ++moveCount, newPos);
        }

        return out;
    }

    /**
     * Generates a new coordinate position based on the direction to move in and the old coordinate position
     *
     * @param dir the direction being travelled in
     * @param oldPos the old coordinate position
     * @return A byte[] corresponding to the new coordinate position
     */
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

    /**
     * Checks if this position is valid on the board (i.e. is not populated by a queen of either player or an arrow)
     *
     * @param position the position to be checked
     * @return If this position is on the board or not. True -> "This position is within the bounds of the board"
     */
    private boolean isValidPosition(byte[] position) {
        return position[0] >= 1 && position[0] <= N && position[1] >= 1 && position[1] <= N && !isOccupied(position);
    }

    /**
     * Specifically checks if this position is previously occupied on the board (i.e. is populated by a queen of either player or an arrow)
     *
     * @param position the position to be checked
     * @return If this position is free or not. True -> "This position is free".
     */
    private boolean isOccupied(byte[] position) {
        return board[position[0]][position[1]] != 0;
    }
}
