import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by rossfreeman on 10/4/16.
 */



public class FCFS extends Scheduler {

    public FCFS() {
        sortedProcesses = new ArrayList<Process>();

        for(Process process : unsortedProcesses) {
            sortedProcesses.add(process);
        }
        Collections.sort(sortedProcesses);

        for(int i = 0; i < sortedProcesses.size(); i++) {
            Process process = sortedProcesses.get(i);
            process.rrPid = i;
        }
    }

    void performOperations() {
        processBlocked();
        processArriving();
        processRunning();
        processReady();
    }

    void processArriving() {
        for(Process process : sortedProcesses) {
            if(process.status == Status.UNSTARTED) {
                process.decrementArrival();
                if(process.status == Status.READY) {
                    readyProccesses.add(process);
                }
            }
        }
    }

    void processBlocked() {
        boolean didIncrement = false;
        for(Process process : sortedProcesses) {
            if(process.status == Status.BLOCKED) {
                process.incrementIO();

                didIncrement = true;

                if(process.status == Status.READY) {
                    readyProccesses.add(process);
                }
            }
        }

        if(didIncrement) {
            ioTime++;
        }
    }

    void processRunning() {
        for(Process process : sortedProcesses) {
            if(process.status == Status.RUNNING) {
                process.incrementCPU();

                // Check if status changed
                if(process.status != Status.RUNNING) {
                    currentProcess = null;
                }
            }
        }
    }

    void processReady() {
        if (readyProccesses.size() > 0) {
            Process process = readyProccesses.get(0);
            if (currentProcess == null) {
                process.setToRunning();
                currentProcess = process;
                readyProccesses.remove(0);
            }
        }
    }

    void printType() {
        System.out.println("The scheduling algorithm used was First Come First Served");
    }

}
