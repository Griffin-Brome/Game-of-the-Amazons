package decision.logic;

import cosc322.amazons.ActionFactoryRecursive;
import cosc322.amazons.GameBoard;
import models.Move;
import models.Queen;

import java.util.ArrayList;

import static utils.Constant.*;
import static utils.MatrixOperations.*;


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
        ActionFactoryRecursive af = new ActionFactoryRecursive(gameBoard, isWhitePlayer);
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
    public static byte[][] getNewGameState(Queen queen, byte[][] boardMatrix, byte[] newQueenPos, byte[] arrowPos) {
        byte[] queenPos = queen.getPosition();
        byte[][] newState = _cloneMatrix(boardMatrix);

        newState[queenPos[0]][queenPos[1]] = BLANK;
        newState[arrowPos[0]][arrowPos[1]] = ARROW;
        newState[newQueenPos[0]][newQueenPos[1]] = queen.getId();

        return newState;

    }

}
