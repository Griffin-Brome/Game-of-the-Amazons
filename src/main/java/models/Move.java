package models;

public class Move {
    private byte[] oldPos;
    private byte[] newPos;
    private byte[] arrowPos;

    public Move(byte[] oldPos) {
        this.oldPos = oldPos;
    }

    public Move(byte[] oldPos, byte[] newPos, byte[] arrowPos) {
        this(oldPos);
        this.newPos = newPos;
        this.arrowPos = arrowPos;
    }

    public Move() {
        this(null, null, null);
    }

    public void setOldPos(byte[] oldPos) {
        this.oldPos = oldPos;
    }

    public void setNewPos(byte[] newPos) {
        this.newPos = newPos;
    }

    public void setArrowPos(byte[] arrowPos) {
        this.arrowPos = arrowPos;
    }

    public byte[] getOldPos() {
        return oldPos;
    }

    public byte[] getNewPos() {
        return newPos;
    }

    public byte[] getArrowPos() {
        return arrowPos;
    }
}
