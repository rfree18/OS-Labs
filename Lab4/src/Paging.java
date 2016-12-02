import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by rossfreeman on 11/21/16.
 */
public class Paging {
    static int numReferences;
    static int q = 3;

    public static void main(String [] args) {
        if(args.length < 6) {
            System.out.println("Error: Incorrect number of arguments");
            return;
        }
        int machineSize = Integer.parseInt(args[0]);
        int pageSize = Integer.parseInt(args[1]);
        int s = Integer.parseInt(args[2]);
        int j = Integer.parseInt(args[3]);
        numReferences = Integer.parseInt(args[4]);
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

        int numFrames = machineSize / pageSize;

        for(int i = 0; i < numFrames; i++) {
            Frame.frames.add(new Frame(i, pageSize));
        }

        lifo();
    }

    public static void lifo() {
        ArrayList<Frame> stack = new ArrayList<>();

        for(int i = Frame.frames.size() -1; i >= 0; i--) {
            stack.add(Frame.frames.get(i));
        }

        while(!isComplete()) {
            for(Process process : Process.processes) {
                int nextAddr = process.addr;
                for(int i = 0; i < q; i++) {
                    if(process.frame != null) {
                        if(process.frame.max >= nextAddr && process.frame.min <= nextAddr) {
                            process.numReferences++;
                        }
                        else {
                            stack.add(0, process.removeFrame());
                            process.faults++;
                        }
                    }
                    else {
                        Frame f = stack.get(0);
                        process.assignToFrame(f);
                        process.faults++;
                    }

                    nextAddr = process.getNextWord();
                }
            }
        }
    }

    public static boolean isComplete() {
        for(Process p : Process.processes) {
            if(p.numReferences < numReferences)
                return false;
        }

        return true;
    }
}
