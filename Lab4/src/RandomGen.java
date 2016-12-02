import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by rossfreeman on 12/1/16.
 */
public class RandomGen {

    Scanner randGen;

    static RandomGen generator;

    private RandomGen() {
        try {
            randGen = new Scanner(new File("random-numbers.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static int getNextInt() {
        if(generator == null) {
            generator = new RandomGen();
        }

        return generator.randGen.nextInt();
    }

}
