package models;

public class Queen extends Piece {

	private boolean white;
	private int id;

	public Queen(byte x, byte y, byte player) {
		super(new byte[] { x, y });
		setPlayer(player);
	}

	public Queen(byte[] pos, byte player) {
		super(pos);
		setPlayer(player);
	}

	public void setWhite(boolean isWhite) {
		this.white = isWhite;
	}

	/**
	 * Returns the player this queen belongs to, since there are only two options,
	 * this is a binary value, if it's not white, then it must be black
	 *
	 * @return whether or not this queen belongs to the white player
	 */
	public boolean isWhite() {
		return white;
	}

	/**
	 * Set the player that this queen belongs to. This should only be called once,
	 * during the instantiation of the queen object (i.e. in the constructor)
	 *
	 * @param player must be either 1 (for white player) or 2 (black player)
	 */
	private void setPlayer(byte player) {
		switch (player) {
		case 1:
			setWhite(true);
			setId(player);
			break;
		case 2:
			setWhite(false);
			setId(player);
			break;
		default:
			throw new IllegalArgumentException(player + "is not a player");
		}
	}

	@Override
	public String toString() {
		return "Queen [white=" + white + ", " + super.toString() + "]";

	}

}
