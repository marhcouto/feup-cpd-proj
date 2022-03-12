import java.util.Scanner;

public class Main {

    public static void partiallyPrintMatrix(int[][] matrix, int size) {
        int val = 6;
        if (size < val) val = size;

        for (int i = 0; i < val; i++) {
            System.out.print('|');
            for (int j = 0; j < val; j++) {
                System.out.print(' ');
                System.out.print(matrix[i][j]);
                System.out.print(' ');
            }
            System.out.println('|');
        }
    }

    // Normal Multiplication
    public static int[][] OnMult(int n) {

        int[][] matrix1 = new int[n][n];
        int[][] matrix2 = new int[n][n];
        int[][] res = new int[n][n];


        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrix1[i][j] = 1;
                matrix2[i][j] = i;
                res[i][j] = 0;
            }
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    res[i][j] += matrix1[i][k] * matrix2[k][j];
                }
            }
        }

        return res;
    }


    // Line Multiplication
    public static int[][] OnMultLine(int n) {

        int[][] matrix1 = new int[n][n];
        int[][] matrix2 = new int[n][n];
        int[][] res = new int[n][n];


        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrix1[i][j] = 1;
                matrix2[i][j] = i;
                res[i][j] = 0;
            }
        }

        for (int i = 0; i < n; i++) {
            for (int k = 0; k < n; k++) {
                for (int j = 0; j < n; j++) {
                    res[i][j] += matrix1[i][k] * matrix2[k][j];
                }
            }
        }

        return res;
    }

    public static int[][] OnMultBlock(int mA, int blockSize){
        int[][] matrix1 = new int[mA][mA];
        int[][] matrix2 = new int[mA][mA];
        int[][] res = new int[mA][mA];

        if( mA % blockSize != 0 || mA % blockSize != 0){
            System.out.println("Error");

        }    

        for (int i = 0; i < mA; i++) {
            for (int j = 0; j < mA; j++) {
                matrix1[i][j] = 1;
                matrix2[i][j] = i;
                res[i][j] = 0;
            }
        }
        
        
        for(int i0 = 0; i0 < mA; i0 += blockSize ){
            for(int i1 = 0; i1 < mA; i1 += blockSize){
                for(int i2 = 0; i2 < mA; i2 += blockSize){
                    for(int i = i0; i < i0 + blockSize; i++){
                        for(int j = i1; j < i1 + blockSize; j++){
                            for(int k = i2; k < i2 + blockSize; k++){
                                res[i][j] += matrix1[i][k] * matrix2[k][j]; 
                            }
                        }
                    }
                }
            }
        }    
        
        return res;
        
    }

    public static void main(String[] args) {
        
        Scanner input = new Scanner(System.in);
        long start = 0, stop = 0;
        int[][] res;
        int option = 0;
        while (true) {
            System.out.println("Choose method:\n1. Multiplication\n2. Line Multiplication\n3. Block Multiplication");
            option = input.nextInt();
            if (option == 1 || option == 2 || option == 3) break;
            System.out.println("Invalid option (choose between 1 2 or 3)");
        }
        System.out.println("Matrix dimensions:");
        int size = input.nextInt();


        switch (option) {
            case 1: {
                start = System.nanoTime();
                res = Main.OnMult(size);
                stop = System.nanoTime();
                Main.partiallyPrintMatrix(res, size);
                break;
            }
            case 2: {
                start = System.nanoTime();
                res = Main.OnMultLine(size);
                stop = System.nanoTime();
                Main.partiallyPrintMatrix(res, size);
                break;
            }
            case 3: {
                start = System.nanoTime();
                res = Main.OnMultBlock(size, 2); // TODO: correct
                stop = System.nanoTime();
                Main.partiallyPrintMatrix(res, size);
                break;
            }
            default: {
                System.out.println("Invalid option");
            }
        }

        
        System.out.println("Time elapsed:" + (stop - start));

        
        input.close();
    }
}