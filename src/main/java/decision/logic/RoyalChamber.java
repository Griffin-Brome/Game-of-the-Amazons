package decision.logic;

import java.util.ArrayList;

import cern.colt.Arrays;
import cosc322.amazons.GameBoard;
import models.Queen;

public class RoyalChamber {
	private boolean isWhitePlayer;
	private ArrayList<Queen> myQueens;
	private ArrayList<Queen> enemyQueens;
	private GameBoard gameBoard;
	
	/**
	 * Used to check if a players queens are in royal chambers.
	 * Uses a modified A* using Manhattan Distance to see if a path exists
	 * to an enemy queen, if no path exists then we're in a chamber. 
	 * 
	 * @param isWhitePlayer
	 * @param gameBoard
	 */
	public RoyalChamber(boolean isWhitePlayer, GameBoard gameBoard) {
		this.isWhitePlayer = isWhitePlayer;
		this.myQueens = isWhitePlayer ? gameBoard.getWhiteQueens() : gameBoard.getBlackQueens();
		this.enemyQueens = isWhitePlayer ? gameBoard.getBlackQueens() : gameBoard.getWhiteQueens();
		this.gameBoard = gameBoard;
	}
	
	/**
	 * For each queen not known to be in a chamber, check if a
	 * path exists to any of the enemy queens. I guess we could improve this
	 * by checking which of the enemy queens is closer before performing the search?
	 * 
	 * Currently just sets the inChamber boolean to true on the Queen object.
	 * 
	 * TODO: remove debug statements once it works. 
	 */
	public void checkRoyalChambers() {
		AStar as = new AStar(gameBoard);
		
		for(Queen myQueen : myQueens) {
			boolean inChamber = true;
			if(!myQueen.inChamber()) {
				for(Queen enemyQueen : enemyQueens) {
					if(as.search(myQueen, enemyQueen)) {
						inChamber = false;
						break;
					} 
				}
				if(inChamber) System.out.println("Queen at " + Arrays.toString(myQueen.getPosition()) + " in chamber");
				myQueen.setInChamber(inChamber);
			}
		}
	}
}
