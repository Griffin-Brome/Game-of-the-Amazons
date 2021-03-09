package models;

import static utils.Constant.*;

public class Arrow extends Piece {

	public Arrow(byte x, byte y) {
		super(x, y);
		setId((byte) ARROW);
	}

	public Arrow(byte[] pos) {
		super(pos);
		setId((byte) ARROW);
	}

}
