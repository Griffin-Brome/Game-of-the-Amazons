package cosc322.milestone1;

public class Arrow extends Piece {
	
	public Arrow(byte x, byte y) {
		super(x, y);
		setId((byte) 3);
	}
	
	public Arrow(byte[] pos) {
		super(pos);
		setId((byte) 3);
	}

}

