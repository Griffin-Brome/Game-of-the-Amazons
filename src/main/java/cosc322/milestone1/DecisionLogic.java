package cosc322.milestone1;

import java.util.ArrayList;

import static utils.Constant.*;


public class DecisionLogic {
    byte[][] boardMatrix;
    GameBoard gameBoard;
    boolean isWhitePlayer;

    DecisionLogic(GameBoard gameBoard, boolean isWhitePlayer) {
        boardMatrix = gameBoard.getMatrix();
        this.gameBoard = gameBoard;
        this.isWhitePlayer = isWhitePlayer;
    }

    /**
     * Returns a SearchTreeNode who's children are all possible moves available for a given player.
     * @param boardMatrix
     * @param queens
     * @return
     */
    public SearchTreeNode getPossibleMoves(byte[][] boardMatrix, ArrayList<Queen> queens) {
        ActionFactoryRecursive af = new ActionFactoryRecursive(gameBoard);
        SearchTreeNode root = new SearchTreeNode(boardMatrix, isWhitePlayer);

        for (Queen queen : queens) {
            for (Move move : af.getPossibleMoves(queen.getPosition())) {
                byte[][] gameState = getNewGameState(queen, boardMatrix, move.getNewPos(), move.getArrowPos());

                SearchTreeNode child = new SearchTreeNode(gameState, queen.getPosition(), move.getNewPos(), move.getArrowPos());

                //TODO: get the real heuristic value
                child.setHeuristicValue((int) (Math.random() * Integer.MAX_VALUE));
                root.add(child);
            }
        }
        return root;
    }


    /**
     * Move the queen to a position in the matrix, moving it out of the way for the
     * arrow shot.
     *
     * @param queen
     * @param tempQueenPos
     */
    public static void makeTempQueenMove(Queen queen, byte[] tempQueenPos, byte[][] matrix) {
        byte[] queenPos = queen.getPosition();
        matrix[queenPos[0]][queenPos[1]] = BLANK;
        matrix[tempQueenPos[0]][tempQueenPos[1]] = queen.getId();
    }


    /**
     * Move the queen back after checking the arrow shot.
     *
     * @param queen
     * @param tempQueenPos
     */
    public static void undoTempQueenMove(Queen queen, byte[] tempQueenPos, byte[][] matrix) {
        byte[] queenPos = queen.getPosition();
        matrix[queenPos[0]][queenPos[1]] = queen.getId();
        matrix[tempQueenPos[0]][tempQueenPos[1]] = BLANK;
    }

    /**
     * Move the queen and arrow to a position in the matrix.
     *
     * @param queen
     * @param tempQueenPos
     */
    public static void makeTempMove(Queen queen, byte[] tempQueenPos, byte[] arrowPos, byte[][] matrix) {
        byte[] queenPos = queen.getPosition();
        matrix[queenPos[0]][queenPos[1]] = BLANK;
        matrix[tempQueenPos[0]][tempQueenPos[1]] = queen.getId();
        matrix[arrowPos[0]][arrowPos[1]] = ARROW;
    }

    /**
     * Move the queen back and remove arrow
     *
     * @param queen
     * @param tempQueenPos
     */
    public static void undoTempMove(Queen queen, byte[] tempQueenPos, byte[] arrowPos, byte[][] matrix) {
        byte[] queenPos = queen.getPosition();
        matrix[queenPos[0]][queenPos[1]] = queen.getId();
        matrix[tempQueenPos[0]][tempQueenPos[1]] = BLANK;
        matrix[arrowPos[0]][arrowPos[1]] = BLANK;
    }

    /**
     * Generate the matrix for a given queen, new position and arrow position.
     *
     * @param queen
     * @param newQueenPos
     * @param arrowPos
     * @return
     */
    public static byte[][] getNewGameState(Queen queen, byte[][] gameBoard, byte[] newQueenPos, byte[] arrowPos) {

        byte[] queenPos = queen.getPosition();
        byte[][] newState = new byte[gameBoard.length][gameBoard[0].length];
        for (int i = 0; i < gameBoard.length; i++) {
            for (int j = 0; j < gameBoard[0].length; j++) {
                newState[i][j] = gameBoard[i][j];
            }
        }
        newState[queenPos[0]][queenPos[1]] = BLANK;
        newState[arrowPos[0]][arrowPos[1]] = ARROW;
        newState[newQueenPos[0]][newQueenPos[1]] = queen.getId();

        return newState;

    }

}
