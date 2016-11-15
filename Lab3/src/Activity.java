/**
 * Created by rossfreeman on 11/6/16.
 */
enum ActivityType {
    INITIATE, REQUEST, COMPUTE, RELEASE, TERMINATE
}

public class Activity {
    ActivityType type;
    int resourceNum;
    int unitSize;

    public Activity(ActivityType type, int resourceNum, int unitSize) {
        this.type = type;
        this.resourceNum = resourceNum;
        this.unitSize = unitSize;
    }
}
