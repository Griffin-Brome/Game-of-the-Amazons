package cosc322.milestone1;

/**
 * Adapted from fig. 5.7, pg.310 of Artificial Intelligence, A Modern Approach (4th Edition)
 */
public class AlphaBetaSearch implements SearchStrategy{
    int alpha, beta;
    SearchTree tree;

    public int getAlpha() { return alpha; }

    public void setAlpha(int alpha) { this.alpha = alpha; }

    public int getBeta() { return beta; }

    public void setBeta(int beta) { this.beta = beta; }

    public SearchTreeNode search() {
        return max(tree.getRoot());
    }

    /**
     * Major difference between this and the textbook's version, is that I don't return a tuple of [Move, value], and
     * instead just return the resultant node that you would get to via performing the move. Also this is a more
     * object-oriented approach over the textbook's functional one, where alpha and beta are not passed as parameters,
     * but instead exist as class attributes
     *
     * @param node
     * @return
     */
    private SearchTreeNode max(SearchTreeNode node) {
        if (node.isTerminal())
            return node;
        SearchTreeNode max = node.getFirstChild();
        SearchTreeNode temp;
        for (SearchTreeNode child: node.getChildren()) {
           temp = min(child);
           if (temp.getHeuristicValue() > max.getHeuristicValue())
               max = temp;
               if (temp.getHeuristicValue() > getAlpha())
                   setAlpha(temp.getHeuristicValue());
           if (max.getHeuristicValue() >= getBeta())
               return max;
        }
        return max;

    }

    /**
     * See above method for implementation details
     *
     * @param node
     * @return
     */
    private SearchTreeNode min(SearchTreeNode node) {
        if (node.isTerminal())
            return node;
        SearchTreeNode min = node.getFirstChild();
        SearchTreeNode temp;
        for (SearchTreeNode child: node.getChildren()) {
            temp = max(child);
            if (temp.getHeuristicValue() < min.getHeuristicValue())
                min = temp;
                if (temp.getHeuristicValue() < getBeta())
                    setBeta(temp.getHeuristicValue());
            if (min.getHeuristicValue() <= getAlpha())
                return min;
        }
        return min;
    }
}
