package models;

import java.util.ArrayList;
import java.util.Arrays;

public class Piece {
    private byte[] position;
    private byte id;

    public Piece(byte[] pos) {
        setPosition(pos);
    }

    public Piece(byte x, byte y) {
        setPosition(new byte[]{x, y});
    }

    /**
     * Set the position of this queen, note that this method DOES NOT check if the
     * position is valid, this is the responsibility of the caller method
     *
     * @param position x,y coordinates of the queen
     */
    public void setPosition(byte[] position) {
        this.position = position;
    }

    public void setPosition(ArrayList<Integer> position) {
        // WARNING: hardcoded as length = 2
        byte[] newPos = new byte[2];
        newPos[0] = position.get(0).byteValue();
        newPos[1] = position.get(1).byteValue();
        setPosition(newPos);
    }

    /**
     * @return x, y coordinates of the queen on the board
     */
    public byte[] getPosition() {
        return position;
    }

    public void setId(byte id) {
        this.id = id;
    }

    public byte getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Piece [position=" + Arrays.toString(position) + ", id=" + id + "]";
    }

}
