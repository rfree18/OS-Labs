import java.util.ArrayList;

/**
 * Created by rossfreeman on 11/21/16.
 */

enum Algo {
    LRU, LIFO, RANDOM;
}
public class Paging {
    static int numReferences;
    static int q = 3;
    static ArrayList<Frame> frames = new ArrayList<>();
    static Algo algoType;

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

        if(r.equals("lru"))
            algoType = Algo.LRU;
        else if(r.equals("lifo"))
            algoType = Algo.LIFO;
        else
            algoType = Algo.RANDOM;

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
                for(int i = 0; i < q; i++) {
                    if(process.numReferences == numReferences) {
                        break;
                    }
                    Page currentPage = process.getPage();
                    if(currentPage.frame == null) {
                        currentPage.frame = evict();
                        currentPage.frame.page = currentPage;
                        process.faults++;
                    }
                    else if(algoType == Algo.LRU) {
                        frames.remove(currentPage.frame);
                        frames.add(currentPage.frame);
                    }
                    Process.increaseResidencies();
                    process.numReferences++;
                    process.getNextWord();
                }
            }
        }
    }

    public static Frame evict() {
        switch (algoType) {
            case LRU:
                return lruEvict();
            case LIFO:
                return lifoEvict();
            default:
                return randomEvict();
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

    public static Frame lifoEvict() {
        Frame next = null;
        for(int i = frames.size() - 1; i >= 0; i--) {
            if(frames.get(i).page == null) {
                next = frames.remove(i);
                break;
            }
        }
        if(next == null) {
            next = frames.remove(frames.size() - 1);
        }
        if(next.page != null) {
            next.evictFrame();
        }

        frames.add(next);

        return next;
    }

    public static Frame randomEvict() {
        Frame next = null;

        for(int i = 0; i < frames.size(); i++) {
            if(frames.get(i).page == null) {
                next = frames.get(i);
                break;
            }
        }
        if(next == null) {
            int rand = RandomGen.getNextInt();
            int numFrames = frames.size();

            int frameId = rand % numFrames;
            for (Frame f : frames) {
                if (f.id == frameId) {
                    next = f;
                }
            }
        }
        if(next.page != null) {
            next.evictFrame();
        }

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
        double residencyCount = 0;
        int processCount = 0;

        for(Process p : Process.processes) {
            totalFaults += p.faults;
            System.out.println("Process " + p.pid + ": ");
            System.out.println("\tFaults: " + p.faults);
            if(p.evictionCount == 0) {
                System.out.println("\tAverage Residency: Undefined");
            }
            else {
                double avgResidency = (double)p.residency / p.evictionCount;
                residencyCount += avgResidency;
                processCount++;
                System.out.println("\tAverage Residency: " + avgResidency);
            }
        }
        System.out.println("Total Faults: " + totalFaults);
        double totalAvgResidency = residencyCount / processCount;
        System.out.println("Total Average Residency: " + totalAvgResidency);
    }
}
