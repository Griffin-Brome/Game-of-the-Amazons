package cosc322.milestone1;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import ygraph.ai.smartfox.games.amazons.AmazonsGameMessage;

/**
 * gameState from server comes in an integer ArrayList like this :
 * 
 *0       0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
 *1 	  0, 0, 0, 0, 1, 0, 0, 2, 0, 0, 0, 
 *2		  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
 *3		  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
 *4    	  0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 2, 
 *5		  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
 *6		  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
 *7		  0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 2, 
 *8		  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
 *9		  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
 *10      0, 0, 0, 0, 1, 0, 0, 2, 0, 0, 0,
 * x
 * pos y     1  2  3  4  5  6  7  8  9  10
 * 
 * Where 1 represents white queen, 2 represents black queen and 3 are arrows.
 * 
 * Also positions sent from the server are (y, x).
 * 
 */
public class GameBoard {

	// Encoding on game state from server :
	private final byte ARROW = 3;
	private final byte BLACK_QUEEN = 2;
	private final byte WHITE_QUEEN = 1;
	private final byte BLANK = 0;

	private ArrayList<Integer> gameState;
	private final byte ROWS = 11;
	private final int COLS = 11;

	// HashMap mapping board positions passed from server to gamestate integer
	private HashMap<ArrayList<Integer>, Integer> coords;
	private byte[][] boardMatrix;

	private ArrayList<ArrayList<Integer>> arrows;
	private ArrayList<ArrayList<Integer>> whiteQueens;
	private ArrayList<ArrayList<Integer>> blackQueens;

	public GameBoard() {
		coords = new HashMap<>();
		boardMatrix = new byte[ROWS][COLS];

		whiteQueens = new ArrayList<>();
		blackQueens = new ArrayList<>();
		arrows = new ArrayList<>();
	}

