package GA;

import java.lang.Integer;

public class Algorithm{

    public static char[][] generate(String[] args) {
        //int width = Integer.getInteger(args[0]);
        //int height = Integer.getInteger(args[1]);
        //GA ga = new GA(width, height);
        GA ga = new GA(20, 20);
        return ga.runGA();
    }
}