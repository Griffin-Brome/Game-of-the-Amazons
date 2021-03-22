package decision.logic;

import cosc322.amazons.ActionFactory;
import cosc322.amazons.GameBoard;
import models.Move;
import static utils.Constant.*;
import static utils.GameLogic._makeTempMove;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Adapted from fig. 5.7, pg.310 of Artificial Intelligence, A Modern Approach (4th Edition)
 */
public class AlphaBetaSearch implements Callable<Move> {
    int alpha, beta;
    GameBoard gameBoard;
    int goalDepth;
    boolean isWhitePlayer;
    int goHard;
    byte territoryDepth;
    ArrayList<Move> possibleMoves;

    public AlphaBetaSearch(GameBoard gameBoard, int goalDepth, boolean isWhitePlayer, int goHard, byte territoryDepth, ArrayList<Move> possibleMoves) {
        this.gameBoard = gameBoard;
        this.goalDepth = goalDepth;
        this.isWhitePlayer = isWhitePlayer;
        this.goHard = goHard;
        this.territoryDepth = territoryDepth;
        this.possibleMoves = possibleMoves;
        setAlpha(-Integer.MAX_VALUE);
        setBeta(Integer.MAX_VALUE);
    }

    public int getAlpha() {
        return alpha;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public int getBeta() {
        return beta;
    }

    public void setBeta(int beta) {
        this.beta = beta;
    }


    /**
     * Returns the best move
     * @return
     */
    @Override
    public Move call(){
        return getBestMove();
    }

    public Move getBestMove() {
        int score = Integer.MIN_VALUE;

        switch(goHard){
            case 1:
                possibleMoves = new ArrayList<>(possibleMoves.subList(0, Math.min(possibleMoves.size(),150)));
                break;
            case 2:
                possibleMoves = new ArrayList<>(possibleMoves.subList(0, Math.min(possibleMoves.size(),250)));
                System.out.println("🔥🔥🔥🔥 Going Hard 🔥🔥🔥🔥");
                break;
            case 3:
                possibleMoves = new ArrayList<>(possibleMoves.subList(0, Math.min(possibleMoves.size(),450)));
                System.out.println("🔥🔥🔥🔥🔥🔥Going HardER 🔥🔥🔥🔥🔥🔥🔥");
                break;
            case 4:
                possibleMoves = new ArrayList<>(possibleMoves.subList(0, Math.min(possibleMoves.size(),750)));
                System.out.println("🔥🔥🔥🔥🔥🔥🔥🔥 Going HardERRR 🔥🔥🔥🔥🔥🔥🔥🔥");
                break;
            default:
                System.out.println("🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥 FULL POWER 🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥");
        }

        Move bestMove = possibleMoves.get(0);

        for (Move move : possibleMoves) {
            byte[][] tempBoard = _makeTempMove(gameBoard.getMatrix(), move);
            int tempScore = alphabeta(tempBoard, 0, -Integer.MAX_VALUE, Integer.MAX_VALUE, true);
            if (tempScore >= score) {
                bestMove = move;
                bestMove.setScore(tempScore);
                score = tempScore;
            }
        }
        System.out.println("Score: " + score + "\t" + bestMove + "\tAll Moves Size: " + possibleMoves.size());
        return bestMove;
    }

    public int alphabeta(byte[][] board, int depth, int alpha, int beta, boolean maximizingPlayer) {
        if (depth == goalDepth) { // isTerminal(board, maximizingPlayer, isWhitePlayer))
            Heuristic heuristic = new Heuristic(board, isWhitePlayer, this.territoryDepth);
            return heuristic.getUtility();
        }

        int value;
        ActionFactory af;

        if (maximizingPlayer) {
            value = -Integer.MAX_VALUE;
            af = new ActionFactory(board, isWhitePlayer);

            for (Move move : af.getPossibleMoves()) {
                byte[][] tempBoard = _makeTempMove(board, move);
                value = Math.max(value, alphabeta(tempBoard, depth + 1, alpha, beta, false));
                alpha = Math.max(alpha, value);
                if (alpha >= beta) {
//                    System.out.println("Prune: " + move.toString());
                    break;
                }
            }
        } else {
            value = Integer.MAX_VALUE;
            af = new ActionFactory(board, !isWhitePlayer);

            for (Move move : af.getPossibleMoves()) {
                byte[][] tempBoard = _makeTempMove(board, move);
                value = Math.min(value, alphabeta(tempBoard, depth + 1, alpha, beta, true));
                beta = Math.min(beta, value);
                if (beta <= alpha) {
//                    System.out.println("Prune: " + move.toString());
                    break;
                }
            }
        }
        return value;
    }
}
