package cosc322.milestone1;

public class Minimax {

	private SearchTree tree;
	private boolean isWhitePlayer;
	private GameBoard board;
	
	public Minimax(GameBoard board, boolean isWhitePlayer) {
		
		this.board = board;
		this.tree = new SearchTree(board);
	
		SearchTreeNode root = new SearchTreeNode(board.getMatrix(), (byte) 1, isWhitePlayer);
		tree.setRoot(root);
		tree.expandNode(root);
		
	}

	
	/**
	 * Abandoned Doesn't work - see alpha beta
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
			
				tree.expandNode(child);
		}
		node.setHeuristicValue(bestScore);
		return node;
	}
	

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
			
			tree.expandNode(child);
			
		}
		
		node.setHeuristicValue(node.getPlayer() == 1 ? alpha : beta);
		
		return node;
		
	}
	
	public SearchTreeNode iterativeDeepening(int depth) {
		return alphabeta(tree.getRoot(), depth, -Integer.MAX_VALUE, Integer.MAX_VALUE);
	}
	
}
