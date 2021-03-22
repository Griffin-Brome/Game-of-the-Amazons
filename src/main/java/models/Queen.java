package models;
import static utils.Constant.*;

public class Queen extends Piece {
	private boolean white;
	private boolean inChamber;
	private int id;
	// Player byte is actually is used to set a queen as white or black!

	public Queen(byte x, byte y, byte player) {
		super(new byte[] { x, y });
		setPlayer(player);
		setInChamber(false);
	}

	public Queen(byte[] pos, byte player) {
		super(pos);
		setPlayer(player);
		setInChamber(false);
	}

	/**
	 * Set whether or not a queen is white.
	 * @param isWhite
	 */
	public void setWhite(boolean isWhite) {
		this.white = isWhite;
	}

	/**
	 * Set the inChamber value for a queen
	 */
	public void setInChamber(boolean inChamber) {
		this.inChamber = inChamber;
	}

	/**
	 * Returns true if a queen was determined to be in a chamber.
	 * @return whether or not queen is in chamber
	 */
	public boolean inChamber() {
		return this.inChamber;
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
		case WHITE_QUEEN:
			setWhite(true);
			setId(player);
			break;
		case BLACK_QUEEN:
			setWhite(false);
			setId(player);
			break;
		default:
			throw new IllegalArgumentException(player + "is not a player");
		}
	}

	@Override
	public String toString() {
		return "Queen [inChamber=" + inChamber + ", white=" + white + ", " + super.toString() + "]";

	}
}
