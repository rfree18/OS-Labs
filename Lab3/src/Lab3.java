import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.File;

/**
 * Created by rossfreeman on 11/4/16.
 */
public class Lab3 {
    public static void main(String [] args) {
        try {
            Scanner inputScan = new Scanner(new File(args[0]));

            // Create appropriate number of task objects
            int numTasks = inputScan.nextInt();
            for(int i = 0; i < numTasks; i++) {
                Task.addTask();
            }

            // Create resource objects
            int numResources = inputScan.nextInt();
            for(int i = 0; i < numResources; i++) {
                Resource.addResource(inputScan.nextInt());
            }

            // Parse through all input
            while(inputScan.hasNext()) {
                String actType = inputScan.next();
                int taskNum = inputScan.nextInt();
                int resourceNum = inputScan.nextInt();
                int unitSize = inputScan.nextInt();

                Task current = Task.tasks.get(taskNum-1);
                current.addActivity(actType, resourceNum, unitSize);
            }

            FIFO fifo = new FIFO();
            fifo.run();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}