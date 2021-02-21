package cosc322.milestone1;

public class Heuristic {
    private final static byte U = 1;
    private final static byte UR = 2;
    private final static byte R = 3;
    private final static byte DR = 4;
    private final static byte D = 5;
    private final static byte DL = 6;
    private final static byte L = 7;
    private final static byte UL = 8;

    private final static byte[] directions = {U, UR, R, DR, D, DL, L, UL};
    private byte[][] board; //N x N
    private byte[][] queenPositions; //8 x 2

    public final static int N = 10;

    public Heuristic(byte[][] board) {
        this.board = board;
        //TODO: intialize byte[][] to store queen positions
    }

    public static int territoryHelper(byte[] queenPosition) {
        //TODO: perhaps call this for each queen and collate their territories
        return territory(U, 0, queenPosition);
    }

    /**
     * @param direction
     * @param moveCount
     * @param currPos
     * @return
     */
    public static int territory(byte direction, int moveCount, byte[] currPos) {
        // do this the state-space search way (like Djikstra) with priority queue ordering by moveCount
        for (byte dir : directions) {
            byte[] newPos = newPosition(dir, currPos);
            if (!isValidPosition(newPos)) continue;
            if(isVisited(newPos)) continue;
            if (dir == direction)
                territory(dir, moveCount, newPos);
            else
                territory(dir, ++moveCount, newPos);
        }

        return 1;
    }

    /**
     * @param dir
     * @param oldPos
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
     * Checks if this position is valid on the board
     * @param position
     * @return
     */
    public static boolean isValidPosition(byte[] position) {
        return position[0] >= 1 && position[0] <= N && position[1] >= 1 && position[1] <= N && !isOccupied(position);
    }

    /**
     * Specifically checks if this position is previously occupied on the board
     * @param position
     * @return
     */
    public static boolean isOccupied(byte[] position) {
        //TODO: check if space is occupied given the board
        return false;

    }
}
