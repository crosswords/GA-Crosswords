package runClasses;

/**
 * Created by Tomek on 2014-11-25.
 */
public class Main {

    public static void main(String[] args) {
        //Algorithm.generate(new String[]{"20","20"});
        char[][] matrix = GA.Algorithm.generate(new String[]{"20", "20"});
        for (int y = 0; y < 20; y++) {
            for (int x = 0; x < 20; x++) {
                if (matrix[x][y] == '\u0000') System.out.print(' ');
                else
                    System.out.print(matrix[x][y]);
            }
            System.out.println();
        }
    }
}
