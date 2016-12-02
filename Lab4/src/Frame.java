import java.util.ArrayList;

/**
 * Created by rossfreeman on 11/29/16.
 */
public class Frame {

    int id;
    int size;
    Process p;
    int min;
    int max;

    static ArrayList<Frame> frames = new ArrayList<>();

    public Frame(int id, int size) {
        this.id = id;
        this.size = size;
    }

    public void evictFrame() {
        this.p = null;
        this.min = 0;
        this.max = 0;
    }
}
