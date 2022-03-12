import java.util.Scanner;

public class Main {

    public static void partiallyPrintMatrix(int[][] matrix, int size) {
        int val = 6;
        if (size < val) val = size;

        System.out.println("\n\nMatrix (partially):");

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

    public static void initializeMatrixes(int[][] m1, int[][] m2, int[][] res, int n) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                m1[i][j] = 1;
                m2[i][j] = i + 1;
                res[i][j] = 0;
            }
        }
    }

    // Normal Multiplication
    public static void OnMult(int[][] m1, int[][] m2, int[][] res, int n) {

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    res[i][j] += m1[i][k] * m2[k][j];
                }
            }
        }

    }

    // Line Multiplication
    public static void OnMultLine(int[][] m1, int[][] m2, int[][] res, int n) {

        for (int i = 0; i < n; i++) {
            for (int k = 0; k < n; k++) {
                for (int j = 0; j < n; j++) {
                    res[i][j] += m1[i][k] * m2[k][j];
                }
            }
        }

    }

    // Block Multiplication
    public static void OnMultBlock(int[][] m1, int[][] m2, int[][] res, int n, int blockSize) {

        if( n % blockSize != 0 || n % blockSize != 0){
            System.out.println("Error");

        }    
        
        
        for(int i0 = 0; i0 < n; i0 += blockSize ){
            for(int i1 = 0; i1 < n; i1 += blockSize){
                for(int i2 = 0; i2 < n; i2 += blockSize){
                    for(int i = i0; i < i0 + blockSize; i++){
                        for(int j = i1; j < i1 + blockSize; j++){
                            for(int k = i2; k < i2 + blockSize; k++){
                                res[i][j] += m1[i][k] * m2[k][j]; 
                            }
                        }
                    }
                }
            }
        }    
       
    }

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);
        while (true) {
            long start = 0, stop = 0;
            int option = 0;

            // User input
            while (true) {
                System.out.println("\n\nChoose method:\n1. Multiplication\n2. Line Multiplication\n3. Block Multiplication\n4. Exit");
                option = input.nextInt();
                if (option == 1 || option == 2 || option == 3 || option == 4) break;
                System.out.println("\nInvalid option (choose between 1 2 or 3)");
            }

            if (option == 4) break;

            System.out.print("Matrix dimensions:");
            int size = input.nextInt();


            // Matrix initialization
            int[][] m1 = new int[size][size];
            int[][] m2 = new int[size][size];
            int[][] res = new int[size][size];
            Main.initializeMatrixes(m1, m2, res, size);

            // Calculations
            switch (option) {
                case 1: {
                    start = System.nanoTime();
                    Main.OnMult(m1, m2, res, size);
                    stop = System.nanoTime();
                    break;
                }
                case 2: {
                    start = System.nanoTime();
                    Main.OnMultLine(m1, m2, res, size);
                    stop = System.nanoTime();
                    break;
                }
                case 3: {
                    System.out.print("\nBlock size:");
                    int noBlocks = input.nextInt();
                    start = System.nanoTime();
                    Main.OnMultBlock(m1, m2, res, size, noBlocks);
                    stop = System.nanoTime();
                    break;
                }
                default: {
                    System.out.println("\nInternal error: Invalid option");
                    return;
                }
            }

            // Results
            Main.partiallyPrintMatrix(res, size);

            float time = (float)(stop - start) / 1000000;
            System.out.println("Time:" + (time) + "seconds");
            
        }
        input.close();
    }
}