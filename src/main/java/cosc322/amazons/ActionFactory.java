package cosc322.amazons;

import decision.logic.Heuristic;
import decision.logic.RoyalChamber;
import models.Move;
import models.Queen;

import static utils.Constant.*;
import static utils.GameLogic.*;
import static utils.MatrixOperations.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class ActionFactory {
    private ArrayList<Queen> ourQueens;
    byte[][] boardMatrix;
    private boolean isWhitePlayer;

    public ActionFactory(GameBoard gameBoard, boolean isWhitePlayer) {
        boardMatrix = gameBoard.getMatrix();
        ourQueens = isWhitePlayer ? gameBoard.getWhiteQueens() : gameBoard.getBlackQueens();
        this.isWhitePlayer = isWhitePlayer;
    }

    public ActionFactory(byte[][] board, boolean isWhitePlayer) {
        boardMatrix = board;
        ourQueens = _queensFromBoard(board, isWhitePlayer);
        this.isWhitePlayer = isWhitePlayer;
    }


    public ArrayList<Move> getPossibleMoves() {
        ArrayList<Move> moves = new ArrayList<>();
        RoyalChamber rc = new RoyalChamber(isWhitePlayer, boardMatrix);
        //TODO: the comments in this method toggle the chamber checking on and off
//        rc.checkRoyalChambers();
//        ourQueens = rc.getFreeQueens();

        for (Queen queen : ourQueens) {
            moves.addAll(getPossibleMoves(queen.getPosition()));
        }

//        if(ourQueens.isEmpty()) {
//            for (Queen queen : rc.getChamberedQueens()) {
//                moves.addAll(getPossibleMoves(queen.getPosition()));
//            }
//        }

        // orders the moves from "best" to "worst" based on mobility heuristic
        Collections.sort(moves);

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
        ArrayList<Move> moves = new ArrayList<>();
        for (byte dir : DIRECTIONS) {
            byte[] newPos = _generateNewPosition(oldPos.clone(), dir);
            while (_isValidPosition(boardMatrix, newPos)){
                ArrayList<byte[]> possibleArrows = generateArrowsHelper(oldPos.clone(), newPos.clone());
                for(byte[] arrowPos : possibleArrows){
                    Move move = new Move(oldPos);
                    move.setNewPos(newPos);
                    move.setArrowPos(arrowPos);

                    // this code calculates the mobility heuristic of this move's future board state and stores it in the move so we can order it
                    byte[][] tempBoard = _makeTempMove(boardMatrix, move);
                    Heuristic h = new Heuristic(tempBoard, isWhitePlayer);
                    move.setOrderingValue(h.mobilityHeuristic());
                    // okay, back to the generating moves ✨

                    moves.add(move);
                }
                newPos = _generateNewPosition(newPos.clone(), dir);
            }
        }
        return moves;
    }

    public ArrayList<byte[]> generateArrowsHelper(byte[] oldQueenPos, byte[] newQueenPos) {
        ArrayList<byte[]> possibleArrows = new ArrayList<>();
        byte[][] tempBoard = _makeTempQueenMove(boardMatrix, oldQueenPos, newQueenPos, isWhitePlayer);

        for (byte dir : DIRECTIONS) {
            byte[] newPos = _generateNewPosition(newQueenPos, dir);
          while(_isValidPosition(tempBoard, newPos)){
              possibleArrows.add(newPos);
              newPos = _generateNewPosition(newPos, dir);
          }
        }
        return possibleArrows;
    }
}