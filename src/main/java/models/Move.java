package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Move implements Comparable<Move> {
    private byte[] oldPos;
    private byte[] newPos;
    private byte[] arrowPos;
    private int orderingValue;
    private int score;
    private boolean hasChildren;
    static byte[] y = {10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
    static char[] x = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'};
    private ArrayList<Move> childMoves;

    public Move(byte[] oldPos) {
        this.oldPos = oldPos;
//        this.score = Integer.MIN_VALUE;
        childMoves = new ArrayList<>();
        this.hasChildren = false;
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

    public int getScore() { return score; }

    public boolean hasChildren() {
        return hasChildren;
    }

    public void setScore(int score) {
        this.score = score;
        setOrderingValue(score);
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public int getOrderingValue() {
        return orderingValue;
    }

    public void addChildMove(Move child) {
        this.childMoves.add(child);
    }

    public void sortChildMoves() {
        Collections.sort(this.childMoves);
    }

    public void addAllChildMove(ArrayList<Move> childList) {
        this.childMoves.addAll(childList);
    }

    public ArrayList<Move> getChildMoves() {
        return childMoves;
    }

    @Override
    public int compareTo(Move m) {
        return m.orderingValue - this.orderingValue;
    }

    @Override
    public String toString() {
        return "Move " +
                _printPosition(oldPos) + " -> " + _printPosition(newPos) +
                " => " + _printPosition(arrowPos) +
                ", orderingValue=" + orderingValue +
                "\t|\tExquisite Move 🧐🔥";
    }

    public static String _printPosition(byte[] pos) {
        return "" + x[pos[1]] + y[pos[0]];
    }
}
