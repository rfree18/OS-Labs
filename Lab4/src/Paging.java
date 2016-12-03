import java.util.ArrayList;

/**
 * Created by rossfreeman on 11/21/16.
 */
public class Paging {
    static int numReferences;
    static int q = 3;
    static ArrayList<Frame> frames = new ArrayList<>();

    public static void main(String [] args) {
        if(args.length < 6) {
            System.out.println("Error: Incorrect number of arguments");
            return;
        }
        int machineSize = Integer.parseInt(args[0]);
        int pageSize = Integer.parseInt(args[1]);
        Process.size = Integer.parseInt(args[2]);
        int j = Integer.parseInt(args[3]);
        numReferences = Integer.parseInt(args[4]);
        String r = args[5];

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

        int numPages = Process.size / pageSize;
        for(Process p : Process.processes) {
            for(int i = 0; i < numPages; i++) {
                int min = i * pageSize;
                int max = min + pageSize - 1;
                p.pages.add(new Page(min, max, i, p));
            }
        }

        run();
        printDetails();
    }

    public static void run() {
        for(int i = Frame.frames.size() -1; i >= 0; i--) {
            frames.add(Frame.frames.get(i));
        }

        while(!isComplete()) {
            for(Process process : Process.processes) {
                int nextAddr = process.addr;
                for(int i = 0; i < q; i++) {
                    if(process.numReferences == numReferences) {
                        break;
                    }
                    Page currentPage = process.getPage();
                    if(currentPage.frame == null) {
                        currentPage.frame = lruEvict();
                        currentPage.frame.page = currentPage;
                        process.faults++;
                    }
                    else {
                        frames.remove(currentPage.frame);
                        frames.add(currentPage.frame);
                    }
                    Process.increaseResidencies();
                    process.numReferences++;
                    nextAddr = process.getNextWord();
                }
            }
        }
    }

    public static Frame lruEvict() {
        Frame next = frames.remove(0);

        if(next.page != null) {
            next.evictFrame();
        }

        frames.add(next);

        return next;
    }

    public static boolean isComplete() {
        for(Process p : Process.processes) {
            if(p.numReferences < numReferences)
                return false;
        }

        return true;
    }

    public static void printDetails() {
        int totalFaults = 0;

        for(Process p : Process.processes) {
            totalFaults += p.faults;
            System.out.println("Process " + p.pid + ": ");
            System.out.println("\tFaults: " + p.faults);
            if(p.evictionCount == 0) {
                System.out.println("\tAverage Residency: Undefined");
            }
            else {
                double avgResidency = (double)p.residency / p.evictionCount;
                System.out.println("\tAverage Residency: " + avgResidency);
            }
        }
        System.out.println("Total Faults: " + totalFaults);
    }
}
