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
