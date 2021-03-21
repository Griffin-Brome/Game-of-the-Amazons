package decision.logic;

import cosc322.amazons.ActionFactory;
import cosc322.amazons.GameBoard;
import models.Move;
import static utils.Constant.*;
import static utils.GameLogic._makeTempMove;

import java.util.ArrayList;
import java.util.concurrent.*;


/**
 * Adapted from fig. 5.7, pg.310 of Artificial Intelligence, A Modern Approach (4th Edition)
 */
public class AlphaBetaSearch extends RecursiveTask<Move> {
    int alpha, beta;
    SearchTree tree;
    GameBoard gameBoard;
    int goalDepth;
    boolean isWhitePlayer;
    int goHard;
    byte territoryDepth;
    ArrayList<Move> moveList;

    public AlphaBetaSearch(GameBoard gameBoard, int goalDepth, boolean isWhitePlayer, int goHard, byte territoryDepth, ArrayList<Move> moveList) {
        this.gameBoard = gameBoard;
        this.goalDepth = goalDepth;
        this.isWhitePlayer = isWhitePlayer;
        this.goHard = goHard;
        this.territoryDepth = territoryDepth;
        this.moveList = moveList;
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
    public Move compute() {
        ArrayList<Move> allMoves = moveList;
        int movesSize = allMoves.size();
        int numThreads = 8;
        int threshold = numThreads * 8;
        int score = 0;
        Move bestMove = allMoves.get(0);

        if(movesSize > threshold) {

        /*switch(goHard){
            case 1:
                allMoves = new ArrayList<>(allMoves.subList(0, 120));
                break;
            case 2:
                allMoves = new ArrayList<>(allMoves.subList(0, 200));
                System.out.println("🔥 Going Hard 🔥");
                break;
            case 3:
                allMoves = new ArrayList<>(allMoves.subList(0, 450));
                System.out.println("🔥🔥 Going HardER 🔥🔥");
                break;
            case 4:
                allMoves = new ArrayList<>(allMoves.subList(0, 700));
                System.out.println("🔥🔥 Going HardER 🔥🔥");
                break;
            default: System.out.println("🔥🔥🔥🔥 FULL POWER 🔥🔥🔥🔥");
        }*/



/*            System.out.println("Before the split");
            System.out.println(allMoves.size() / 2);
            System.out.println(allMoves.size() - 1);*/

            ArrayList<Move> testMoves1 = new ArrayList<>(allMoves.subList(0, movesSize/numThreads));
            ArrayList<Move> testMoves2 = new ArrayList<>(allMoves.subList(movesSize/numThreads, movesSize/numThreads*2));
            ArrayList<Move> testMoves3 = new ArrayList<>(allMoves.subList(movesSize / numThreads*2, movesSize/numThreads*3));
            ArrayList<Move> testMoves4 = new ArrayList<>(allMoves.subList(movesSize / numThreads*3, movesSize/numThreads*4));
            ArrayList<Move> testMoves5 = new ArrayList<>(allMoves.subList(movesSize / numThreads*4, movesSize/numThreads*5));
            ArrayList<Move> testMoves6 = new ArrayList<>(allMoves.subList(movesSize / numThreads*5, movesSize/numThreads*6));
            ArrayList<Move> testMoves7 = new ArrayList<>(allMoves.subList(movesSize / numThreads*6, movesSize/numThreads*7));
            ArrayList<Move> testMoves8 = new ArrayList<>(allMoves.subList(movesSize / numThreads*7, movesSize/numThreads*8));

            AlphaBetaSearch test1 = new AlphaBetaSearch(gameBoard, goalDepth, isWhitePlayer, this.goHard, territoryDepth, testMoves1);
            AlphaBetaSearch test2 = new AlphaBetaSearch(gameBoard, goalDepth, isWhitePlayer, this.goHard, territoryDepth, testMoves2);
            AlphaBetaSearch test3 = new AlphaBetaSearch(gameBoard, goalDepth, isWhitePlayer, this.goHard, territoryDepth, testMoves3);
            AlphaBetaSearch test4 = new AlphaBetaSearch(gameBoard, goalDepth, isWhitePlayer, this.goHard, territoryDepth, testMoves4);
            AlphaBetaSearch test5 = new AlphaBetaSearch(gameBoard, goalDepth, isWhitePlayer, this.goHard, territoryDepth, testMoves5);
            AlphaBetaSearch test6 = new AlphaBetaSearch(gameBoard, goalDepth, isWhitePlayer, this.goHard, territoryDepth, testMoves6);
            AlphaBetaSearch test7 = new AlphaBetaSearch(gameBoard, goalDepth, isWhitePlayer, this.goHard, territoryDepth, testMoves7);
            AlphaBetaSearch test8 = new AlphaBetaSearch(gameBoard, goalDepth, isWhitePlayer, this.goHard, territoryDepth, testMoves8);

            invokeAll(test1, test2, test3, test4, test5, test6, test7, test8);
            System.out.println("After the split");
        }
        else{
            for (Move move : allMoves) {
                byte[][] tempBoard = _makeTempMove(gameBoard.getMatrix(), move);
                int tempScore = alphabeta(tempBoard, 0, -Integer.MAX_VALUE, Integer.MAX_VALUE, true);
                if (tempScore >= score) {
                    bestMove = move;
                    score = tempScore;
                }
            }
            System.out.println("Score: " + score + "\t" + bestMove + "\tAll Moves Size: " + allMoves.size());
        }
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
