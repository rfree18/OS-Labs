import java.util.ArrayList;

/**
 * Created by rossfreeman on 12/3/16.
 */
public class Page {
    int min;
    int max;
    int id;

    Process process;

    Frame frame;

    int residency = 0;

    public Page(int min, int max, int id, Process p) {
        this.min = min;
        this.max = max;
        this.id = id;
        process = p;
    }
}
