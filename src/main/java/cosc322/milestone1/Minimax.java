package cosc322.milestone1;

/**
 * 
 * Mostly experimental - trying to understand how it might work
 *
 */
public class Minimax {

	private SearchTree tree;
	private GameBoard board;
	
	public Minimax(GameBoard board, boolean isWhitePlayer) {
		setGameBoard(board);
		setTree(new SearchTree(board));
		SearchTreeNode root = new SearchTreeNode(board.getMatrix(), (byte) 1, isWhitePlayer);
		tree.setRoot(root);
	}
	
	/**
	 * Experiments -- Abandoned Doesn't work - see Alpha Beta Search
	 */
	public SearchTreeNode minimax(SearchTreeNode node, int depthRemaining) {
		if(node.getChildren().size() == 0 || depthRemaining == 0) {
			// this nodes heuristic value can be generated during the search tree creation
			return node;
		}

		int bestScore = (-1) * node.getPlayer() * Integer.MAX_VALUE; 		
		for(SearchTreeNode child : node.getChildren()) {
				int score = minimax(child, depthRemaining - 1).getHeuristicValue();
				if(node.getPlayer() == 1) {
					bestScore = Math.max(bestScore, score);
				} else {
					bestScore = Math.min(bestScore, score);
				}
				//tree.expandNode(child);
		}
		node.setHeuristicValue(bestScore);
		return node;
	}
	
	/**
	 * Experiments -- See Alpha Beta Search
	 */
	public SearchTreeNode alphabeta(SearchTreeNode node, int depthRemaining, int alpha, int beta) {
		// if terminal node and no children were generated or reached max depth
		if(node.getChildren().size() == 0 || depthRemaining == 0) {
			// this nodes heuristic value is generated during the search tree creation ..
			return node;
		}
	
		// for each of node kids
		for(SearchTreeNode child : node.getChildren()) {		
			// recursive call with -1 depth
			SearchTreeNode temp = alphabeta(child, depthRemaining - 1, alpha, beta);
			int score = temp.getHeuristicValue();
			
			//maximizing
			if(node.getPlayer() == 1) {
				//alpha = Math.max(alpha, score);
				if(alpha < score) {
					alpha = score;
					node.setNextNode(child);
				} 
				if(alpha >= beta) break;
			}  else {
				// minimizing
				//beta = Math.min(beta, score);
				if(beta > score) {
					beta = score;
					node.setNextNode(child);
				} 
				if (beta <= alpha) break;
			}
			//tree.expandNode(child);
		}
		node.setHeuristicValue(node.getPlayer() == 1 ? alpha : beta);

		return node;		
	}
	
	
	public void setGameBoard(GameBoard gameBoard) {
		this.board = gameBoard;
	}


	public SearchTree getTree() {
		return tree;
	}


	public void setTree(SearchTree tree) {
		this.tree = tree;
	}

	public GameBoard getBoard() {
		return board;
	}
}
