import GA.GA;

/**
 * Created by Mariusz on 2014-05-18.
 */
public class Algorithm {

    public static char[][] generate(String[] args) {
        int width = Integer.parseInt(args[0]);
        int height = Integer.parseInt(args[1]);
        GA ga = new GA(width, height);
        return ga.runGA();
    }
}
