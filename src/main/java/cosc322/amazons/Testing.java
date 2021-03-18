package cosc322.amazons;

import decision.logic.Heuristic;

import static utils.MatrixOperations._printMatrix;

public class Testing {
    public static void main(String[] args) {
        testTerritoryHeuristic();
    }

    private static void testTerritoryHeuristic(){
        byte[][] fakeBoard = {
                {0, 0, 0, 1, 0, 0, 1, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {2, 0, 0, 0, 0, 0, 0, 0, 0, 2},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 2, 0, 0, 2, 0, 0, 0}
        };
        byte[][] whiteQueens = {{3, 0}, {0, 3}, {0, 6}, {3, 9}};
        byte[][] blackQueens = {{6, 0}, {9, 3}, {9, 6}, {6, 9}};

        Heuristic h = new Heuristic(fakeBoard, true);
        System.out.println(h.mobilityHeuristic());

//        byte[][] fakeBoard = {
//                {1, 0, 0},
//                {0, 0, 0},
//                {0, 0, 2}
//        };
//        byte[][] whiteQueens = {{0, 0}};
//        byte[][] blackQueens = {{2, 2}};
//        Heuristic h = new Heuristic(fakeBoard, whiteQueens, blackQueens);
//        h.territoryHeuristic();
        _printMatrix(fakeBoard);
    }
}
