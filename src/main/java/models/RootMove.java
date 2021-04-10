package models;

import java.util.ArrayList;
import java.util.Collections;

public class RootMove {
    private ArrayList<Move> childMoves;

    public RootMove(){
        childMoves = new ArrayList<>();
    }

    public void addChildMove(Move child) {
        this.childMoves.add(child);
    }

    public void addAllChildMove(ArrayList<Move> childList) {
        this.childMoves.addAll(childList);
    }

    public ArrayList<Move> getChildMoves() {
        sortChildMoves(); // implicitly sort moves before returning them
        return childMoves;
    }

    public void sortChildMoves() {
        Collections.sort(this.childMoves);
    }
}
