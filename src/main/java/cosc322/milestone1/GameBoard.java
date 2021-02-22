package cosc322.milestone1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import ygraph.ai.smartfox.games.amazons.AmazonsGameMessage;

/**
 * gameState from server stored in matrix : 
 * 
 *9 	  0, 0, 0, 2, 0, 0, 2, 0, 0, 0, 
 *8		  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
 *7		  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
 *6    	  2, 0, 0, 0, 0, 0, 0, 0, 0, 2, 
 *5		  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
 *4 	  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
 *3		  1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 
 *2		  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
 *1		  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
 *0       0, 0, 0, 1, 0, 0, 1, 0, 0, 0,
 *y
 *pos x   0  1  2  3  4  5  6  7  8  9
 * 
 * Where 1 represents white queen, 2 represents black queen and 3 are arrows.
 * 
 * Also positions sent from the server are (y, x).
 * They're stored as byte arrays in respective arraylists in that format.
 * 
 */
public class GameBoard {

	// Encoding on game state from server :
	private final byte ARROW = 3;
	private final byte BLACK_QUEEN = 2;
	private final byte WHITE_QUEEN = 1;
	private final byte BLANK = 0;

	private ArrayList<Integer> gameState;
	public final int ROWS = 10;
	public final int COLS = 10;
	
	private byte[][] boardMatrix;

	private ArrayList<byte[]> arrows;
	private ArrayList<byte[]> whiteQueens;
	private ArrayList<byte[]> blackQueens;

