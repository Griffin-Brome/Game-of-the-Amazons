package cosc322.amazons;

import models.Move;
import models.Queen;

import static utils.Constant.*;
import static utils.GameLogic.*;
import static utils.MatrixOperations.*;

import java.util.ArrayList;

public class ActionFactoryRecursive {
    private ArrayList<Queen> ourQueens;
    byte[][] boardMatrix;
    ArrayList<Move> moves;
    private boolean isWhitePlayer;

    public ActionFactoryRecursive(GameBoard gameBoard, boolean isWhitePlayer) {
        boardMatrix = gameBoard.getMatrix();
        ourQueens = isWhitePlayer ? gameBoard.getWhiteQueens() : gameBoard.getBlackQueens();
        moves = new ArrayList<>();

        this.isWhitePlayer = isWhitePlayer;
    }

    public ActionFactoryRecursive(byte[][] board, boolean isWhitePlayer) {
        boardMatrix = board;
        ourQueens = _queensFromBoard(board, isWhitePlayer);
        moves = new ArrayList<>();

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
        ArrayList<byte[]> possibleMoves = generateMovesHelper(oldPos);

        for (byte[] possibleMove : possibleMoves) {
            addMove(oldPos, possibleMove);
        }

        return moves;
    }


    private void addMove(byte[] oldPos, byte[] newMove) {
        Move move = new Move(oldPos);
        move.setNewPos(new byte[] { newMove[0], newMove[1] });
        move.setArrowPos(new byte[] { newMove[2], newMove[3] });

        moves.add(move);
    }


    public ArrayList<byte[]> generateMovesHelper(byte[] oldPos) {
        ArrayList<byte[]> possibleMoves = new ArrayList<>();

        for (byte dir : DIRECTIONS) {
            byte[] newPos = _generateNewPosition(oldPos.clone(), dir);
            if (_isValidPosition(boardMatrix, newPos))
                generateMoves(dir, oldPos, newPos, possibleMoves);
        }
        return possibleMoves;
    }


    public void generateMoves(byte dir, byte[] originalPos, byte[] currPos, ArrayList<byte[]> possibleMoves) {
        ArrayList<byte[]> possibleArrowPositions = generateArrowsHelper(originalPos.clone(), currPos.clone());
        for(byte[] arrowPos: possibleArrowPositions) {
            // add the current move to the set of all possible moves
            possibleMoves.add(new byte[] {currPos[0], currPos[1], arrowPos[0], arrowPos[1]});
        }

        byte[] newPos = _generateNewPosition(currPos.clone(), dir);

        // if the new position is valid, explore it
        if (_isValidPosition(boardMatrix, newPos))
            generateMoves(dir, originalPos, newPos, possibleMoves);
    }


    public ArrayList<byte[]> generateArrowsHelper(byte[] oldQueenPos, byte[] newQueenPos) {
        ArrayList<byte[]> possibleArrows = new ArrayList<>();
        byte[][] tempBoard = makeTempQueenMove(boardMatrix, oldQueenPos, newQueenPos);

        for (byte dir : DIRECTIONS) {
            byte[] newPos = _generateNewPosition(newQueenPos.clone(), dir);
            if (_isValidPosition(tempBoard, newPos))
                generateArrows(dir, tempBoard, newPos, possibleArrows);
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