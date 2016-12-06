import java.util.ArrayList;

/**
 * Created by rossfreeman on 11/28/16.
 */
class Process {
    private double a;
    private double b;
    private double c;
    int numReferences;
    int residency;
    int pid;
    private int addr;
    int faults = 0;

    int evictionCount = 0;

    ArrayList<Page> pages;

    static ArrayList<Process> processes = new ArrayList<>();
    static int size = 0;

    Process(int pid, double a, double b, double c) {
        this.pid = pid;
        this.a = a;
        this.b = b;
        this.c = c;
        residency = 0;

        pages = new ArrayList<>();

        numReferences = 0;
        addr = (111 * pid) % size;
    }

    void getNextWord() {
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
    }

    Page getPage() {
        for(Page p : pages) {
            if(addr >= p.min && addr <= p.max) {
                return p;
            }
        }

        return null;
    }

    static void increaseResidencies() {
        for(Process process : processes) {
            for(Page page : process.pages) {
                if(page.frame != null) {
                    page.residency++;
                }
            }
        }
    }

    private int mod(int a, int b) {
        return (a+b)%b;
    }
}
