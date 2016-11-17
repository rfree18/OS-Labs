import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rossfreeman on 11/14/16.
 */
public class Banker extends FIFO {

    /**
     * Initializes a banker object and its necessary variables
     */
    public Banker() {
        tasks = new ArrayList<>();
        processQueue = new ArrayList<>();

        // Duplicate tasks and resources to maintain clean copies
        for (Task task : Task.tasks) {
            tasks.add(task.duplicate());
        }

        resources = Resource.duplicate();
    }

    /**
     * Continuously runs until all processes are terminated or aborted
     */
    public void run() {
        while (hasTasks()) {
            // Add tasks to the queue if they are not already there
            for (Task task : tasks) {
                if (!processQueue.contains(task) && task.status != Status.ABORTED && task.status != Status.TERMINATED) {
                    processQueue.add(task);
                }
            }

            for (Task task : processQueue) {
                if (task.status == Status.RUNNING) {
                    Activity act = task.activities.get(task.cycleNum);

                    if (act.type == ActivityType.REQUEST) {
                        Resource resource = resources.get(act.resourceNum - 1);
                        // Get task's claim for specified resource
                        int claim = task.resourceClaim.get(resource);

                        if (task.resourceHas.containsKey(resource)) {
                            // Adjust claim for resources task already has
                            claim -= task.resourceHas.get(resource);
                        }

                        // Check if request results in a safe state
                        if (!isSafe(task, resource, act.unitSize)) {
                            task.isBlocked = true;
                            task.blockCount++;
                            task.activities.add(task.cycleNum + 1, act);
                        } else if (act.unitSize > claim) {
                            // Abort task if its request is greater than its claim
                            abortTask(task);
                        } else {
                            // Safely add resource to task
                            resource.addResourceToTask(task, act.unitSize);
                            task.isBlocked = false;
                        }
                        task.cycleNum++;
                    } else if (act.type == ActivityType.RELEASE) {
                        Resource resource = resources.get(act.resourceNum - 1);
                        resource.removeResourceFromTask(task, act.unitSize);
                        task.cycleNum++;
                    } else if (act.type == ActivityType.TERMINATE) {
                        task.status = Status.TERMINATED;
                        task.cycleNum++;
                    } else if (act.type == ActivityType.INITIATE) {
                        Resource r = resources.get(act.resourceNum - 1);
                        // If claim is more than number of resources, then abort
                        if (act.unitSize > r.totalUnits) {
                            abortTask(task);
                        } else {
                            // Add claim to task
                            task.resourceClaim.put(r, act.unitSize);
                            task.cycleNum++;
                        }
                    } else if (act.type == ActivityType.COMPUTE) {
                        if (task.computeTime == 0) {
                            task.computeTime = act.resourceNum;
                        }
                        task.computeTime--;

                        if (task.computeTime != 0) {
                            task.activities.add(task.cycleNum + 1, act);
                        }

                        task.cycleNum++;
                    }
                }
            }

            processResources();

            ArrayList<Task> toRemove = new ArrayList<>();

            for (Task t : processQueue) {
                if (!t.isBlocked || t.status != Status.RUNNING)
                    toRemove.add(t);
            }

            processQueue.removeAll(toRemove);
        }

        printDetails();
    }

    /**
     * Checks if a given request results in a safe state
     * @param t the task making the request
     * @param r the resource being requested
     * @param units the number of units being requested
     * @return true if the state is safe, false otherwise
     */
    public boolean isSafe(Task t, Resource r, int units) {
        boolean [] didComplete = new boolean[tasks.size()];
        HashMap<Resource, Integer> simResources = new HashMap<>();

        // Add resources to hashmap to simulate resources without affecting the actual values
        for(Resource resource : resources) {
            if(resource == r) {
                // Remove requested amount from resource
                simResources.put(resource, resource.unitsLeft - units);
            } else {
                simResources.put(resource, resource.unitsLeft);
            }
        }

        // Initialize array to mark which tasks are currently completed (aborted/terminated)
        for(int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if(task.status == Status.ABORTED || task.status == Status.TERMINATED) {
                didComplete[i] = true;
            }
        }

        // Loop through tasks to simulate this scenario
        for(int i = 0; i < tasks.size(); i++) {
            boolean isSafe = true;
            Task task = tasks.get(i);

            for(Resource resource : resources) {
                // This should almost always be true
                if(task.resourceClaim.containsKey(resource)) {
                    int resourcesNeeded = task.resourceClaim.get(resource);
                    if(task.resourceHas.containsKey(resource)) {
                        // Take into account resources the task already has
                        resourcesNeeded -= task.resourceHas.get(resource);
                    }
                    if(task == t && resource == r) {
                        // Process the request through the simulation
                        resourcesNeeded -= units;
                    }
                    // If task needs more resources than it has, then it is blocked
                    if (resourcesNeeded > simResources.get(resource)) {
                        isSafe = false;
                        // No need to check further since it cannot possibly terminate at this point
                        break;
                    }
                }
            }

            // If a task completes, simulate its termination and return resources to manager
            if(!didComplete[i] && isSafe) {
                for(Resource resource : resources) {
                    int current = simResources.get(resource);

                    int toAdd = 0;
                    if(task.resourceHas.containsKey(resource)) {
                        // Add number of units for specified resource to be returned
                        toAdd += task.resourceHas.get(resource);
                    }

                    if(task == t && resource == r) {
                        // Add additional units if it is the task requested the specified resource
                        toAdd += units;
                    }

                    current += toAdd;

                    simResources.replace(resource, current);
                }

                didComplete[i] = true;
                // Need to start cycle over to take new resources into account
                i = -1;
            }
        }

        for(int i = 0; i < didComplete.length; i++) {
            // If a process could not complete, the state is unsafe
            if(!didComplete[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Properly aborts a specified task and returns its resources to the manager
     * @param t the task to be aborted
     */
    public void abortTask(Task t) {
        t.status = Status.ABORTED;

        // Return its resources to the manager
        for (Resource r : t.resourceHas.keySet()) {
            r.removeResourceFromTask(t, -1);
        }

        // Process returned resources
        processResources();

    }

    /**
     * Prints the details of the resource manager's run
     */
    public void printDetails() {
        int cycleSum = 0;
        int waitSum = 0;

        System.out.println("\t\tBanker's");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.print("Task " + (i + 1) + "\t\t");
            Task t = tasks.get(i);
            t.printDetails();

            if (t.status != Status.ABORTED) {
                cycleSum += tasks.get(i).cycleNum;
                waitSum += t.blockCount;
            }
        }
        double pct = (double) waitSum / cycleSum * 100;
        System.out.println("total \t\t" + cycleSum + "\t" + waitSum + "\t " + pct + "%");
    }
}
