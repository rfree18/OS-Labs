import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by rossfreeman on 10/3/16.
 */

enum Status {
    RUNNING, BLOCKED, READY, TERMINATED, UNSTARTED
}
class Process implements Comparable<Process> {
    public int arrivalTime; // A
    int initialArrivalTime;
    public int randBound; // B
    public int timeNeeded; // C
    public int ioMultiple; //M
    public Status status;
    int currentCPUTime = 0;
    int currentIOTime = 0;
    int totalCPUTime = 0;
    int totalIOTime = 0;
    int waitTime = 0;

    int pid;
    int rrPid;

    static int rrQuantum = Integer.MIN_VALUE;

    int burstTime = 0;
    int ioBurstTime = 0;

    static ArrayList<Integer> randInts = new ArrayList<>();

    public Process(int a, int b, int c, int m, int pid) {
        arrivalTime = a;
        initialArrivalTime = a;
        randBound = b;
        timeNeeded = c;
        ioMultiple = m;

        this.pid = pid;

        status = Status.UNSTARTED;

    }

    void decrementArrival() {
        arrivalTime -= 1;
        if(arrivalTime < 0) {
            status = Status.READY;
        }
    }

    void incrementCPU() {
        currentCPUTime++;
        totalCPUTime++;

        if(totalCPUTime == timeNeeded) {
            status = Status.TERMINATED;
        }
        else if(rrQuantum > 0 && currentCPUTime % rrQuantum == 0) {
            if(burstTime != 0 && currentCPUTime == burstTime) {
                ioBurstTime = ioMultiple * burstTime;
                burstTime = 0;
                currentCPUTime = 0;
                status = Status.BLOCKED;
            } else
                status = Status.READY;
        }
        else if(burstTime != 0 && currentCPUTime == burstTime) {
            ioBurstTime = ioMultiple * burstTime;
            burstTime = 0;
            currentCPUTime = 0;
            status = Status.BLOCKED;
        }
    }

    void incrementIO() {
        currentIOTime++;
        totalIOTime++;

        if(currentIOTime == ioBurstTime) {
            ioBurstTime = 0;
            currentIOTime = 0;
            status = Status.READY;
        }
    }

    void setToRunning() {
        this.status = Status.RUNNING;
        if(burstTime == 0)
            burstTime = randomOS();
    }

    public int randomOS() {
        if(randInts.size() == 0) {
            loadInts();
        }

        int nextVal = randInts.remove(0);

        return 1 + (nextVal % randBound);
    }

    private void loadInts() {
        try (Scanner randGen = new Scanner(new File("random-numbers.txt"))) {
            while(randGen.hasNext()) {
                randInts.add(randGen.nextInt());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printStatus() {
        System.out.println("\t (A,B,C,M) = (" + initialArrivalTime + "," + randBound + "," + timeNeeded + "," + ioMultiple + ")");
        System.out.println("\t Finishing time: " + (totalIOTime + totalCPUTime + waitTime + initialArrivalTime));
        System.out.println("\t Turnaround time: " + (totalCPUTime + totalIOTime + waitTime));
        System.out.println("\t I/O time: " + totalIOTime);
        System.out.println("\t Waiting time: " + waitTime);
    }

    private String getStatusName() {
        switch(status) {
            case UNSTARTED:
                return "unstarted";
            case READY:
                return "ready";
            case BLOCKED:
                return "blocked";
            case TERMINATED:
                return "terminated";
            default:
                return "running";
        }
    }

    public void printDetailedStatus() {
        int runningStat = 0;
        switch (status) {
            case RUNNING:
                runningStat = burstTime - currentCPUTime < rrQuantum ? burstTime - currentCPUTime : 0;
                if(rrQuantum > 0) {
                    runningStat = rrQuantum - (currentCPUTime % rrQuantum) - runningStat;
                } else
                    runningStat = burstTime - currentCPUTime;
                break;
            case BLOCKED:
                runningStat = ioBurstTime - currentIOTime;
                break;
            case READY:
                break;
        }
        System.out.format("%10s \t", getStatusName());
        System.out.print(runningStat + "\t");
    }

    public int compareTo(Process other) {
       if(arrivalTime < other.arrivalTime) {
           return -1;
       } else if(arrivalTime > other.arrivalTime) {
           return 1;
       } else {
           if(timeRemaining() < other.timeRemaining()) {
               return -1;
           } else if(timeRemaining() > other.timeRemaining()) {
               return 1;
           } else {
               if(pid > other.pid) {
                   return 1;
               } else if(pid < other.pid) {
                   return -1;
               }
               else
                return 0;
           }
       }
    }

    private int timeRemaining() {
        return timeNeeded - totalCPUTime;
    }

    public void reset() {
        currentCPUTime = 0;
        currentIOTime = 0;
        totalCPUTime = 0;
        totalIOTime = 0;
        waitTime = 0;
        burstTime = 0;

        arrivalTime = initialArrivalTime;
        status = Status.UNSTARTED;
    }
}
