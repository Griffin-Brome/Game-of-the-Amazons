package models;

import static utils.Constant.ARROW;

public class Arrow extends Piece {

	public Arrow(byte x, byte y) {
		super(x, y);
		setId(ARROW);
	}

	public Arrow(byte[] pos) {
		super(pos);
		setId(ARROW);
	}

}
