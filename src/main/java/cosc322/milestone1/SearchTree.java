package cosc322.milestone1;

import java.util.ArrayList;

public class SearchTree {

	private final static byte BLANK = 0;
	private final static byte ARROW = 3;

	private SearchTreeNode root;
	private GameBoard gameBoard;
	private byte[][] matrix;

	public SearchTree(GameBoard gameBoard) {
		setBoard(gameBoard);
	}

	public SearchTreeNode expandNode(SearchTreeNode root) {
		ArrayList<Queen> queens;

		if (root.isWhitePlayer()) {
			queens = gameBoard.getWhiteQueens();
		} else {
			queens = gameBoard.getBlackQueens();
		}

		for (Queen queen : queens) {
			
			// get possible queen move
			for (byte[] newQueenPos : ActionFactory.getMoves(gameBoard, queen.getPosition())) {

				// move piece on board
				makeTempQueenMove(queen, newQueenPos);

				// Get possible arrows from that position
				for (byte[] arrowPos : ActionFactory.getMoves(gameBoard, newQueenPos)) {

					/**
					 * 
					 * Ideally this would be replaced with generating the game state for a temporary
					 * move, checking whatever heuristic (mobility, territory, territory-mobility,
					 * etc) then moving the piece back and only storing the heuristic value
					 * 
					 */				
					//byte[][] gameState = getNewGameState(queen, newQueenPos, arrowPos);
					//root.add(new SearchTreeNode(gameState, queen.getPosition(), newQueenPos, arrowPos, (byte) ((-1) * root.getPlayer()), !root.isWhitePlayer()));
					
					/**
					 * 
					 * kind of like this?
					 * 
					 */
					int tempHeuristic = getHeuristic(queen, newQueenPos, arrowPos); 
					root.add(new SearchTreeNode(tempHeuristic, queen.getPosition(), newQueenPos, arrowPos, (byte) ((-1) * root.getPlayer()), !root.isWhitePlayer()));
					
				}

				// undo move
				undoTempQueenMove(queen, newQueenPos);
			}
		}
		
		return root;

	}

	/**
	 * Generate the matrix for a given queen, new position and arrow position.
	 * 
	 * @param queen
	 * @param newQueenPos
	 * @param arrowPos
	 * @return
	 */
	public byte[][] getNewGameState(Queen queen, byte[] newQueenPos, byte[] arrowPos) {

		byte[] queenPos = queen.getPosition();

		// ideally we make the move - check its heuristic value - then undo the move on
		// the same board - not recreate and save matrix for each node ..
		byte[][] newState = gameBoard.getMatrixCopy();

		newState[queenPos[0]][queenPos[1]] = BLANK;
		newState[arrowPos[0]][arrowPos[1]] = ARROW;
		newState[newQueenPos[0]][newQueenPos[1]] = queen.getId();

		return newState;

	}
	
	/**
	 * Make a temporary move, check the heuristic for the game state
	 * and move pieces back
	 * 
	 * @param queen
	 * @param newQueenPos
	 * @param arrowPos
	 * @return
	 */
	public int getHeuristic(Queen queen, byte[] newQueenPos, byte[] arrowPos) {
		
		byte[] queenPos = queen.getPosition();
		
		makeTempMove(queen, newQueenPos, arrowPos);
		int heuristic = checkFakeHeuristic(queen, newQueenPos, arrowPos, matrix);
		undoTempMove(queen, newQueenPos, arrowPos);
		
		return heuristic;
	}

	public int checkFakeHeuristic(Queen queen, byte[] newQueenPos, byte[] arrowPos, byte[][] board) {
		return (int) (Math.random() * Integer.MAX_VALUE);
	}
	
	
	
	/**
	 * Move the queen to a position in the matrix, moving it out of the way for the
	 * arrow shot.
	 * 
	 * @param queen
	 * @param tempQueenPos
	 */
	public void makeTempQueenMove(Queen queen, byte[] tempQueenPos) {
		byte[] queenPos = queen.getPosition();
		matrix[queenPos[0]][queenPos[1]] = BLANK;
		matrix[tempQueenPos[0]][tempQueenPos[1]] = queen.getId();
	}

	/**
	 * Move the queen back after checking the arrow shot.
	 * 
	 * @param queen
	 * @param tempQueenPos
	 */
	public void undoTempQueenMove(Queen queen, byte[] tempQueenPos) {
		byte[] queenPos = queen.getPosition();
		matrix[queenPos[0]][queenPos[1]] = queen.getId();
		matrix[tempQueenPos[0]][tempQueenPos[1]] = BLANK;
	}
	
	/**
	 * Move the queen and arrow to a position in the matrix.
	 * 
	 * @param queen
	 * @param tempQueenPos
	 */
	public void makeTempMove(Queen queen, byte[] tempQueenPos, byte[] arrowPos) {
		byte[] queenPos = queen.getPosition();
		matrix[queenPos[0]][queenPos[1]] = BLANK;
		matrix[tempQueenPos[0]][tempQueenPos[1]] = queen.getId();
		matrix[arrowPos[0]][arrowPos[1]] = ARROW;
	}

	/**
	 * Move the queen back and remove arrow
	 * 
	 * @param queen
	 * @param tempQueenPos
	 */
	public void undoTempMove(Queen queen, byte[] tempQueenPos, byte[] arrowPos) {
		byte[] queenPos = queen.getPosition();
		matrix[queenPos[0]][queenPos[1]] = queen.getId();
		matrix[tempQueenPos[0]][tempQueenPos[1]] = BLANK;
		matrix[arrowPos[0]][arrowPos[1]] = BLANK;
	}


	
	/**
	 * Set the root of the search tree
	 * 
	 * @param board
	 * @param queen
	 * @param player
	 */
	public void setRoot(SearchTreeNode root) {
		this.root = root;
	}

	/**
	 * retrieve the root
	 * 
	 * @return
	 */
	public SearchTreeNode getRoot() {
		return this.root;
	}

	/**
	 * Set the gameboard and matrix references
	 * 
	 * @param gameBoard
	 */
	public void setBoard(GameBoard gameBoard) {
		this.gameBoard = gameBoard;
		this.matrix = gameBoard.getMatrix();
	}

	/**
	 * Print out all nodes in tree
	 * 
	 */
	public void printTree() {
		if (root.getChildren().size() > 0) {
			root.printTree();
		}
	}
}
