package utils;

import static utils.Constant.N;

public class MatrixOperations {
    public static void printMatrix(byte[][] mat) {
        for (byte row = 0; row < N; row++) {
            for (byte col = 0; col < N; col++) {
                System.out.print(mat[row][col] + "\t\t");
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Adds 2 equal sized N x N matrices without modifying their original values
     *
     * @param a the first byte matrix
     * @param b the second byte matrix
     * @return A matrix equal to a+b
     */
    public static byte[][] addMatrix(byte[][] a, byte[][] b) {
        //assume a and b are the same size, N x N
        byte[][] c = new byte[N][N];
        for (byte row = 0; row < N; row++) {
            for (byte col = 0; col < N; col++) {
                c[row][col] = (byte) (a[row][col] + b[row][col]);
            }
        }
        return c;
    }

    /**
     * Subtracts 2 equal sized N x N matrices, i.e. (a - b), without modifying their original values
     *
     * @param a the first byte matrix
     * @param b the second byte matrix
     * @return A matrix equal to a-b
     */
    public static byte[][] subMatrix(byte[][] a, byte[][] b) {
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
    public static int reduceMatrix(byte[][] mat) {
        int total = 0;
        for (byte row = 0; row < N; row++) {
            for (byte col = 0; col < N; col++) {
                total += mat[row][col];
            }
        }
        return total;
    }
}
