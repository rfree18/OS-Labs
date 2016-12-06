/**
 * Created by rossfreeman on 12/3/16.
 */
class Page {
    int min;
    int max;

    Process process;

    Frame frame;

    int residency = 0;

    Page(int min, int max, Process p) {
        this.min = min;
        this.max = max;
        process = p;
    }
}
