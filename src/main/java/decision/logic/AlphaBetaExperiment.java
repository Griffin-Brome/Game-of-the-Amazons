package decision.logic;

import cosc322.amazons.ActionFactory;
import cosc322.amazons.GameBoard;
import models.Move;
import models.RootMove;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import static utils.GameLogic.*;
import static utils.MatrixOperations._printMatrix;

/**
 * Adapted from fig. 5.7, pg.310 of Artificial Intelligence, A Modern Approach (4th Edition)
 */
public class AlphaBetaExperiment implements Callable<Move> {
    int alpha, beta;
    GameBoard gameBoard;
    int goalDepth;
    boolean isWhitePlayer;
    int goHard;
    byte territoryDepth;
    ArrayList<Move> possibleMoves;
    long startTime;
    long maxTime = 28000;

    public AlphaBetaExperiment(GameBoard gameBoard, int goalDepth, boolean isWhitePlayer, int goHard, byte territoryDepth, long startTime, RootMove root) {
        this.gameBoard = gameBoard;
        this.goalDepth = goalDepth;
        this.isWhitePlayer = isWhitePlayer;
        this.goHard = goHard;
        this.territoryDepth = territoryDepth;
        this.possibleMoves = root.getChildMoves();
        this.startTime = startTime;
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

    public Move getBestMove(int turnNumber) {
        if(turnNumber < 5) {
//            return possibleMoves.get(0);
            return getFirstMove();
//            return getBestMove();
        } else {
            return getBestMove();
        }
    }

    public Move getBestMove() {
        int score = Integer.MIN_VALUE;

        prunePossibleMoves(); // just encapsulates the switch statement that sublists based on various tuning parameters
        Move bestMove = possibleMoves.get(0);
        byte[][] currentBoard = gameBoard.getMatrix();

        int halfMoves = (int) Math.floor(possibleMoves.size() / 1.5); // we must be 2/3 of the way through a layer before trusting it enough to return it's value
        int count = 0;
        for (Move upperMove : possibleMoves) {
            int tempScore;
            if(System.currentTimeMillis() - this.startTime >= this.maxTime) {
                return count >= halfMoves ? bestMove : null;
            }
            _doTempMove(currentBoard, upperMove);
            tempScore = alphabeta(upperMove, currentBoard, 1, -Integer.MAX_VALUE, Integer.MAX_VALUE, false);
            upperMove.setScore(tempScore);

            if (tempScore >= score) {
                bestMove = upperMove;
                score = tempScore;
            }
            _undoTempMove(currentBoard, upperMove);
            ++count;
        }
        System.out.println("Score: " + score + "\t" + bestMove + "\tAll Moves Size: " + possibleMoves.size());
        return bestMove;
    }

    public void prunePossibleMoves() {
        int movesMax = 120;
        switch(goHard){
            case 1:
                movesMax = 120;
                break;
            case 2:
               movesMax = 250;
                System.out.println("🔥🔥🔥🔥 Going Hard 🔥🔥🔥🔥");
                break;
            case 3:
                movesMax = 500;
                System.out.println("🔥🔥🔥🔥 Going Hard 🔥🔥🔥🔥");
                break;
            case 4:
                movesMax = 800;
                System.out.println("🔥🔥🔥🔥 Going Hard 🔥🔥🔥🔥");
                break;
            default:
                System.out.println("🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥 FULL POWER 🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥");
                return; // don't sublist at all when at full power
        }

        possibleMoves = new ArrayList<>(possibleMoves.subList(0, Math.min(possibleMoves.size(), movesMax)));
    }

    public Move getFirstMove() {
        int score = Integer.MIN_VALUE;
        Move bestMove = possibleMoves.get(0);
        byte[][] currentBoard = gameBoard.getMatrix();
        for (Move upperMove : possibleMoves) {
            if(System.currentTimeMillis() - this.startTime >= this.maxTime) {
                return bestMove;
            }
            _doTempMove(currentBoard, upperMove);
            Heuristic heuristic = new Heuristic(currentBoard, isWhitePlayer, (byte) 4);
            int utility = heuristic.mobilityHeuristic() + heuristic.territoryHeuristic() + 5 * heuristic.immediateMovesHeuristic();
            _undoTempMove(currentBoard, upperMove);
            if(utility >= score) {
                bestMove = upperMove;
                score = utility;
            }
        }

        return bestMove;
    }

    public int alphabeta(Move parentMove, byte[][] board, int depth, int alpha, int beta, boolean maximizingPlayer) {
        if (depth == goalDepth) {
            Heuristic heuristic = new Heuristic(board, isWhitePlayer, this.territoryDepth);
            int utility = heuristic.getUtility();
            parentMove.setScore(utility);
            return utility;
        }

        if(System.currentTimeMillis() - this.startTime >= this.maxTime) {
            if(!maximizingPlayer) {
                return Integer.MIN_VALUE;
            } else {
                return Integer.MAX_VALUE;
            }
        }

        int value;
        ActionFactory af;
        ArrayList<Move> allMoves;

        if (maximizingPlayer) {
            value = -Integer.MAX_VALUE;

            if(parentMove.hasChildren()) {
                parentMove.sortChildMoves();
                allMoves = parentMove.getChildMoves();
                for (Move move : allMoves) {
                    value = Math.max(value, move.getScore());
                    alpha = Math.max(alpha, value);
                    if (alpha >= beta) break;
                }
            } else {
                af = new ActionFactory(board, isWhitePlayer);
                allMoves = af.getPossibleMoves();
                for (Move move : allMoves) {
                    _doTempMove(board, move);
                    value = Math.max(value, alphabeta(move, board, depth + 1, alpha, beta, false));
                    _undoTempMove(board, move);
                    alpha = Math.max(alpha, value);
                    if (alpha >= beta) break;
                }
                parentMove.addAllChildMove(allMoves);
                parentMove.setHasChildren(true);
            }
        } else {
            value = Integer.MAX_VALUE;

            if(parentMove.hasChildren()) {
                parentMove.sortChildMoves();
                allMoves = parentMove.getChildMoves();
                for (Move move : allMoves) {
                    value = Math.min(value, move.getScore());
                    beta = Math.min(beta, value);
                    if (beta <= alpha) break;
                }
            } else {
                af = new ActionFactory(board, !isWhitePlayer);
                allMoves = af.getPossibleMoves();
                for (Move move : allMoves) {
                    _doTempMove(board, move);
                    value = Math.min(value, alphabeta(move, board, depth + 1, alpha, beta, true));
                    _undoTempMove(board, move);
                    beta = Math.min(beta, value);
                    if (beta <= alpha) break;
                }
                parentMove.addAllChildMove(allMoves);
                parentMove.setHasChildren(true);
            }
        }
        return value;
    }
}
