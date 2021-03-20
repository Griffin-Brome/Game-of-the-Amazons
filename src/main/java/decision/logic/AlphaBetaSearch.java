package decision.logic;

import cosc322.amazons.ActionFactory;
import cosc322.amazons.GameBoard;
import models.Move;
import static utils.Constant.*;
import static utils.GameLogic._makeTempMove;

import java.util.ArrayList;

/**
 * Adapted from fig. 5.7, pg.310 of Artificial Intelligence, A Modern Approach (4th Edition)
 */
public class AlphaBetaSearch implements SearchStrategy {
    int alpha, beta;
    SearchTree tree;
    GameBoard gameBoard;
    int goalDepth;
    boolean isWhitePlayer;
    int goHard;
    byte territoryDepth;

    public AlphaBetaSearch(GameBoard gameBoard, int goalDepth, boolean isWhitePlayer, int goHard, byte territoryDepth) {
        this.gameBoard = gameBoard;
        this.goalDepth = goalDepth;
        this.isWhitePlayer = isWhitePlayer;
        this.goHard = goHard;
        this.territoryDepth = territoryDepth;
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
    public Move getBestMove() {
        ActionFactory af = new ActionFactory(gameBoard, isWhitePlayer);
        int score = 0;

        ArrayList<Move> allMoves = new ArrayList<>(af.getPossibleMoves());
        switch(goHard){
            case 1:
                allMoves = new ArrayList<>(allMoves.subList(0, 150));
                System.out.println("\uD83D\uDD25 Going Hard \uD83D\uDD25");
                break;
            case 2:
                allMoves = new ArrayList<>(allMoves.subList(0, 250));
                System.out.println("\uD83D\uDD25\uD83D\uDD25 Going HardER \uD83D\uDD25\uD83D\uDD25");
                break;
            //case 3: allMoves = new ArrayList<>(allMoves.subList(0, 450)); break;
            default:
                System.out.println("\uD83D\uDD25\uD83D\uDD25\uD83D\uDD25\uD83D\uDD25 FULL POWER \uD83D\uDD25\uD83D\uDD25\uD83D\uDD25\uD83D\uDD25");
        }


        Move bestMove = allMoves.get(0);

        for (Move move : allMoves) {
            byte[][] tempBoard = _makeTempMove(gameBoard.getMatrix(), move);
            int tempScore = alphabeta(tempBoard, 0, -Integer.MAX_VALUE, Integer.MAX_VALUE, true);
            if (tempScore >= score) {
                bestMove = move;
                score = tempScore;
            }
        }
        System.out.println("Score: " + score + "\t" + bestMove + "\tAll Moves Size: " + allMoves.size());
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

    /**
     * Major difference between this and the textbook's version, is that I don't return a tuple of [Move, value], and
     * instead just return the resultant node that you would get to via performing the move. Also this is a more
     * object-oriented approach over the textbook's functional one, where alpha and beta are not passed as parameters,
     * but instead exist as class attributes
     *
     * @param node
     * @return
     * @deprecated
     */
    private SearchTreeNode max(SearchTreeNode node) {
        if (node.isTerminal()) // || goalDepth == currentDepth
            return node;

        SearchTreeNode max = node.getFirstChild();
        SearchTreeNode temp;
        for (SearchTreeNode child : node.getChildren()) {
            temp = min(child);
            if (temp.getHeuristicValue() > max.getHeuristicValue())
                max = temp;
            if (temp.getHeuristicValue() > getAlpha())
                setAlpha(temp.getHeuristicValue());
            if (max.getHeuristicValue() >= getBeta())
                return max;
        }
        return max;
    }

    /**
     * See above method for implementation details
     *
     * @param node
     * @return
     * @deprecated
     */
    private SearchTreeNode min(SearchTreeNode node) {
        if (node.isTerminal())
            return node;
        SearchTreeNode min = node.getFirstChild();
        SearchTreeNode temp;
        for (SearchTreeNode child : node.getChildren()) {
            temp = max(child);
            if (temp.getHeuristicValue() < min.getHeuristicValue())
                min = temp;
            if (temp.getHeuristicValue() < getBeta())
                setBeta(temp.getHeuristicValue());
            if (min.getHeuristicValue() <= getAlpha())
                return min;
        }
        return min;
    }
}
