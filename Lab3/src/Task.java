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
    HashMap<Resource, Integer> resourceHas;
    HashMap<Resource, Integer> resourceNeeds;
    HashMap<Resource, Integer> resourceClaim;

    private Task() {;
        cycleNum = 0;
        isBlocked = false;
        blockCount = 0;
        computeTime = 0;

        activities = new ArrayList<>();
        resourceHas = new HashMap<>();
        resourceNeeds = new HashMap<>();
        status = Status.RUNNING;
    }

    void addActivity(String a, int resourceNum, int units) {
        Activity act;

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

        activities.add(act);
    }

    Task duplicate() {
        Task t = new Task();
        t.cycleNum = 0;
        t.blockCount = 0;
        t.activities = activities;

        return t;
    }

    static void addTask() {
        if(tasks == null) {
            tasks = new ArrayList<>();
        }

        tasks.add(new Task());
    }

    public void printDetails() {
        if(status == Status.ABORTED) {
            System.out.println("aborted");
        } else {
            cycleNum--;
            double pct = (double)blockCount/cycleNum * 100;
            System.out.println(cycleNum + "\t" + blockCount + "\t" + pct + "%");
        }
    }

}
