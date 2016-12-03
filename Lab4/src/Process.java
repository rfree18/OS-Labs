import java.util.ArrayList;

/**
 * Created by rossfreeman on 11/28/16.
 */
public class Process {
    double a;
    double b;
    double c;
    int numReferences;
    int residency;
    int pid;
    int addr;
    int faults = 0;

    int evictionCount = 0;

    ArrayList<Page> pages;

    static ArrayList<Process> processes = new ArrayList<>();
    static int size = 0;

    public Process(int pid, double a, double b, double c) {
        this.pid = pid;
        this.a = a;
        this.b = b;
        this.c = c;
        residency = 0;

        pages = new ArrayList<>();

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

    public Page getPage() {
        for(Page p : pages) {
            if(addr >= p.min && addr <= p.max) {
                return p;
            }
        }

        return null;
    }

    public static void increaseResidencies() {
        for(Process process : processes) {
            for(Page page : process.pages) {
                if(page.frame != null) {
                    page.residency++;
                }
            }
        }
    }

    public int mod(int a, int b) {
        return (a+b)%b;
    }
}
