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
    int addr;
    Frame frame;
    int faults = 0;

    static ArrayList<Process> processes = new ArrayList<>();
    static int size = 0;

    public Process(int pid, double a, double b, double c) {
        this.pid = pid;
        this.a = a;
        this.b = b;
        this.c = c;

        numReferences = 0;
        addr = (111 * pid) % size;
    }

    public int getNextWord() {
        double r = RandomGen.getNextInt();
        double result = r/(Integer.MAX_VALUE + 1d);

        if(result < a) {
            addr = mod(addr+1, size);
        } else if(result < a+b) {
            addr = mod(addr-5, size);
        } else if(result < a+b+c) {
            addr = mod(addr+4, size);
        } else {
            int offset = RandomGen.getNextInt();
            addr = mod(offset, size);
        }

        return addr;
    }

    public void assignToFrame(Frame f) {
        frame = f;
        f.p = this;
        f.min = addr;
        f.max = addr + size;
    }

    public Frame removeFrame() {
        Frame result = frame;

        frame.evictFrame();
        frame = null;

        return frame;
    }

    public int mod(int a, int b) {
        return (a+b)%b;
    }
}
