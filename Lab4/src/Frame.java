import java.util.ArrayList;

/**
 * Created by rossfreeman on 11/29/16.
 */
public class Frame {

    int id;
    int size;
    Page page;

    int residency = 0;

    static ArrayList<Frame> frames = new ArrayList<>();

    public Frame(int id, int size) {
        this.id = id;
        this.size = size;
    }

    public void evictFrame() {
        page.process.residency += page.residency;
        page.process.evictionCount++;
        page.residency = 0;
        page.frame = null;
        page = null;
    }
}
