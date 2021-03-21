package models;

public class TerritoryState {
    byte dir;
    byte moveCount;
    byte[] currPos;

    public TerritoryState(byte dir, byte moveCount, byte[] currPos) {
        this.dir = dir;
        this.moveCount = moveCount;
        this.currPos = currPos;
    }

    public byte getDir() {
        return dir;
    }

    public byte getMoveCount() {
        return moveCount;
    }

    public byte[] getCurrPos() {
        return currPos;
    }
}
