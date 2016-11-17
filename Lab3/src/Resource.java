import java.util.ArrayList;

/**
 * Created by rossfreeman on 11/6/16.
 */
public class Resource {
    int unitsLeft;
    int resourceNum;
    int totalUnits;

    int unitsToProcess;

    static ArrayList<Resource> resources;

    /**
     * Initizlies new resource
     * @param units - total number of units for resource
     */
    private Resource(int units) {
        unitsLeft = units;
        totalUnits = units;
        unitsToProcess = 0;
    }

    /**
     * Adds new units of a resource to a task if possible
     * @param t - the task requesting the addition
     * @param units - the number of units being requested
     * @return - true if the request can be satisfied, false otherwise
     */
    public boolean addResourceToTask(Task t, int units) {
        // Can't satisfy request if it is greater than remaining resources
        if(units > unitsLeft) {
            t.resourceNeeds.put(this, units);
            return false;
        }

        // Subtract requested units
        unitsLeft -= units;

        int current = 0;

        // Update task's resource list
        if(t.resourceHas.containsKey(this))
            current = t.resourceHas.get(this);
        current += units;
        t.resourceHas.put(this, current);

        return true;
    }

    /**
     * Remove resource from a task if possible
     * @param t - task requesting release
     * @param units - number of units to be released or -1 to release all resources
     * @return true if the request can be satisfied, false otherwise
     */
    public boolean removeResourceFromTask(Task t, int units) {
        int current = t.resourceHas.get(this);
        // Request is invalid, cannot satisfy
        if(current < units && units >= -1) {
            return false;
        }

        // Release all resources
        if(units == -1) {
            unitsToProcess += current;
            current = 0;
        } else {
            current -= units;
            // Don't update resource until processUnits() is called
            unitsToProcess += units;
        }

        t.resourceHas.put(this, current);

        return true;

    }

    /**
     * Processes and adds released resources back into the manager
     * NOTE: this is to prevent released resources from being counted mid-cycle
     */
    public void processUnits() {
        unitsLeft += unitsToProcess;
        unitsToProcess = 0;
    }

    /**
     * Adds a new resource to the resource list
     * @param units - total number of units for resource
     */
    static void addResource(int units) {
        if(resources == null) {
            resources = new ArrayList<>();
        }

        Resource n = new Resource(units);

        resources.add(n);
        // Assign resource number
        n.resourceNum = resources.indexOf(n);
    }

    /**
     * Creates shallow duplicate of resource list to provide a clean copy of resources
     * @return - ArrayList containing new copies of all resources
     */
    static ArrayList<Resource> duplicate() {
        ArrayList<Resource> dup = new ArrayList<>();

        for(Resource r : resources) {
            Resource n = new Resource(r.unitsLeft);
            dup.add(n);
        }

        return dup;
    }
}
