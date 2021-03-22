package decision.logic;

import static utils.Constant.*;
import static utils.GameLogic.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

import cosc322.amazons.GameBoard;
import models.Queen;

/**
 * Represents a modified A* algorithm
 * (https://en.wikipedia.org/wiki/A*_search_algorithm) that checks to see if a
 * path exists from a given position to a given enemy queen. Unlike the
 * traditional A* search, we are not interested in the actual path that is
 * found, only if one exists.
 */
public class AStar {
	private Queen enemy;
	private PriorityQueue<byte[]> queue;
	byte[][] boardMatrix;

	/**
	 * @param gameBoard the gameboard to check
	 */
	public AStar(GameBoard gameBoard) {
		this.boardMatrix = gameBoard.getMatrix();
		/**
		 * Return whichever one has a smaller Manhattan distance to the enemy queen
		 */
		queue = new PriorityQueue<>(1, Comparator.comparingInt(o -> ManhattanDistance(o, enemy.getPosition())));
	}

	public AStar(byte[][] board) {
		this.boardMatrix = board;
		/**
		 * Return whichever one has a smaller Manhattan distance to the enemy queen
		 */
		queue = new PriorityQueue<>(1, Comparator.comparingInt(o -> ManhattanDistance(o, enemy.getPosition())));
	}

	/**
	 * Computes the Manhattan distance between two points
	 *
	 * @param pos1 first position
	 * @param pos2 second position
	 * @return Manhattan distance as a scalar value
	 */
	private int ManhattanDistance(byte[] pos1, byte[] pos2) {
		return Math.abs(pos1[0] - pos2[0]) + Math.abs(pos1[1] - pos2[1]);
	}

	/**
	 * Attempt to find a path from queen to enemyQueen.
	 * 
	 * Should we consider all queens? There's some edges cases where we are not in
	 * chamber but this would not reach any enemy queens, like if one of our queens
	 * is in the way?
	 *
	 * @return true if we can find the enemy queen, false otherwise
	 */
	public boolean search(Queen myQueen, Queen enemyQueen) {
		// track visited spots and empty queue
		boolean[][] visited = new boolean[ROWS][COLS];
		queue.clear(); 
		
		//set enemy to be used in Comparator
		setEnemy(enemyQueen);
		
		// add start position to queue
		byte[] queenPos = myQueen.getPosition();
		visited[queenPos[0]][queenPos[1]] = true;
		addAllNeighboursToQueue(queenPos);
		
		while (!queue.isEmpty()) {
			
			// Dequeue the first element
			byte[] pos = queue.poll();
			visited[pos[0]][pos[1]] = true;
//			System.out.println("Checking position "+ Arrays.toString(pos));

			ArrayList<byte[]> neighbours = possibleMoves(pos);
			for(byte[] neighbour : neighbours) {
				
				// Add the new neighbors, according to the following:
				// if already visited, skip
				if(visited[neighbour[0]][neighbour[1]]) 
					continue;
				
				// - if there is an enemy, return true
				if (_getOccupant(boardMatrix, neighbour) == enemyQueen.getId()) {
//					System.out.println("Found at " + Arrays.toString(neighbour));
					return true;
				}
				
				// - if the position is already in the queue, skip
				if (checkIfContains(neighbour)) 
					continue;
				
				// if not yet visited add neighbor to the queue
				queue.offer(neighbour);
				
			}
		}

		// if enemyQueen was not found return false
		return false;
	}

	/**
	 * Check if a given position is in the queue
	 *
	 * @param pos1 the position to check for
	 * @return if the position is in the queue
	 */
	private boolean checkIfContains(byte[] pos1) {
		for (byte[] pos2 : queue)
			if (Arrays.equals(pos1, pos2))
				return true;
		return false;
	}

	/**
	 * Checks the 8 surrounding tiles and adds them to a list if they're
	 * valid tiles.
	 * 
	 * @param pos: check 8 surrounding positions and add to list
	 * @return positions: list of those positions
	 */
	public ArrayList<byte[]> possibleMoves(byte[] pos) {
		ArrayList<byte[]> positions = new ArrayList<>();

		for (byte dir : DIRECTIONS) {
			byte[] newPos = _generateNewPosition(pos.clone(), dir);
			if (isValidPosition(newPos)) {
				positions.add(newPos);
			}
		}
		return positions;
	}

	/**
	 * Checks if a position is within the bounds of the board.
	 * 
	 * @param position: on board (x, y)
	 * @return true:	if position is within bounds of board	
	 */
	public boolean isValidPosition(byte[] position) {
		return position[0] >= 0 && position[0] < N && position[1] >= 0 && position[1] < N && _getOccupant(this.boardMatrix, position) != ARROW;
	}

	/**
	 * Adds all neighbours of a position to the queen, 
	 * currently used to initialize the queue, probably not necessary?
	 * Could probably just add the start position?
	 * 
	 * @param queenPos
	 */
	public void addAllNeighboursToQueue(byte[] queenPos) {
		for (byte[] possibleMove : possibleMoves(queenPos)) {
			queue.offer(possibleMove);
		}
	}
	
	/**
	 * Sets the current enemy queen to search for.
	 * 
	 * @param enemyQueen: queen currently being searched for
	 */
	public void setEnemy(Queen enemyQueen) {
		this.enemy = enemyQueen;
	}
}
