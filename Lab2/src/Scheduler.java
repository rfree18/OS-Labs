import java.util.ArrayList;

/**
 * Created by rossfreeman on 10/5/16.
 */
public abstract class Scheduler {
    static ArrayList<Process> unsortedProcesses = new ArrayList<Process>();
    ArrayList<Process> sortedProcesses;
    ArrayList<Process> readyProccesses = new ArrayList<>();

    Process currentProcess = null;

    int totalRunningTime = 0;
    int roundNum = 0;
    int ioTime = 0;

    static boolean isVerbose;

    abstract void performOperations();
    abstract void printType();


    static void addProcess(Process p) {
        unsortedProcesses.add(p);
    }

    static void startSchedulers(boolean isVerbose) {
        Scheduler.isVerbose = isVerbose;

        FCFS fcfs = new FCFS();
        fcfs.run();

        RR rr = new RR();
        rr.run();
        Process.rrQuantum = Integer.MIN_VALUE;

        Uni uni = new Uni();
        uni.run();

        SJF sjf = new SJF();
        sjf.run();
    }

    final void run() {
        System.out.print("The original input was:\t" + unsortedProcesses.size());
        for(Process process : unsortedProcesses) {
            System.out.print(" (" + process.initialArrivalTime + ", " + process.randBound
                    + ", " + process.timeNeeded + ", " + process.ioMultiple + ")");
        }

        System.out.print("\nThe (sorted) input is:\t" + sortedProcesses.size());
        for(Process process : sortedProcesses) {
            System.out.print(" (" + process.initialArrivalTime + ", " + process.randBound
                    + ", " + process.timeNeeded + ", " + process.ioMultiple + ")" + process.pid);
        }
        System.out.println("\n");

        while(hasMoreProcesses()) {
            processRound();
        }

        System.out.println();
        printType();
        System.out.println();

        for(int i = 0; i < sortedProcesses.size(); i++) {
            System.out.println("Process " + i + ": ");
            sortedProcesses.get(i).printStatus();
            System.out.println();
        }

        printSummary();

        Process.randInts.clear();

        for(Process process : unsortedProcesses) {
            process.reset();
        }
    }

    final void processRound() {
        for(Process process : sortedProcesses) {
            if(process.status == Status.READY) {
                process.waitTime++;
            }
        }

        if(isVerbose)
            printDetails();

        performOperations();

        totalRunningTime++;
        roundNum++;
    }

    final boolean hasMoreProcesses() {
        for(Process process : sortedProcesses) {
            if(process.status != Status.TERMINATED)
                return true;
        }

        return false;
    }

    final float getCPUUtil() {
        float sum = 0;
        for(Process process : sortedProcesses) {
            sum += process.totalCPUTime;
        }

        return sum / totalRunningTime;
    }

    final float getIOUtil() {
        return (float)ioTime / totalRunningTime;
    }

    final float getThroughput() {
        float avg =  ((float)sortedProcesses.size() / totalRunningTime);
        return avg * (float)100.0;
    }

    final float getTurnaround() {
        float sum = 0;
        for(Process process : sortedProcesses) {
            sum += process.totalIOTime + process.totalCPUTime + process.waitTime;
        }

        return sum / sortedProcesses.size();
    }

    final float getAverageWait() {
        float sum = 0;
        for(Process process : sortedProcesses) {
            sum += process.waitTime;
        }

        return sum / sortedProcesses.size();
    }

    final void printSummary() {
        totalRunningTime--;
        System.out.println("Summary Data:");
        System.out.println("\t Finishing time: " + totalRunningTime);
        System.out.println("\t CPU Utilization: " + getCPUUtil());
        System.out.println("\t I/O Utilization: " + getIOUtil());
        System.out.println("\t Throughput: " + getThroughput() + " processes per hundred cycles");
        System.out.println("\t Average turnaround time: " + getTurnaround());
        System.out.println("\t Average waiting time: " + getAverageWait());
        System.out.println();
    }

    final void printDetails() {
        System.out.print("Before cycle\t" + totalRunningTime + ":\t");
        for(Process process : sortedProcesses) {
            process.printDetailedStatus();
        }
        System.out.println();

    }
}
