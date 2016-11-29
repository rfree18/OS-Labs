import java.util.ArrayList;

/**
 * Created by rossfreeman on 11/28/16.
 */
public class Process {
    double a;
    double b;
    double c;
    int numReferences;
    int pid;

    static ArrayList<Process> processes = new ArrayList<>();
    static int size = 0;

    public Process(int pid, double a, double b, double c) {
        this.pid = pid;
        this.a = a;
        this.b = b;
        this.c = c;

        numReferences = 0;
    }
}
