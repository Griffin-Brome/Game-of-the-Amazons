package cosc322.amazons;

import java.util.ArrayList;

import models.Arrow;
import models.Queen;

import static utils.Constant.*;
import static utils.GameLogic.*;
import static utils.MatrixOperations._makeMatrix;

/**
 * gameState from server stored in matrix :
 *      [Y] 0  1  2  3  4  5  6  7  8  9
 *
 * [X] 0    0, 0, 0, 2, 0, 0, 2, 0, 0, 0,
 * 1	    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
 * 2	    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
 * 3        2, 0, 0, 0, 0, 0, 0, 0, 0, 2,
 * 4	    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
 * 5 	    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
 * 6	    1, 0, 0, 0, 0, 0, 0, 0, 0, 1,
 * 7	    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
 * 8	    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
 * 9        0, 0, 0, 1, 0, 0, 1, 0, 0, 0,
 * <p>
 * Where 1 represents white queen, 2 represents black queen and 3 are arrows.
 * The server thinks bottom left is [1, 1] and top right is [10, 10]
 * We use bottom left as [9, 0] and top right as [0, 9] -> i.e. our positions correlate to the index in the matrix
 */
public class GameBoard {
    // Encoding on game state from server :
    private ArrayList<Integer> gameState;
    private byte[][] boardMatrix;
    private ArrayList<Arrow> arrows;
    private ArrayList<Queen> whiteQueens;
    private ArrayList<Queen> blackQueens;

    public GameBoard() {
        boardMatrix = new byte[ROWS][COLS];
        whiteQueens = new ArrayList<>();
        blackQueens = new ArrayList<>();
        arrows = new ArrayList<>();
    }

    /**
     * Updates the coordinates HashMap and matrix
     * Prints some debugging statements
     */
    public void updateBoard(ArrayList<Integer> queenPosCurr, ArrayList<Integer> queenPosNext, ArrayList<Integer> arrowPos) {
        moveQueen(queenPosCurr, queenPosNext);
        shootArrow(arrowPos);
    }

    /**
     * Moves queen from position A to B
     *
     * @param currPos TODO
     * @param endPos  TODO
     */
    private void moveQueen(ArrayList<Integer> currPos, ArrayList<Integer> endPos) {
        // Check if new position is a valid state
        if (!_isValidPosition(boardMatrix, endPos)) {
            System.err.println("Cannot move to these coordinates");
        } else {
            // Determine inhabitant of tile
            byte piece = getOccupant(currPos);
            if (piece == BLACK_QUEEN || piece == WHITE_QUEEN) {
                updateQueen(currPos, endPos, piece);
            } else {
                logGameBoard();
            }
        }
    }

    /**
     * Removed the coordinates hashmap in favor of this function that checks the matrix directly.
     *
     * @param pos the position to be checked
     * @return the value of the piece as defined in CONSTANTS
     */
    public byte getOccupant(ArrayList<Integer> pos) {
        int x = pos.get(0);
        int y = pos.get(1);
        return boardMatrix[x][y];
    }

    /**
     * Update the matrix based on the arraylist positions received from the server
     *
     * @param currPos
     * @param endPos
     * @param piece
     */
    public void updateQueenInMatrix(ArrayList<Integer> currPos, ArrayList<Integer> endPos, byte piece) {
        int currX = currPos.get(0);
        int currY = currPos.get(1);
        int endX = endPos.get(0);
        int endY = endPos.get(1);

        boardMatrix[currX][currY] = BLANK;
        boardMatrix[endX][endY] = piece;
    }

    /**
     * Update the arraylists with the new positions from the server.
     * May actually be better to leave them as arraylists to keep from converting back and forth.
     *
     * @param currPos
     * @param endPos
     * @param piece
     */
    public void updateQueen(ArrayList<Integer> currPos, ArrayList<Integer> endPos, byte piece) {
        switch (piece) {
            case BLACK_QUEEN:
                for (Queen queen : blackQueens) {
                    if (queen.getPosition()[0] == currPos.get(0) && queen.getPosition()[1] == currPos.get(1)) {
                        queen.setPosition(endPos);
                    }
                }
                break;

            case WHITE_QUEEN:
                for (Queen queen : whiteQueens) {
                    if (queen.getPosition()[0] == currPos.get(0) && queen.getPosition()[1] == currPos.get(1)) {
                        queen.setPosition(endPos);
                    }
                }
                break;
        }
        updateQueenInMatrix(currPos, endPos, piece);
    }

    /**
     * Shoots an arrow at the position
     *
     * @param arrowPos Coordinates to shoot arrow at
     */
    private void shootArrow(ArrayList<Integer> arrowPos) {
        if (!_isValidPosition(boardMatrix, arrowPos)) {
            System.err.println("Selected tile is occupied");
        } else {
            int x = arrowPos.get(0);
            int y = arrowPos.get(1);
            boardMatrix[x][y] = ARROW;
            arrows.add(new Arrow((byte) x, (byte) y));
        }
    }

    /**
     * Initializes our boardMatrix using the integer matrix the server sends to us
     *
     * @param gameState - from server
     * @param showBoard - Whether or not to output the matrix representation to the console
     */
    public void setBoardState(ArrayList<Integer> gameState, boolean showBoard) {
        byte[][] rawMatrix = _makeMatrix(gameState);
        for (int i = 1; i < 11; i++) {
            for (int j = 1; j < 11; j++) {
                this.boardMatrix[i - 1][j - 1] = rawMatrix[i][j];
            }
        }
        whiteQueens = _queensFromBoard(this.boardMatrix, true);
        blackQueens = _queensFromBoard(this.boardMatrix, false);
        if (showBoard) logGameBoard();
    }
  
    public void logGameBoard() {
        System.out.println("\nCurrent Board Matrix from Server:\n------------------------------------------");
        for (int x = 0; x < ROWS; x++) {
            for (int y = 0; y < COLS; y++) {
                System.out.printf("(%d,%d) => %d, ", x, y, this.boardMatrix[x][y]);
            }
            System.out.println();
        }
        /* lists of pieces */
        for (Queen wQueen : whiteQueens) {
            System.out.println(wQueen);
        }
        for (Queen bQueen : blackQueens) {
            System.out.println(bQueen);
        }
        for (Arrow arrow : arrows) {
            System.out.println(arrow);
        }
    }

    public ArrayList<Queen> getBlackQueens() {
        return this.blackQueens;
    }

    public ArrayList<Queen> getWhiteQueens() {
        return this.whiteQueens;
    }

    public byte[][] getMatrix() {
        return this.boardMatrix;
    }
}
