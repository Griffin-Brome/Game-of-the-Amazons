package decision.logic;

import cosc322.amazons.ActionFactory;
import cosc322.amazons.GameBoard;
import models.Move;
import models.RootMove;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import static utils.GameLogic.*;

/**
 * Adapted from fig. 5.7, pg.310 of Artificial Intelligence, A Modern Approach (4th Edition)
 */
public class AlphaBeta {
    int alpha, beta;
    byte[][] currentBoard;
    int goalDepth;
    boolean isWhitePlayer;
    int level;
    byte territoryDepth;
    ArrayList<Move> possibleMoves;
    long startTime;
    long maxTime = 28000;

    public AlphaBeta(GameBoard gameBoard, int goalDepth, boolean isWhitePlayer, int level, byte territoryDepth, long startTime, RootMove root) {
        this.currentBoard = gameBoard.getMatrix();
        this.goalDepth = goalDepth;
        this.isWhitePlayer = isWhitePlayer;
        this.level = level;
        this.territoryDepth = territoryDepth;
        this.possibleMoves = root.getChildMoves();
        this.startTime = startTime;
        setAlpha(Integer.MIN_VALUE);
        setBeta(Integer.MAX_VALUE);
    }

    public Move getBestMove(int turnNumber) {
        if (turnNumber < 4) {
            return getEarlyMove();
        } else {
            return getBestMove();
        }
    }

    public Move getBestMove() {
        int score = Integer.MIN_VALUE;

        // just encapsulates the switch statement that sublist based on various tuning parameters
        // at UPPER 1 find the objective best move from all moves, only sublist if we're going deeper than that
        Move bestMove = possibleMoves.get(0);

        for (Move upperMove : possibleMoves) {
            if (System.currentTimeMillis() - this.startTime >= this.maxTime) {
                return null;
            }

            _doTempMove(currentBoard, upperMove);
            int tempScore = alphaBeta(upperMove, currentBoard, 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
            upperMove.setScore(tempScore);
            _undoTempMove(currentBoard, upperMove);

            if (tempScore >= score) {
                bestMove = upperMove;
                score = tempScore;
            }
        }
        System.out.println("Score: " + score + "\t" + bestMove + "\tAll Moves Size: " + possibleMoves.size());
        return bestMove;
    }

    public Move getEarlyMove() {
        int score = Integer.MIN_VALUE;
        Move bestMove = possibleMoves.get(0);
        for (Move upperMove : possibleMoves) {
            if (System.currentTimeMillis() - this.startTime >= this.maxTime) {
                return bestMove;
            }
            _doTempMove(currentBoard, upperMove);
            Heuristic heuristic = new Heuristic(currentBoard, isWhitePlayer, (byte) 5);
            int utility = heuristic.getUtility();
            _undoTempMove(currentBoard, upperMove);
            if (utility >= score) {
                bestMove = upperMove;
                score = utility;
            }
        }
        return bestMove;
    }

    public int alphaBeta(Move parentMove, byte[][] board, int depth, int alpha, int beta, boolean maximizingPlayer) {
        if (depth >= goalDepth) {
            Heuristic heuristic = new Heuristic(board, isWhitePlayer, this.territoryDepth);
            int utility = heuristic.getUtility();
            parentMove.setScore(utility);
            return utility;
        }

        if (System.currentTimeMillis() - this.startTime >= this.maxTime) {
            return maximizingPlayer ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        }

        int value;
        ArrayList<Move> allMoves;

        if (parentMove.hasChildren()) {
            parentMove.sortChildMoves(maximizingPlayer);
            allMoves = parentMove.getChildMoves();
        } else {
            ActionFactory af = new ActionFactory(board, maximizingPlayer == isWhitePlayer);
            allMoves = af.getPossibleMoves();
            parentMove.addAllChildMove(allMoves);
            parentMove.setHasChildren(true);
        }

        if (maximizingPlayer) {
            value = Integer.MIN_VALUE;

            for (Move move : allMoves) {
                _doTempMove(board, move);
                value = Math.max(value, alphaBeta(move, board, depth + 1, alpha, beta, false));
                _undoTempMove(board, move);
                alpha = Math.max(alpha, value);
                if (alpha >= beta) break;
            }
        } else {
            value = Integer.MAX_VALUE;

            for (Move move : allMoves) {
                _doTempMove(board, move);
                value = Math.min(value, alphaBeta(move, board, depth + 1, alpha, beta, true));
                _undoTempMove(board, move);
                beta = Math.min(beta, value);
                if (beta <= alpha) break;
            }
        }

        return value;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public void setBeta(int beta) {
        this.beta = beta;
    }
}
