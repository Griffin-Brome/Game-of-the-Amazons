package cosc322.milestone1;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import ygraph.ai.smartfox.games.amazons.AmazonsGameMessage;

/**
 * gameState from server comes in an integer ArrayList like this : 
 * 
 *	   [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
 *	10	0, 0, 0, 0, 2, 0, 0, 2, 0, 0, 0,
 *	9	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
 *	8	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
 *	7	0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 2,
 *	6	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
 *	5	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
 *	4	0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1,
 *	3	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
 *	2	0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
 *	1	0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0]
 * 
 * 	pos	   1  2  3  4  5  6  7  8  9  10
 * 
 *  Where 1 is represents white queen,
 *  2 represents black queen and 3 are arrows. 
 *  
 *  Also positions sent from the server are (y, x).
 *  
 *  So yeah.
 */
public class GameBoard {

	// Encoding on game state from server :
	private final byte ARROW = 3;
	private final byte BLACK_QUEEN = 2;
	private final byte WHITE_QUEEN = 1;
	private final byte BLANK = 0;

	private byte[] gameStateBytes;
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
		gameStateBytes = new byte[ROWS * COLS];
		boardMatrix = new byte[ROWS][COLS];
		
		whiteQueens = new ArrayList<>();
		blackQueens = new ArrayList<>();
		arrows = new ArrayList<>();
	}
	
	/**
	 * Updates the coordinates HashMap and matrix .. 
	 * Also prints a couple statements .. going to need a class/function to generate 
	 * possible actions. 
	 * 
	 * @param msgDetails
	 */
	@SuppressWarnings("unchecked")
	public void updateBoard(Map<String, Object> msgDetails){
		
		ArrayList<Integer> queenPosCurr = (ArrayList<Integer>)msgDetails.get(AmazonsGameMessage.QUEEN_POS_CURR);
		ArrayList<Integer> queenPosNext = (ArrayList<Integer>)msgDetails.get(AmazonsGameMessage.Queen_POS_NEXT);
		ArrayList<Integer> arrowPos = (ArrayList<Integer>)msgDetails.get(AmazonsGameMessage.ARROW_POS);
	
		if(this.coords.get(queenPosCurr) == BLACK_QUEEN) {
			System.out.println("Moving Black Queen from " + queenPosCurr);
			blackQueens.remove(queenPosCurr);
			blackQueens.add(queenPosCurr);
			
			if(this.coords.get(queenPosNext) == BLANK) {
				System.out.println("To a blank spot at " + queenPosNext);
				blackQueens.add(queenPosNext);
			} else {
				System.out.println("Not a blank spot.");
			}
		} 
		
		if(this.coords.get(queenPosCurr) == WHITE_QUEEN) {
			System.out.println("Moving White Queen from " + queenPosCurr);
			blackQueens.remove(queenPosCurr);
			blackQueens.add(queenPosCurr);
			
			if(this.coords.get(queenPosNext) == BLANK) {
				System.out.println("To a blank spot at " + queenPosNext);
				blackQueens.add(queenPosNext);
			} else {
				System.out.println("Not a blank spot.");
			}
		}
		
		if(this.coords.get(arrowPos) == BLANK) {
			System.out.println("Placing Arrow at a blank spot at " + arrowPos);
		} else {
			System.out.println("Placing Arrow at a non-blank spot at " + arrowPos);
		}	
	}
	
	/**
	 * A lot of this will not be necessary but I am not sure what 
	 * will end up being best/easiest/most efficient to use during
	 * actual searches so : 
	 * 
	 * - A HashMap of the piece coordinates on the board.
	 * These are stored in the same format that positions are passed from the server. -> coords
	 * 
	 * - A simple 11x11 matrix representation of the gameState ArrayList -> boardMatrix
	 * 
	 * - byte array version of gameState arrayList -> gameStateBytes
	 * 
	 * - the actual ArrayList -> gameState
	 * 
	 * @param gameState - from server
	 */
	public void setBoardState(ArrayList<Integer> gameState){
		// update local gameStateList
		this.gameState = gameState;
		
		// byte array
		for(int pos = 0; pos < gameState.size(); pos++) {
			this.gameStateBytes[pos] = gameState.get(pos).byteValue();
		}
		
		// update coordinates of pieces
		int pos = 12;
		for(int y = ROWS-1; y > 0 ; y--) {
			for(int x = 1; x < COLS; x++) {
				//positions from server are passed as y,x ...
				ArrayList<Integer> coords = new ArrayList<Integer>(Arrays.asList(y, x));
				int currentTile = gameState.get(pos);
				this.coords.put(coords, currentTile);
				
				// add to appropriate list of pieces
				if(currentTile==BLACK_QUEEN) {
					if(!blackQueens.contains(coords))
						blackQueens.add(coords);
				}
				
				if(currentTile==WHITE_QUEEN) {
					if(!whiteQueens.contains(coords))
						whiteQueens.add(coords);
				}
				
				if(currentTile==ARROW) {
					if(!arrows.contains(coords))
						arrows.add(coords);
				}
					
				pos++;
			}
			pos++;
		}
		
		// update matrix
		pos = 0;
		for(int row = 0; row < ROWS; row++) {
			for(int col = 0; col < COLS; col++) {
				//positions from server are passed as y,x ...
				this.boardMatrix[row][col] = gameState.get(pos++).byteValue();
			}
		}
	
		/*	uncomment to see matrix from server	*/
		
//		System.out.println("\nCurrent Board Matrix from Server:------------------------------------------");
//		for(int row = 0; row < ROWS; row++) {
//			for(int col = 0; col < COLS; col++) {
//				System.out.printf("%d, ", this.boardMatrix[row][col]);
//			}
//			System.out.println("");
//		}
		
		/* lists of pieces */
		
//		System.out.println("whitequeens :" + whiteQueens);
//		System.out.println("blackqueens" + blackQueens);
//		System.out.println("arrows" + arrows);
	}
	
//	public ArrayList<Integer> getPossibleMoves(AmazonsAIPlayer player){
//		ArrayList<Integer> moves = new ArrayList<>();
//		
//	}
	
	
	public ArrayList<Integer> getGameState() {
		return this.gameState;
	}
	
	public HashMap<ArrayList<Integer>, Integer> getAllCoords(){
		return this.coords;
	}
	
	public ArrayList<ArrayList<Integer>> getBlackQueens(){
		return this.blackQueens;
	}
	
	public ArrayList<ArrayList<Integer>> getWhiteQueens(){
		return this.whiteQueens;
	}
	
	public ArrayList<ArrayList<Integer>> getArrows(){
		return this.arrows;
	}
	
}
