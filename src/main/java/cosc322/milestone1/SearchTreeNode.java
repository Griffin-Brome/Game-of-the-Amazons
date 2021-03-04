package cosc322.milestone1;

import java.util.ArrayList;
import java.util.Arrays;

public class SearchTreeNode {
	private int heuristicValue;
	private byte player;
	private byte[][] board;
	private byte[] currQueenPos;
	private byte[] nextQueenPos;
	private byte[] arrowPos;
	private boolean isWhitePlayer;
	private SearchTreeNode nextNode;

	private SearchTreeNode parent;
	private ArrayList<SearchTreeNode> children;

	/**
	 * Called when creating the very first node in the tree
	 * 
	 * @param board
	 */
	public SearchTreeNode(byte[][] board, byte player, boolean isWhitePlayer) {
		setBoard(board);
		this.children = new ArrayList<>();
		this.player = player;
		this.isWhitePlayer = isWhitePlayer;
	}

	/**
	 * Called when adding a child to a root node Includes the moves required to
	 * reach it what player and the resulting board.
	 * 
	 * 
	 * Ideally this would be replaced with the node's heuristic value based on the
	 * board and not the resulting board.
	 * 
	 * 
	 * @param board
	 * @param currQueenPos
	 * @param nextQueenPos
	 * @param arrowPos
	 */
	public SearchTreeNode(byte[][] board, byte[] currQueenPos, byte[] nextQueenPos, byte[] arrowPos, byte player) {
		setBoard(board);
		setChildren(new ArrayList<>());
		setCurrQueenPos(currQueenPos);
		setNextQueenPos(nextQueenPos);
		setArrowPos(arrowPos);
		setPlayer(player);
	}
	
	public SearchTreeNode(byte[][] board, byte[] currQueenPos, byte[] nextQueenPos, byte[] arrowPos) {
		setBoard(board);
		setChildren(new ArrayList<>());
		setCurrQueenPos(currQueenPos);
		setNextQueenPos(nextQueenPos);
		setArrowPos(arrowPos);
	}

	public SearchTreeNode(int heuristic, byte[] currQueenPos, byte[] nextQueenPos, byte[] arrowPos, byte player,
			boolean isWhitePlayer) {
		setChildren(new ArrayList<>());
		setCurrQueenPos(currQueenPos);
		setNextQueenPos(nextQueenPos);
		setArrowPos(arrowPos);
		setPlayer(player);
		setWhitePlayer(isWhitePlayer);
		setHeuristicValue(heuristic);
	}

	public void add(SearchTreeNode child) {
		children.add(child);
		child.setParent(this);
	}

	public void setNextNode(SearchTreeNode child) {
		this.nextNode = child;
	}

	public SearchTreeNode getNextNode() {
		return this.nextNode;
	}

	public void setBoard(byte[][] board) {
		this.board = board;
	}

	public byte[][] getBoard() {
		return this.board;
	}

	public void setParent(SearchTreeNode parent) {
		this.parent = parent;
	}

	public void setHeuristicValue(int val) {
		this.heuristicValue = val;
	}

	public int getHeuristicValue() {
		return this.heuristicValue;
	}

	public byte getPlayer() {
		return this.player;
	}

	public SearchTreeNode getParent() {
		return this.parent;
	}

	public ArrayList<SearchTreeNode> getChildren() {
		return this.children;
	}

	public boolean isWhitePlayer() {
		return this.isWhitePlayer;
	}
	
	public void setWhitePlayer(boolean isWhitePlayer) {
		this.isWhitePlayer = isWhitePlayer;
	}

	public void setChildren(ArrayList<SearchTreeNode> children) {
		this.children = children;
	}

	public void setPlayer(byte player) {
		this.player = player;
	}

	public void printTree() {
		System.out.println(this.toString());
		for (SearchTreeNode child : getChildren()) {
			child.printTree();
		}
	}

	public byte[] getCurrQueenPos() {
		return currQueenPos;
	}

	public void setCurrQueenPos(byte[] currQueenPos) {
		this.currQueenPos = currQueenPos;
	}

	public byte[] getNextQueenPos() {
		return nextQueenPos;
	}

	public void setNextQueenPos(byte[] nextQueenPos) {
		this.nextQueenPos = nextQueenPos;
	}

	public byte[] getArrowPos() {
		return arrowPos;
	}

	public void setArrowPos(byte[] arrowPos) {
		this.arrowPos = arrowPos;
	}
	
	@Override
	public String toString() {
		return "SearchTreeNode [heuristicValue=" + heuristicValue + ", currQueenPos=" + Arrays.toString(currQueenPos)
				+ ", nextQueenPos=" + Arrays.toString(nextQueenPos) + ", arrowPos=" + Arrays.toString(arrowPos)
				+ ", parent=" + parent + ", player=" + player + ", isWhitePlayer=" + isWhitePlayer;
	}


	/**
	 * @return
	 */
	public SearchTreeNode getFirstChild() {
		return children.get(0);
	}

	/**
	 * @return whether or not this node has any children
	 */
	public boolean isTerminal() {
		//FIXME this might be a dumb way of doing this..., feel free to make me more robust
		return getChildren() == null;
	}
}
