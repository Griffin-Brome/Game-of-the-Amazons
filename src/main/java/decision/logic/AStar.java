package decision.logic;

import static utils.Constant.DIRECTIONS;
import static utils.Constant.N;
import static utils.GameLogic._generateNewPosition;
import static utils.GameLogic._isValidPosition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

import cosc322.amazons.GameBoard;
import models.Queen;


/**
 * Represents a modified A* algorithm (https://en.wikipedia.org/wiki/A*_search_algorithm) that checks to see if a path
 * exists from a given position to a given enemy queen. Unlike the traditional A* search, we are not interested in the
 * actual path that is found, only if one exists.
 */
public class AStar {
    private Queen enemy;
    private PriorityQueue<byte[]> queue;
    private GameBoard gameBoard;

    /**
     * @param enemy the enemy queen that we want to check for an existing path to
     */
    public AStar(Queen enemy, GameBoard gameBoard){
        this.enemy = enemy;
        this.gameBoard = gameBoard;
        queue = new PriorityQueue<>(1, new Comparator<byte[]>() {
            @Override
            /**
             * Return whichever one has a smaller Manhattan distance to the enemy queen
             */
            public int compare(byte[] o1, byte[] o2) {
                return ManhattanDistance(o2, enemy.getPosition()) - ManhattanDistance(o1, enemy.getPosition());
            }
        });
    }

    /**
     * Computes the Manhattan distance between two points
     *
     * @param pos1 first position
     * @param pos2 second position
     * @return manhattan distance as a scalar value
     */
    private int ManhattanDistance(byte[] pos1, byte[] pos2){
        return Math.abs(pos1[0] - pos2[0]) + Math.abs(pos1[1] - pos2[1]);
    }

    /**
     * Attempt to find a path to this class' enemy queen
     *
     * @return true if we can find the enemy queen, false otherwise
     */
    public boolean search(){
        // Place all 8 neighbors in the queue

        // Dequeue the first element

        // Add the new neighbors, according to the following:
        // - if the position is already in the queue, skip
        // - if there is one of our queens in the position, skip
        // - if there is an enemy, return true

        // Repeat
        return true;
    }

    /**
     * Check if a given position is in the queue
     *
     * @param pos1 the position to check for
     * @return if the position is in the queue
     */
    private boolean checkIfContains(byte[] pos1){
        for (byte[] pos2: queue )
           if (Arrays.equals(pos1, pos2)) return true;
       return false;
    }
    

	public ArrayList<byte[]> possibleMoves(byte[] pos) {
		ArrayList<byte[]> positions = new ArrayList<>();
		
		/**
		 * TODO: create a different _isValidPosition to return position even if it contains a piece
		 * this is required for us to check whether or not a queen or arrow was reached.
		 */
		for (byte dir : DIRECTIONS) {
			byte[] newPos;
			if(isValidPosition(gameBoard.getMatrix(), newPos = _generateNewPosition(pos, dir))) {
				if(!Arrays.equals(newPos, new byte[] {-1, -1}))
					positions.add(newPos);
			}
		}

		return positions;
	}
	
	public boolean isValidPosition(byte[][] board, byte[] newPos){
		return position[0] >= 0 && position[0] < N && position[1] >= 0 && position[1] < N;
	}

	public void addToQueue(PriorityQueue<byte[]> queue, Queen queen) {
		for(byte[] possibleMove : possibleMoves(queen.getPosition())) {
			queue.add(possibleMove);
		}
	}
}
