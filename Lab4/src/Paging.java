import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by rossfreeman on 11/21/16.
 */
public class Paging {
    public static void main(String [] args) {
        if(args.length < 6) {
            System.out.println("Error: Incorrect number of arguments");
            return;
        }

        try (Scanner randGen = new Scanner(new File("random-numbers.txt"))) {
            int m = Integer.parseInt(args[0]);
            int p = Integer.parseInt(args[1]);
            int s = Integer.parseInt(args[2]);
            int j = Integer.parseInt(args[3]);
            int n = Integer.parseInt(args[4]);
            String r = args[5];

            Process.size = s;

            switch (j) {
                case 1:
                    Process.processes.add(new Process(1, 1, 0, 0));
                    break;
                case 2:
                    for(int i = 0; i < 4; i++) {
                        Process.processes.add(new Process(i+1, 1, 0, 0));
                    }
                    break;
                case 3:
                    for(int i = 0; i < 4; i++) {
                        Process.processes.add(new Process(i+1, 0, 0, 0));
                    }
                    break;
                case 4:
                    Process p1 = new Process(1, .75, .25, 0);
                    Process p2 = new Process(2, .75, 0, .25);
                    Process p3 = new Process(3, .75, .125, .125);
                    Process p4 = new Process(4, .5, .125, .125);

                    Process.processes.add(p1);
                    Process.processes.add(p2);
                    Process.processes.add(p3);
                    Process.processes.add(p4);
                    break;
                default:
                    System.out.println("Error: Invalid job mix value");
                    return;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isComplete(int numReferences) {
        for(Process p : Process.processes) {
            if(p.numReferences < numReferences)
                return false;
        }

        return true;
    }
}
