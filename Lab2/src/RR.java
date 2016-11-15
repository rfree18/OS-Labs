import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by rossfreeman on 10/6/16.
 */
public class RR extends FCFS {

    ArrayList<Process> tempList = new ArrayList<>();

    public RR() {
        Process.rrQuantum = 2;
    }

    void performOperations() {
        processArriving();
        processBlocked();
        processRunning();
        processReadyList();
        processReady();
    }

    void processBlocked() {
        boolean didIncrement = false;
        for(Process process : sortedProcesses) {
            if(process.status == Status.BLOCKED) {
                process.incrementIO();

                didIncrement = true;

                if(process.status == Status.READY) {
                    tempList.add(process);
                }
            }
        }

        if(didIncrement) {
            ioTime++;
        }
    }

    void processArriving() {
        for(Process process : sortedProcesses) {
            if(process.status == Status.UNSTARTED) {
                process.decrementArrival();
                if(process.status == Status.READY) {
                    tempList.add(process);
                }
            }
        }
    }

    void processRunning() {
        for(Process process : sortedProcesses) {
            if(process.status == Status.RUNNING) {
                process.incrementCPU();

                // Check if status changed
                if(process.status != Status.RUNNING) {
                    currentProcess = null;
                    if(process.status == Status.READY) {
                        tempList.add(process);
                    }
                }
            }
        }
    }

    void processReadyList() {
        Collections.sort(tempList, new Comparator<Process>() {
            @Override
            public int compare(Process p1, Process p2) {
                return Integer.compare(p1.rrPid, p2.rrPid);
            }
        });
        readyProccesses.addAll(tempList);
        tempList.clear();
    }

    void printType() {
        System.out.println("The scheduling algorithm used was Round Robin");
    }
}
