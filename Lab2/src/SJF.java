import java.util.Collections;

/**
 * Created by rossfreeman on 10/6/16.
 */
public class SJF extends FCFS {

    public SJF() {
    }

    void processReady() {
        Collections.sort(readyProccesses);
        super.processReady();
    }

    void printType() {
        System.out.println("The scheduling algorithm used was Shortest Job First");
    }
}
