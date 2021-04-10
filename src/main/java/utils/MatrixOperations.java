package utils;

import java.util.ArrayList;

import static utils.Constant.N;

public class MatrixOperations {
    public static void _printMatrix(byte[][] mat) {
        for (byte[] bytes : mat) {
            for (byte col = 0; col < mat[0].length; col++) {
                System.out.print(bytes[col] + "\t\t");
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Adds any number of equal sized N x N matrices, returning the total matrix, without modifying any original values
     * [computes a cell-by-cell addition]
     *
     * @param matrices a list of all matrices to sum
     * @return A matrix equal to sum(all matrices)
     */
    public static byte[][] _addMatrix(byte[][]... matrices) {
        //assume a and b are the same size, N x N
        byte[][] out = new byte[N][N];
        for (byte row = 0; row < N; row++) {
            for (byte col = 0; col < N; col++) {
                for(byte[][] matrix: matrices){
                    out[row][col] += matrix[row][col];
                }
            }
        }
        return out;
    }

    /**
     * Subtracts 2 equal sized N x N matrices, i.e. (a - b), without modifying their original values
     *
     * @param a the first byte matrix
     * @param b the second byte matrix
     * @return A matrix equal to a-b
     */
    public static byte[][] _subMatrix(byte[][] a, byte[][] b) {
        //assume a and b are the same size, N x N
        byte[][] c = new byte[N][N];
        for (byte row = 0; row < N; row++) {
            for (byte col = 0; col < N; col++) {
                c[row][col] = (byte) (a[row][col] - b[row][col]);
            }
        }
        return c;
    }

    /**
     * Adds all entries in a matrix and returns a single value totalling them
     *
     * @param mat the matrix to reduce
     * @return An integer representing the sum of all elements in the matrix
     */
    public static int _reduceMatrix(byte[][] mat) {
        int total = 0;
        for (byte row = 0; row < N; row++) {
            for (byte col = 0; col < N; col++) {
                total += mat[row][col];
            }
        }
        return total;
    }

    public static byte[][] _cloneMatrix(byte[][] mat) {
        // just so the new matrix is the same size as the old one in all cases
        byte[][] newMatrix = new byte[mat.length][mat[0].length];

        for (byte row = 0; row < mat.length; row++) {
            System.arraycopy(mat[row], 0, newMatrix[row], 0, mat[0].length);
        }

        return newMatrix;
    }

    public static byte[][] _makeMatrix(ArrayList<Integer> state) {
        // just so the new matrix is the same size as the old one in all cases
        byte[][] newMatrix = new byte[11][11];
        int i = 0;

        for (byte row = 0; row < 11; row++) {
            for (byte col = 0; col < 11; col++) {
                int x = state.get(i);
                newMatrix[row][col] = (byte) x;
                i++;
            }
        }

        return newMatrix;
    }

    /**
     * Initializes every cell in a matrix to equal a provided value. Will overwrite every cell in the matrix to make them = to 'value'
     * @param matrix the matrix
     * @param value the value to initialize the matrix with
     */
    public static void _initializeMatrix(byte[][] matrix, byte value) {
        for (byte row = 0; row < N; row++) {
            for (byte col = 0; col < N; col++) {
                matrix[row][col] = value;
            }
        }
    }
}
