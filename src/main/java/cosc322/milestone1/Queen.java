package cosc322.milestone1;

public class Queen {
    private byte[] position;
    private boolean white;

    /**
     * Returns the player this queen belongs to, since there are only two options, this is a binary value, if it's not
     * white, then it must be black
     *
     * @return whether or not this queen belongs to the white player
     */
    public boolean isWhite() {
        return white;
    }

    /**
     * Set the position of this queen, note that this method DOES NOT check if the position is valid, this is the
     * responsibility of the caller method
     *
     * @param position x,y coordinates of the queen
     */
    public void setPosition(byte[] position) {
        this.position = position;
    }

    /**
     * @return x,y coordinates of the queen on the board
     */
    public byte[] getPosition() {
        return position;
    }

    /**
     * Set the player that this queen belongs to. This should only be called once, during the instantiation of the
     * queen object (i.e. in the constructor)
     *
     * @param player must be either 1 (for white player) or 2 (black player)
     */
    private void setPlayer(byte player) {
        switch (player) {
            case 1:
                this.white = true;
                break;
            case 2:
                this.white = false;
                break;
            default:
                throw new IllegalArgumentException(player + "is not a player");
        }

    }
}
