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

    /**
     * Creates a new Activity object with the specified properties
     * @param type - the type of activity
     * @param resourceNum - the identification number of the resource
     * @param unitSize - the units of the resource
     */
    public Activity(ActivityType type, int resourceNum, int unitSize) {
        this.type = type;
        this.resourceNum = resourceNum;
        this.unitSize = unitSize;
    }
}
