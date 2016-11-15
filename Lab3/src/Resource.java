import java.util.ArrayList;

/**
 * Created by rossfreeman on 11/6/16.
 */
public class Resource {
    int unitsLeft;
    int resourceNum;

    int unitsToProcess;
    int totalClaim;

    static ArrayList<Resource> resources;

    private Resource(int units) {
        unitsLeft = units;
        unitsToProcess = 0;
        totalClaim = 0;
    }

    public boolean addResourceToTask(Task t, int units) {
        if(units > unitsLeft) {
            t.resourceNeeds.put(this, units);
            return false;
        }

        unitsLeft -= units;

        int current = 0;

        if(t.resourceHas.containsKey(this))
            current = t.resourceHas.get(this);
        current += units;
        t.resourceHas.put(this, current);

        return true;
    }

    public boolean removeResourceFromTask(Task t, int units) {
        int current = t.resourceHas.get(this);
        if(current < units && units >= -1) {
            return false;
        }

        if(units == -1) {
            unitsToProcess += current;
            current = 0;
        } else {
            current -= units;
            unitsToProcess += units;
        }

        t.resourceHas.put(this, current);

        return true;

    }

    public void processUnits() {
        unitsLeft += unitsToProcess;
        unitsToProcess = 0;
    }

    static void addResource(int units) {
        if(resources == null) {
            resources = new ArrayList<>();
        }

        Resource n = new Resource(units);

        resources.add(n);
        n.resourceNum = resources.indexOf(n);
    }

    static ArrayList<Resource> duplicate() {
        ArrayList<Resource> dup = new ArrayList<>();

        for(Resource r : resources) {
            Resource n = new Resource(r.unitsLeft);
            dup.add(n);
        }

        return dup;
    }
}
