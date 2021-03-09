package decision.logic;

import cosc322.amazons.GameBoard;

public class SearchTree {

	private SearchTreeNode root;
	private GameBoard gameBoard;
	private byte[][] matrix;

	public SearchTree(GameBoard gameBoard) {
		setBoard(gameBoard);
	}

	/**
	 *
	 * @param root
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