	@SuppressWarnings("unchecked")
	public void updateBoard(Map<String, Object> msgDetails) {
		ArrayList<Integer> queenPosCurr = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.QUEEN_POS_CURR);
		ArrayList<Integer> queenPosNext = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.Queen_POS_NEXT);
		ArrayList<Integer> arrowPos = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.ARROW_POS);
		updateBoard(queenPosCurr, queenPosNext, arrowPos);
	}
	
	/**
	 * Updates the coordinates HashMap and matrix
	 * Prints some debugging statements
	 * 
	 * @param msgDetails
	 */
	@SuppressWarnings("unchecked")
	public void updateBoard(ArrayList<Integer> queenPosCurr, ArrayList<Integer> queenPosNext, ArrayList<Integer> arrowPos) {
		
		if (this.coords.get(queenPosCurr) == BLACK_QUEEN) {
			System.out.println("\nMoving Black Queen from " + queenPosCurr);
			boardMatrix[queenPosCurr.get(1)][queenPosCurr.get(0)] = BLANK;
			boardMatrix[queenPosNext.get(1)][queenPosNext.get(0)] = BLACK_QUEEN;
						
			for(int i = 0; i < blackQueens.size(); i++) {
				if(blackQueens.get(i).get(0)==queenPosCurr.get(0)&&blackQueens.get(i).get(1)==queenPosCurr.get(1)) {
					blackQueens.remove(i);	
				}
			}
			
			if (this.coords.get(queenPosNext) == BLANK) {
				System.out.println("\nTo a blank spot at " + queenPosNext);
				blackQueens.add(queenPosNext);
			} else {
				System.out.println("Not a blank spot.");
			}
		}

		if (this.coords.get(queenPosCurr) == WHITE_QUEEN) {

			System.out.println("\nMoving White Queen from " + queenPosCurr);
			boardMatrix[queenPosCurr.get(1)][queenPosCurr.get(0)] = BLANK;
			boardMatrix[queenPosNext.get(1)][queenPosNext.get(0)] = WHITE_QUEEN;
			
			for(int i = 0; i < whiteQueens.size(); i++) {
				if(whiteQueens.get(i).get(0)==queenPosCurr.get(0)&&whiteQueens.get(i).get(1)==queenPosCurr.get(1)) {
					whiteQueens.remove(i);	
				}
			}
			
			if (this.coords.get(queenPosNext) == BLANK) {
				System.out.println("\nTo a blank spot at " + queenPosNext);
				whiteQueens.add(queenPosNext);
			} else {
				System.out.println("Not a blank spot.");
			}
		}

		if (this.coords.get(arrowPos) == BLANK) {
			System.out.println("\nPlacing Arrow at a blank spot at " + arrowPos + "\n");
			boardMatrix[arrowPos.get(1)][arrowPos.get(0)] = ARROW;
			arrows.add(arrowPos);
		}
	}

	/**
	 * A lot of this will not be necessary but I am not sure what will end up being
	 * best/easiest/most efficient to use during actual searches so :
	 * 
	 * - A HashMap of the piece coordinates on the board. These are stored in the
	 * same format that positions are passed from the server. -> coords
	 * 
	 * - A simple 11x11 matrix representation of the gameState ArrayList ->
	 * boardMatrix
	 * 
	 * - the actual ArrayList -> gameState
	 * 
	 * @param gameState - from server
	 */
	public void setBoardState(ArrayList<Integer> gameState) {
		// update local gameStateList
		this.gameState = gameState;

		// update coordinates of pieces
		int pos = 12;
		for (int y = ROWS - 1; y > 0; y--) {
			for (int x = 1; x < COLS; x++) {
				
				// positions from server are passed as y,x ...
				ArrayList<Integer> coords = new ArrayList<Integer>(Arrays.asList(y, x));
				int currentTile = gameState.get(pos);
				this.coords.put(coords, currentTile);
				
				this.boardMatrix[x][y] = gameState.get(pos).byteValue();

				// add to appropriate list of pieces
				if (currentTile == BLACK_QUEEN) {
					blackQueens.add(coords);
				}

				if (currentTile == WHITE_QUEEN) {
					whiteQueens.add(coords);
				}

				if (currentTile == ARROW) {
					arrows.add(coords);
				}

				pos++;
			}
			pos++;
		}

		/* uncomment to see matrix from server */

//		System.out.println("\nCurrent Board Matrix from Server:\n------------------------------------------");
//		for (int row = 0; row < ROWS; row++) {
//			for (int col = 0; col < COLS; col++) {
//				System.out.printf("%d, ", this.boardMatrix[row][col]);
//			}
//			System.out.println("");
//		}
//
//		/* lists of pieces */
//
//		System.out.println("whitequeens :" + whiteQueens);
//		System.out.println("blackqueens" + blackQueens);
//		System.out.println("arrows" + arrows);
	}

	public ArrayList<ArrayList<Integer>> getPossibleMoves(boolean isWhitePlayer) {
		ArrayList<ArrayList<Integer>> moves = new ArrayList<>();
		if (isWhitePlayer) {
			for (ArrayList<Integer> queenPos : whiteQueens) {
				moves.addAll(getPossibleMoves(queenPos));
			}
		} else {
			for (ArrayList<Integer> queenPos : blackQueens) {
				moves.addAll(getPossibleMoves(queenPos));
			}
		}
		
		return moves;
	}

	public ArrayList<ArrayList<Integer>> getPossibleMoves(ArrayList<Integer> pos) {
		ArrayList<ArrayList<Integer>> moves = new ArrayList<>();
		int y = pos.get(0);
		int x = pos.get(1);

		// go right
//		System.out.printf("Right from %s\n", pos);
		while (++x < COLS) {
			if (boardMatrix[x][y] == BLANK) {
				ArrayList<Integer> newPos = new ArrayList<>();
//				System.out.printf("Board: %d,  y:%d, x:%d\n", boardMatrix[x][y], y, x);
				newPos.add(y);	
				newPos.add(x);
				newPos.add(pos.get(0)); // queen original position
				newPos.add(pos.get(1));
				moves.add(newPos);
			} else {
				break;
			}
		}
		
		y = pos.get(0);
		x = pos.get(1);

		// go left
//		System.out.printf("Left from %s\n", pos);
		while (--x > 0) {
			if (boardMatrix[x][y] == BLANK) {
				ArrayList<Integer> newPos = new ArrayList<>();
//				System.out.printf("Board: %d,  y:%d, x:%d\n", boardMatrix[x][y], y, x);
				newPos.add(y);
				newPos.add(x);
				newPos.add(pos.get(0)); // queen original position
				newPos.add(pos.get(1));
				moves.add(newPos);
			} else {
				break;
			}
		}
		
		y = pos.get(0);
		x = pos.get(1);

		// go up
//		System.out.printf("Up from %s\n", pos);
		while (++y < COLS) {
			if (boardMatrix[x][y] == BLANK) {
				ArrayList<Integer> newPos = new ArrayList<>();
//				System.out.printf("Board: %d,  y:%d, x:%d\n", boardMatrix[x][y], y, x);
				newPos.add(y);
				newPos.add(x);
				newPos.add(pos.get(0)); // queen original position
				newPos.add(pos.get(1));
				moves.add(newPos);
			} else {
				break;
			}
		}
		
		y = pos.get(0);
		x = pos.get(1);

		// go down
//		System.out.printf("Down from %s\n", pos);
		while (--y > 0) {
			if (boardMatrix[x][y] == BLANK) {
				ArrayList<Integer> newPos = new ArrayList<>();
//				System.out.printf("Board: %d,  y:%d, x:%d\n", boardMatrix[x][y], y, x);
				newPos.add(y);
				newPos.add(x);
				newPos.add(pos.get(0)); // queen original position
				newPos.add(pos.get(1));
				moves.add(newPos);
			} else {
				break;
			}
		}
		
		
	
		y = pos.get(0);
		x = pos.get(1);

		// go diagonal up right
//		System.out.printf("Diag up right from %s\n", pos);
		while (++y < ROWS && ++x < COLS) {
			if (boardMatrix[x][y] == BLANK) {
				ArrayList<Integer> newPos = new ArrayList<>();
//				System.out.printf("Board: %d,  y:%d, x:%d\n", boardMatrix[x][y], y, x);
				newPos.add(y);
				newPos.add(x);
				newPos.add(pos.get(0)); // queen original position
				newPos.add(pos.get(1));
				moves.add(newPos);
			} else {
				break;
			}
		}
		
		y = pos.get(0);
		x = pos.get(1);
		
		// go diagonal up left
//		System.out.printf("Diag up left from %s\n", pos);
		while (++y < ROWS && --x > 0) {
			if (boardMatrix[x][y] == BLANK) {
				ArrayList<Integer> newPos = new ArrayList<>();
//				System.out.printf("Board: %d,  y:%d, x:%d\n", boardMatrix[x][y], y, x);
				newPos.add(y);
				newPos.add(x);
				newPos.add(pos.get(0)); // queen original position
				newPos.add(pos.get(1));
				moves.add(newPos);
			} else {
				break;
			}
		}
		
		y = pos.get(0);
		x = pos.get(1);
		
		// go diagonal down left
//		System.out.printf("Diag down left from %s\n", pos);
		while (--y > 0 && --x > 0) {
			if (boardMatrix[x][y] == BLANK) {
				ArrayList<Integer> newPos = new ArrayList<>();
//				System.out.printf("Board: %d,  y:%d, x:%d\n", boardMatrix[x][y], y, x);
				newPos.add(y);
				newPos.add(x);
				newPos.add(pos.get(0)); // queen original position
				newPos.add(pos.get(1));
				moves.add(newPos);
			} else {
				break;
			}
		}
		
		y = pos.get(0);
		x = pos.get(1);
		
		// go diagonal down right
//		System.out.printf("Diag down right from %s\n", pos);
		while (--y > 0 && ++x < COLS) {
			if (boardMatrix[x][y] == BLANK) {
				ArrayList<Integer> newPos = new ArrayList<>();
//				System.out.printf("Board: %d,  y:%d, x:%d\n", boardMatrix[x][y], y, x);
				newPos.add(y);
				newPos.add(x);
				newPos.add(pos.get(0)); // queen original position
				newPos.add(pos.get(1));
				moves.add(newPos);
			} else {
				break;
			}
		}
		
		
		return moves;
	}

	public ArrayList<Integer> getGameState() {
		return this.gameState;
	}

	public HashMap<ArrayList<Integer>, Integer> getAllCoords() {
		return this.coords;
	}

	public ArrayList<ArrayList<Integer>> getBlackQueens() {
		return this.blackQueens;
	}

	public ArrayList<ArrayList<Integer>> getWhiteQueens() {
		return this.whiteQueens;
	}

	public ArrayList<ArrayList<Integer>> getArrows() {
		return this.arrows;
	}
	
	public byte[][] getMatrix(){
		return this.boardMatrix;
	}

}
