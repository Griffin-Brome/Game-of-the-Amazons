package models;

import java.util.Arrays;

public class Move implements Comparable<Move> {
    private byte[] oldPos;
    private byte[] newPos;
    private byte[] arrowPos;
    private int orderingValue;

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

    public void setOrderingValue(int orderingValue) {
        this.orderingValue = orderingValue;
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

    public int setOrderingValue() {
        return orderingValue;
    }

    @Override
    public int compareTo(Move m) {
        return m.orderingValue - this.orderingValue;
    }

    @Override
    public String toString() {
        return "Move{" +
                "oldPos=" + Arrays.toString(oldPos) +
                ", newPos=" + Arrays.toString(newPos) +
                ", arrowPos=" + Arrays.toString(arrowPos) +
                ", orderingValue=" + orderingValue +
                '}';
    }
}
