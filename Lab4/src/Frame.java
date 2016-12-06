import java.util.ArrayList;

/**
 * Created by rossfreeman on 11/29/16.
 */
class Frame {

    int id;
    Page page;

    static ArrayList<Frame> frames = new ArrayList<>();

    Frame(int id) {
        this.id = id;
    }

    void evictFrame() {
        page.process.residency += page.residency;
        page.process.evictionCount++;
        page.residency = 0;
        page.frame = null;
        page = null;
    }
}
