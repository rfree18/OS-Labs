import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by rossfreeman on 10/3/16.
 */

public class Lab2 {
    private static boolean isVerbose = false;

    public static void main(String [] args) {
        int argCount = 0;

        if (args[0].equals("--verbose")) {
            isVerbose = true;
            argCount++;
        }

        try (Scanner processScan = new Scanner(new File(args[argCount]))) {
            try (Scanner randGen = new Scanner(new File("random-numbers.txt"))) {
                int processCount = processScan.nextInt();;

                for(int i = 0; i < processCount; i++) {
                    String val = processScan.next().substring(1);
                    int arrival = Integer.parseInt(val);
                    int randBound = processScan.nextInt();
                    int computeTime = processScan.nextInt();
                    int ioMultiple = Character.getNumericValue(processScan.next().charAt(0));

                    Scheduler.addProcess(new Process(arrival, randBound, computeTime, ioMultiple, i));
                }

                Scheduler.startSchedulers(isVerbose);
            } catch(IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