	public GameBoard() {
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
	 */
	@SuppressWarnings("unchecked")
	public void updateBoard(ArrayList<Integer> queenPosCurr, ArrayList<Integer> queenPosNext, ArrayList<Integer> arrowPos) {
		moveQueen(queenPosCurr, queenPosNext);
		shootArrow(arrowPos);
	}

	/**
	 * Moves queen from position A to B
	 *
	 * @param currPos TODO
	 *
	 * @param endPos TODO
	 */
	private void moveQueen(ArrayList<Integer> currPos, ArrayList<Integer> endPos) {
		// Check if new position is a valid state
		if (!isValid(endPos)) {
			System.err.println("Cannot move to these coordinates");
		} else {
			// Determine inhabitant of tile
			int tileOccupant = getOccupant(currPos);
			switch (tileOccupant) {
				case BLACK_QUEEN:
					System.out.println("\nMoving Black Queen from " + currPos);
					updateMatrix(currPos, endPos, BLACK_QUEEN);
					updatePieces(currPos, endPos, BLACK_QUEEN);
					System.out.println("\nTo a blank spot at " + endPos);

					break;
				case WHITE_QUEEN:
					System.out.println("\nMoving White Queen from " + currPos);
					updateMatrix(currPos, endPos, WHITE_QUEEN);
					updatePieces(currPos, endPos, WHITE_QUEEN);
					System.out.println("\nTo a blank spot at " + endPos);
					
					break;
				default:
					System.err.println("Selected tile does not contain a queen -> " + currPos + " - " + getOccupant(currPos));
					System.err.println("white");
					for(byte[] wQueen : whiteQueens) {
						System.err.println(Arrays.toString(wQueen));
					}
					System.err.println("black");
					for(byte[] bQueen : blackQueens) {
						System.err.println(Arrays.toString(bQueen));
					}
					return;
			}
		}
	}
	
	/**
	 * Removed the coordinates hasmap in favor of this function that checks the matrix directly. 
	 * 
	 * @param pos
	 * @return
	 */
	public int getOccupant(ArrayList<Integer> pos) {
		int y = pos.get(0) -1;
		int x = pos.get(1) -1;
		return boardMatrix[x][y];
	}
	
	/**
	 * Update the matrix based on the arraylist positions received from the server
	 * 
	 * @param currPos
	 * @param endPos
	 * @param piece
	 */
	public void updateMatrix(ArrayList<Integer> currPos, ArrayList<Integer> endPos, byte piece) {
		int currY = currPos.get(0) -1;
		int currX = currPos.get(1) -1;
		int endY  = endPos.get(0) -1;
		int endX  = endPos.get(1) -1;
		
		boardMatrix[currX][currY] = BLANK;
		boardMatrix[endX][endY] = piece;	
	}
	
	/**
	 * Update the arraylists with the new positions from the server. 
	 * May actually be better to leave them as arraylists to keep from converting back and forth.
	 * 
	 * @param currPos
	 * @param endPos
	 * @param piece
	 */
	public void updatePieces(ArrayList<Integer> currPos, ArrayList<Integer> endPos, byte piece) {
		switch(piece) {
		case BLACK_QUEEN:

			for(int i = 0; i < blackQueens.size(); i++) { 
				if(blackQueens.get(i)[0] == currPos.get(0) && blackQueens.get(i)[1] == currPos.get(1)) { 
					blackQueens.remove(i);
				}
			}
				
			blackQueens.add(new byte[] {endPos.get(0).byteValue(), endPos.get(1).byteValue()});
			break;
		case WHITE_QUEEN:
			
			for(int i = 0; i < whiteQueens.size(); i++) {
				if(whiteQueens.get(i)[0] == currPos.get(0) && whiteQueens.get(i)[1] == currPos.get(1)) { 
					whiteQueens.remove(i);
				}
			}
			
			whiteQueens.add(new byte[] {endPos.get(0).byteValue(), endPos.get(1).byteValue()});
			break;
		case ARROW: 
			arrows.add(new byte[] {endPos.get(0).byteValue(), endPos.get(1).byteValue()});
			break;
		}

	}
	

	/**
	 * Check if position is empty on board
	 *
	 * @param pos position to check
	 * @return whether or not the tile is occupied
	 */
	private boolean isValid(ArrayList<Integer> pos) {
		int y = pos.get(0) -1;
		int x = pos.get(1) -1;
		return boardMatrix[x][y] == BLANK;
	}


	/**
	 * Shoots an arrow at the position
     *
	 * @param arrowPos Coords to shoot arrow at
	 */
	private void shootArrow(ArrayList<Integer> arrowPos) {
	    if (!isValid(arrowPos)) {
	    	System.err.println("Selected tile is occupied");
	    	return;
		} else {
			System.out.println("\nPlacing Arrow at a blank spot at " + arrowPos + "\n");
			int x = arrowPos.get(1) -1;
			int y = arrowPos.get(0) -1;
			boardMatrix[x][y] = ARROW;
			arrows.add(new byte[] {arrowPos.get(0).byteValue(), arrowPos.get(1).byteValue()});
		}
	}

	/**
	 * A lot of this will not be necessary but I am not sure what will end up being
	 * best/easiest/most efficient to use during actual searches so :
	 * 
	 * - A simple 10x10 matrix representation of the gameState ArrayList ->
	 * boardMatrix
	 * 
	 * - the actual ArrayList -> gameState
	 * 
	 * @param gameState - from server
	 * @param showBoard - Whether or not to output the matrix representation to the console
	 */
	public void setBoardState(ArrayList<Integer> gameState, boolean showBoard) {
		// update local gameStateList
		setGameState(gameState);

		// update coordinates of pieces
		int pos = 12;
		for (int y = ROWS; y > 0; y--) {
			for (int x = 1; x < COLS+1; x++) {
				
				// positions from server are passed as y,x ...
				ArrayList<Integer> position = new ArrayList<Integer>(Arrays.asList(y, x));
				int occupant = gameState.get(pos);

				// add to appropriate list of pieces
				byte [] bytePos = new byte[]{position.get(0).byteValue(), position.get(1).byteValue()};
                switch (occupant) {
					case BLACK_QUEEN:
						blackQueens.add(bytePos);
						break;
					case WHITE_QUEEN:
						whiteQueens.add(bytePos);
						break;
					case ARROW:
						arrows.add(bytePos);
				}
				pos++;
			}
			pos++;
		}

		// update matrix ..
		pos = 12;
		for(int y = ROWS-1; y >= 0; y--) {
			for(int x = 0; x < COLS; x++) {
				this.boardMatrix[x][y] = gameState.get(pos++).byteValue();
			}
			pos++;
		}
		

		if (showBoard) {
			System.out.println("\nCurrent Board Matrix from Server:\n------------------------------------------");
			for (int y = 0; y < ROWS; y++) {
				for (int x = 0; x < COLS; x++) {
					System.out.printf("(%d,%d) => %d, ", x, y, this.boardMatrix[x][y]);
				}
				System.out.println("");
			}

			/* lists of pieces */


			for(byte[] wQueen : whiteQueens) {
				System.out.println(Arrays.toString(wQueen));
			}
			for(byte[] bQueen : blackQueens) {
				System.out.println(Arrays.toString(bQueen));
			}
			for(byte[] arrow : arrows) {
				System.out.println(Arrays.toString(arrow));
			}
		}
	}

	/**
	 *
	 * @param gameState
	 */
	private void setGameState(ArrayList<Integer> gameState) {
		this.gameState = gameState;
	}

	public ArrayList<byte[]> getPossibleMoves(boolean isWhitePlayer) {
		ArrayList<byte[]> moves = new ArrayList<>();
		if (isWhitePlayer) {
			for (byte[] queenPos : whiteQueens) {
				moves.addAll(getPossibleMoves(queenPos));
			}
		} else {
			for (byte[] queenPos : blackQueens) {
				moves.addAll(getPossibleMoves(queenPos));
			}
		}
		
		return moves;
	}

	/**
	 * Likely to be replaced with the recursive approach but updated nonetheless.
	 * Simply moves in all 8 directions from a position and checks for possible moves.
	 * 
	 * @param pos
	 * @return
	 */
	public ArrayList<byte[]> getPossibleMoves(byte[] pos) {
		ArrayList<byte[]> moves = new ArrayList<>();
		byte y = (byte) (pos[0] - 1);
		byte x = (byte) (pos[1] - 1);

		while (++x < COLS && isBlank(boardMatrix[x][y])) {
			byte[] newPos = new byte[4];
			addMove(pos, moves, y, x, newPos);
		}
		
		y = (byte) (pos[0] -1);
		x = (byte) (pos[1] -1);

		// go left
		while (--x > 0 && isBlank(boardMatrix[x][y])) {
			byte[] newPos = new byte[4];
			addMove(pos, moves, y, x, newPos);
		}
	
		
		y = (byte) (pos[0] -1);
		x = (byte) (pos[1] -1);
		
		// go up
		while (++y < COLS && isBlank(boardMatrix[x][y])) {
			byte[] newPos = new byte[4];
			addMove(pos, moves, y, x, newPos);
		}
		
		
		y = (byte) (pos[0] -1);
		x = (byte) (pos[1] -1);

		// go down
		while (--y > 0 && isBlank(boardMatrix[x][y])) {
			byte[] newPos = new byte[4];
			addMove(pos, moves, y, x, newPos);
		}
		
		
		y = (byte) (pos[0] -1);
		x = (byte) (pos[1] -1);

		// go diagonal up right
		while (++y < ROWS && ++x < COLS && isBlank(boardMatrix[x][y])) {
			byte[] newPos = new byte[4];
			addMove(pos, moves, y, x, newPos);
		}
		
		
		y = (byte) (pos[0] -1);
		x = (byte) (pos[1] -1);
		
		// go diagonal up left
		while (++y < ROWS && --x > 0 && isBlank(boardMatrix[x][y])) {
			byte[] newPos = new byte[4];
			addMove(pos, moves, y, x, newPos);
		}
		
		
		y = (byte) (pos[0] -1);
		x = (byte) (pos[1] -1);
		
		// go diagonal down left
		while (--y > 0 && --x > 0 && isBlank(boardMatrix[x][y])) {
			byte[] newPos = new byte[4];
			addMove(pos, moves, y, x, newPos);
		}
		
		
		y = (byte) (pos[0] -1);
		x = (byte) (pos[1] -1);
		
		// go diagonal down right
		while (--y > 0 && ++x < COLS && isBlank(boardMatrix[x][y])) {
			byte[] newPos = new byte[4];
			addMove(pos, moves, y, x, newPos);
		}
		return moves;
	}

	private void addMove(byte[] pos, ArrayList<byte[]> moves, byte y, byte x, byte[] newPos) {
		newPos[0] = (byte) (y + 1);
		newPos[1] = (byte) (x + 1);
		newPos[2] = (byte) (pos[0]); // queen original position
		newPos[3] = (byte) (pos[1]);
		moves.add(newPos);
	}

	private boolean isBlank(byte boardMatrix1) {
		return boardMatrix1 == BLANK;
	}

	public ArrayList<Integer> getGameState() {
		return this.gameState;
	}

	public ArrayList<byte[]> getBlackQueens() {
		return this.blackQueens;
	}

	public ArrayList<byte[]> getWhiteQueens() {
		return this.whiteQueens;
	}

	public ArrayList<byte[]> getArrows() {
		return this.arrows;
	}
	
	public byte[][] getMatrix(){
		return this.boardMatrix;
	}

}