package cosc322.amazons;

import java.util.ArrayList;
import java.util.Arrays;

import models.Arrow;
import models.Queen;

import static utils.Constant.*;
import static utils.GameLogic.*;

/**
 * gameState from server stored in matrix :
 * <p>
 * 9        0, 0, 0, 2, 0, 0, 2, 0, 0, 0,
 * 8	    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
 * 7	    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
 * 6        0, 0, 0, 0, 0, 0, 0, 0, 0, 2,
 * 5	    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
 * 4 	    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
 * 3	    1, 0, 0, 0, 0, 0, 0, 0, 0, 1,
 * 2	    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
 * 1	    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
 * 0        0, 0, 0, 1, 0, 0, 1, 0, 0, 0,
 * y
 * pos x    0  1  2  3  4  5  6  7  8  9
 * <p>
 * Where 1 represents white queen, 2 represents black queen and 3 are arrows.
 * <p>
 * Also positions sent from the server are (y, x).
 * They're stored as byte arrays in respective arraylists in (x, y) 0-indexed format.
 * All code uses (x, y) 0 indexed, we just have to make sure to send the sendMoveMessage in the
 * format the server prefers.
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
            int tileOccupant = getOccupant(currPos);
            if (tileOccupant == BLACK_QUEEN || tileOccupant == WHITE_QUEEN) {
                updateMatrix(currPos, endPos, BLACK_QUEEN);
                updatePieces(currPos, endPos, BLACK_QUEEN);
            } else {
                System.err.println("Selected tile does not contain a queen -> " + currPos + " - " + tileOccupant);
                System.err.println("White Queens");
                for (Queen wQueen : whiteQueens) {
                    System.err.println(wQueen);
                }
                System.err.println("Black Queens");
                for (Queen bQueen : blackQueens) {
                    System.err.println(bQueen);
                }
            }
        }
    }

    /**
     * Removed the coordinates hashmap in favor of this function that checks the matrix directly.
     *
     * @param pos
     * @return
     */
    public int getOccupant(ArrayList<Integer> pos) {
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
    public void updateMatrix(ArrayList<Integer> currPos, ArrayList<Integer> endPos, byte piece) {
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
    public void updatePieces(ArrayList<Integer> currPos, ArrayList<Integer> endPos, byte piece) {
        switch (piece) {
            //FIXME Rewrite this to just use .setPosition() instead of removing and re-adding the queen
            case BLACK_QUEEN:
                blackQueens.removeIf(queen -> queen.getPosition()[0] == currPos.get(0) && queen.getPosition()[1] == currPos.get(1));
                blackQueens.add(new Queen(endPos.get(0).byteValue(), endPos.get(1).byteValue(), BLACK_QUEEN));
                break;

            case WHITE_QUEEN:
                whiteQueens.removeIf(queen -> queen.getPosition()[0] == currPos.get(0) && queen.getPosition()[1] == currPos.get(1));
                whiteQueens.add(new Queen(endPos.get(0).byteValue(), endPos.get(1).byteValue(), WHITE_QUEEN));
                break;

            case ARROW:
                arrows.add(new Arrow(endPos.get(0).byteValue(), endPos.get(1).byteValue()));
                break;
        }

    }

    /**
     * Check if position is empty on board
     *
     * @param pos position to check
     * @return whether or not the tile is occupied
     */
    private boolean isValid(ArrayList<Integer> pos) {
        int x = pos.get(0);
        int y = pos.get(1);
        return boardMatrix[x][y] == BLANK;
    }


    /**
     * Shoots an arrow at the position
     *
     * @param arrowPos Coords to shoot arrow at
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
     * A lot of this will not be necessary but I am not sure what will end up being
     * best/easiest/most efficient to use during actual searches so :
     * <p>
     * - A simple 10x10 matrix representation of the gameState ArrayList ->
     * boardMatrix
     * <p>
     * - the actual ArrayList -> gameState
     *
     * @param gameState - from server
     * @param showBoard - Whether or not to output the matrix representation to the console
     */
    public void setBoardState(ArrayList<Integer> gameState, boolean showBoard) {
        //FIXME Yikes yeah we should refactor this at some point but for now it's just legacy code that works
        setGameState(gameState);

        // update coordinates of pieces
        int pos = 12;
        for (int y = ROWS; y > 0; y--) {
            for (int x = 1; x < COLS + 1; x++) {
                /**
                 *
                 * Constantly converting is super dumb - our code should only ever handle it as (x, y) and 0 indexed,
                 * Then when sending a move message revert to (y, x) and 1 indexed since that's what the server expects
                 *
                 * This method could be replaced with a more readable and logical one,
                 * since gamestate is only actually passed once and for all intents and purposes may as well be ignored as opposed
                 * to trying to convert it to (x, y) and 0 indexed format as done in this call.
                 *
                 */
                ArrayList<Integer> position = new ArrayList<>(Arrays.asList(x - 1, y - 1));
                int occupant = gameState.get(pos);

                // add to appropriate list of pieces
                byte[] bytePos = new byte[]{position.get(0).byteValue(), position.get(1).byteValue()};
                switch (occupant) {
                    case BLACK_QUEEN:
                        blackQueens.add(new Queen(bytePos, BLACK_QUEEN));
                        break;
                    case WHITE_QUEEN:
                        whiteQueens.add(new Queen(bytePos, WHITE_QUEEN));
                        break;
                    case ARROW:
                        arrows.add(new Arrow(bytePos));
                }
                pos++;
            }
            pos++;
        }

        // update matrix ..
        pos = 12;
        for (int y = ROWS - 1; y >= 0; y--) {
            for (int x = 0; x < COLS; x++) {
                this.boardMatrix[x][y] = gameState.get(pos++).byteValue();
            }
            pos++;
        }

        if (showBoard) {
            System.out.println("\nCurrent Board Matrix from Server:\n------------------------------------------");
            for (int y = 0; y < ROWS; y++) {
                for (int x = 0; x < COLS; x++) {
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
    }

    /**
     * @param gameState
     */
    private void setGameState(ArrayList<Integer> gameState) {
        this.gameState = gameState;
    }

    public ArrayList<Integer> getGameState() {
        return this.gameState;
    }

    public ArrayList<Queen> getBlackQueens() {
        return this.blackQueens;
    }

    public ArrayList<Queen> getWhiteQueens() {
        return this.whiteQueens;
    }

    public ArrayList<Arrow> getArrows() {
        return this.arrows;
    }

    public byte[][] getMatrix() {
        return this.boardMatrix;
    }
}
