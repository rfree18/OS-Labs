import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by rossfreeman on 10/5/16.
 */
public class Uni extends Scheduler {

    public Uni() {
        sortedProcesses = new ArrayList<Process>();

        for(Process process : unsortedProcesses) {
            sortedProcesses.add(process);
        }
        Collections.sort(sortedProcesses);
    }

    void performOperations() {
        processArriving();
        if(processBlocked()) {
            return;
        }
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

    boolean processBlocked() {
        boolean didIncrement = false;
        for(Process process : sortedProcesses) {
            if(process.status == Status.BLOCKED) {
                process.incrementIO();

                didIncrement = true;

                if(process.status == Status.READY) {
                    process.setToRunning();
                }
            }
        }

        if(didIncrement) {
            ioTime++;
        }
        return didIncrement;
    }

    boolean processRunning() {
        for(Process process : sortedProcesses) {
            if(process.status == Status.RUNNING) {
                process.incrementCPU();

                // Check if status changed
                if(process.status == Status.TERMINATED) {
                    currentProcess = null;
                }
                return true;
            }
        }
        return false;
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
        System.out.println("The scheduling algorithm used was Uniprocessing");
    }
}
