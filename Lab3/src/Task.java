import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rossfreeman on 11/6/16.
 */

enum Status {
    RUNNING, ABORTED, TERMINATED
}

public class Task {
    static ArrayList<Task> tasks;

    int cycleNum;
    Status status;
    boolean isBlocked;
    int blockCount;
    int computeTime;

    ArrayList<Activity> activities;
    ArrayList<Activity> ogActivities;
    HashMap<Resource, Integer> resourceHas;
    HashMap<Resource, Integer> resourceNeeds;
    HashMap<Resource, Integer> resourceClaim;

    /**
     * Initializes tasks and its variables
     */
    private Task() {;
        cycleNum = 0;
        isBlocked = false;
        blockCount = 0;
        computeTime = 0;

        activities = new ArrayList<>();
        ogActivities = new ArrayList<>();
        resourceHas = new HashMap<>();
        resourceNeeds = new HashMap<>();
        resourceClaim = new HashMap<>();
        status = Status.RUNNING;
    }

    /**
     * Adds new activity to task with given parameters
     * @param a the tyoe if activity
     * @param resourceNum the identifying resource number
     * @param units the number of units of the specifed resource
     */
    void addActivity(String a, int resourceNum, int units) {
        Activity act;

        // Assign appropriate type to activity
        if(a.equals("initiate")) {
            act = new Activity(ActivityType.INITIATE, resourceNum, units);
        } else if(a.equals("request")) {
            act = new Activity(ActivityType.REQUEST, resourceNum, units);
        } else if(a.equals("compute")) {
            act = new Activity(ActivityType.COMPUTE, resourceNum, units);
        } else if(a.equals("release")) {
            act = new Activity(ActivityType.RELEASE, resourceNum, units);
        } else {
            act = new Activity(ActivityType.TERMINATE, resourceNum, units);
        }

        // Need to add to ogActivites to preserve a "clean" copy in case original ArrayList is modified
        activities.add(act);
        ogActivities.add(act);
    }

    /**
     * Provides a shallow duplication of task in new memory address
     * @return new task object with original properties
     */
    Task duplicate() {
        Task t = new Task();
        t.cycleNum = 0;
        t.blockCount = 0;

        // Go through original activity list and add it to the new lists
        for(Activity act : ogActivities) {
            t.activities.add(act);
            t.ogActivities.add(act);
        }

        return t;
    }

    /**
     * Adds task to the class's Task list
     */
    static void addTask() {
        if(tasks == null) {
            tasks = new ArrayList<>();
        }

        tasks.add(new Task());
    }

    /**
     * Prints details of tasks
     */
    public void printDetails() {
        // Don't print details if not terminated properly
        if(status == Status.ABORTED) {
            System.out.println("aborted");
        } else {
            cycleNum--; // Account for 1-off error due to counting during termination action
            double pct = (double)blockCount/cycleNum * 100;
            System.out.println(cycleNum + "\t" + blockCount + "\t" + pct + "%");
        }
    }

}
