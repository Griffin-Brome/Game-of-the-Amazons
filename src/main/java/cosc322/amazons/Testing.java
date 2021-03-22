package cosc322.amazons;

import decision.logic.Heuristic;
import decision.logic.RoyalChamber;
import models.Queen;

import static utils.MatrixOperations._printMatrix;

public class Testing {
    public static void main(String[] args) {
        testTerritoryHeuristic();
    }

    private static void testTerritoryHeuristic(){
        byte[][] fakeBoard = {
                {0, 0, 3, 1, 0, 0, 1, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {2, 0, 0, 0, 0, 0, 0, 0, 0, 2},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {3, 3, 3, 3, 3, 3, 1, 3, 3, 3},
                {0, 0, 0, 2, 0, 0, 2, 0, 0, 0}
        };

        RoyalChamber rc = new RoyalChamber(true, fakeBoard);
        rc.checkRoyalChambers();
        for(Queen queen: rc.getFreeQueens()) {
            System.out.println(queen);
        }
        for(Queen queen: rc.getChamberedQueens()) {
            System.out.println(queen);
        }

//        long start = System.currentTimeMillis();
//        Heuristic h = new Heuristic(fakeBoard, true);
//        System.out.println(h.territoryHeuristic());
//        System.out.println("Territory Iterative: " + (System.currentTimeMillis() - start));
//
//        start = System.currentTimeMillis();
//        Heuristic h2 = new Heuristic(fakeBoard, true);
//        System.out.println(h2.territoryHeuristicR());
//        System.out.println("Territory Recursive: " + (System.currentTimeMillis() - start));

        _printMatrix(fakeBoard);
    }
}
