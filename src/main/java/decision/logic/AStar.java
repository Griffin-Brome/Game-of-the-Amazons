package decision.logic;

import models.Queen;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Represents a modified A* algorithm (https://en.wikipedia.org/wiki/A*_search_algorithm) that checks to see if a path
 * exists from a given position to a given enemy queen. Unlike the traditional A* search, we are not interested in the
 * actual path that is found, only if one exists.
 */
public class AStar {
    private Queen enemy;
    private PriorityQueue<byte[]> queue;

    /**
     * @param enemy the enemy queen that we want to check for an existing path to
     */
    public AStar(Queen enemy){
        this.enemy = enemy;
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
}
