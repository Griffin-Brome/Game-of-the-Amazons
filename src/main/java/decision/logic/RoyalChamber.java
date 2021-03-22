package decision.logic;

import java.util.ArrayList;

import cern.colt.Arrays;
import cosc322.amazons.GameBoard;
import models.Queen;

import static utils.GameLogic._queensFromBoard;

public class RoyalChamber {
	private boolean isWhitePlayer;
	private ArrayList<Queen> myQueens;
	private ArrayList<Queen> freeQueens;
	private ArrayList<Queen> chamberQueens;
	private ArrayList<Queen> enemyQueens;
	private byte[][] boardMatrix;
	
	/**
	 * Used to check if a players queens are in royal chambers.
	 * Uses a modified A* using Manhattan Distance to see if a path exists
	 * to an enemy queen, if no path exists then we're in a chamber. 
	 * 
	 * @param isWhitePlayer
	 * @param board
	 */
	public RoyalChamber(boolean isWhitePlayer, byte[][] board) {
		this.isWhitePlayer = isWhitePlayer;
		this.myQueens = _queensFromBoard(board, isWhitePlayer);
		this.enemyQueens = _queensFromBoard(board, !isWhitePlayer);
		this.boardMatrix = board;
		this.freeQueens = new ArrayList<>(this.myQueens);
		this.chamberQueens = new ArrayList<>();
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
		AStar as = new AStar(boardMatrix);
		
		for(Queen myQueen : myQueens) {
			boolean inChamber = true;
			if(!myQueen.inChamber()) {
				for(Queen enemyQueen : enemyQueens) {
					if(as.search(myQueen, enemyQueen)) {
						inChamber = false;
						break;
					} 
				}
				myQueen.setInChamber(inChamber);

				if(inChamber) {
					chamberQueens.add(myQueen);
					freeQueens.remove(myQueen);
				}
			}
		}
	}

	public ArrayList<Queen> getChamberedQueens() {
		return chamberQueens;
	}

	public ArrayList<Queen> getFreeQueens() {
		return freeQueens;
	}
}
