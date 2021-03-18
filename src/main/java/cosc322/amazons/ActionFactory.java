package cosc322.amazons;

import models.Move;
import models.Queen;

import static utils.Constant.*;
import static utils.GameLogic.*;
import static utils.MatrixOperations.*;

import java.util.ArrayList;
import java.util.Arrays;

public class ActionFactory {
    private ArrayList<Queen> ourQueens;
    byte[][] boardMatrix;
    private boolean isWhitePlayer;

    public ActionFactory(GameBoard gameBoard, boolean isWhitePlayer) {
        boardMatrix = gameBoard.getMatrix();
        ourQueens = isWhitePlayer ? gameBoard.getWhiteQueens() : gameBoard.getBlackQueens();

        this.isWhitePlayer = isWhitePlayer;
    }

    public ActionFactory(byte[][] board, ArrayList<Queen> queens, boolean isWhitePlayer) {
        boardMatrix = board;
        ourQueens = queens;

        this.isWhitePlayer = isWhitePlayer;
    }


    public ArrayList<Move> getPossibleMoves() {
        ArrayList<Move> moves = new ArrayList<>();

        for (Queen queen : ourQueens) {
            moves.addAll(getPossibleMoves(queen.getPosition()));
        }

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
            byte[] newPos = _generateNewPosition(oldPos, dir);
            while (_isValidPosition(boardMatrix, newPos)){
                ArrayList<byte[]> possibleArrows = generateArrowsHelper(oldPos,newPos);
                for(byte[] arrowPos : possibleArrows){
                    Move move = new Move(oldPos);
                    move.setNewPos(newPos);
                    move.setArrowPos(arrowPos);
                    moves.add(move);
                }
                newPos = _generateNewPosition(newPos, dir);
            }
        }
        return moves;
    }


    public ArrayList<byte[]> generateArrowsHelper(byte[] oldQueenPos, byte[] newQueenPos) {
        ArrayList<byte[]> possibleArrows = new ArrayList<>();
        byte[][] tempBoard = makeTempQueenMove(boardMatrix, oldQueenPos, newQueenPos);

        for (byte dir : DIRECTIONS) {
            byte[] newPos = _generateNewPosition(newQueenPos, dir);
          while(_isValidPosition(tempBoard, newPos)){
              possibleArrows.add(newPos);
              newPos = _generateNewPosition(newPos, dir);
          }
        }
        return possibleArrows;
    }


    public void generateArrows(byte dir, byte[][] tempBoard, byte[] currPos, ArrayList<byte[]> possibleArrows) {
        possibleArrows.add(new byte[] {currPos[0], currPos[1]});

        byte[] newPos = _generateNewPosition(currPos.clone(), dir);
        // if the new position is valid, explore it
        if (_isValidPosition(tempBoard, newPos))
            generateArrows(dir, tempBoard, newPos, possibleArrows);
    }


    public byte[][] makeTempQueenMove(byte[][] oldBoard, byte[] oldPos, byte[] newPos){
        byte[][] tempBoard = _cloneMatrix(oldBoard);
        tempBoard[oldPos[0]][oldPos[1]] = BLANK;
        tempBoard[newPos[0]][newPos[1]] = isWhitePlayer ? WHITE_QUEEN : BLACK_QUEEN;

        return tempBoard;
    }
}