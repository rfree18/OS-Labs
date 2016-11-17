import java.util.ArrayList;

/**
 * Created by rossfreeman on 11/6/16.
 */
public class FIFO {
    ArrayList<Task> tasks;
    ArrayList<Task> processQueue;
    ArrayList<Resource> resources;

    /**
     * Initializes a FIFO object to run and initializes needed variables
     */
    public FIFO() {
        tasks = new ArrayList<>();
        processQueue = new ArrayList<>();

        // Duplicate tasks and resources so they can be reused later
        for(Task task : Task.tasks) {
            tasks.add(task.duplicate());
        }

        resources = Resource.duplicate();
    }

    /**
     * Checks to make sure that there is still a task running
     * @return true if there is a task running, false otherwise
     */
    public boolean hasTasks() {
        for(Task task : tasks) {
            if(task.status == Status.RUNNING) {
                return true;
            }
        }

        return false;
    }

    /**
     * Main run function. Loops until all tasks are aborted or terminated
     */
    public void run() {
        while(hasTasks()) {
            // Add items to queue if it is still running and not already there (ie blocked)
            for(Task task : tasks) {
                if(!processQueue.contains(task) && task.status != Status.ABORTED && task.status != Status.TERMINATED){
                    processQueue.add(task);
                }
            }

            // Process each tasks in the queue
            for(Task task : processQueue) {
                if (task.status == Status.RUNNING) {
                    Activity act = task.activities.get(task.cycleNum);

                    if (act.type == ActivityType.REQUEST) {
                        // Resource numbers start from 1 (as per the input)
                        Resource resource = resources.get(act.resourceNum - 1);

                        // Add resource if possible
                        if (!resource.addResourceToTask(task, act.unitSize)){
                            task.isBlocked = true;
                            task.blockCount++;
                            // Add current activity into next slot to revisit in next run
                            task.activities.add(task.cycleNum + 1, act);
                        } else {
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
                    } else if(act.type == ActivityType.INITIATE) {
                        task.cycleNum++;
                    } else if(act.type == ActivityType.COMPUTE) {
                        // Initialize compute time
                        if(task.computeTime == 0) {
                            task.computeTime = act.resourceNum;
                        }
                        task.computeTime--;
                        // Must redo compute action until completed
                        if(task.computeTime != 0) {
                            task.activities.add(task.cycleNum + 1, act);
                        }

                        task.cycleNum++;
                    }
                }
            }

            // Add any released resources at the end of the cycle
            processResources();

            int taskCount = 0;
            // Check for deadlock and abort tasks appropriately
            while(deadlocked()) {
                Task t = tasks.get(taskCount);

                while(t.status != Status.RUNNING) {
                    taskCount++;
                    t = tasks.get(taskCount);
                }

                abortTask(t);
                taskCount++;
            }

            ArrayList<Task> toRemove = new ArrayList<>();

            // Only remove un-blocked tasks so that blocked tasks can get checked first
            for(Task t : processQueue) {
                if(!t.isBlocked || t.status != Status.RUNNING)
                    toRemove.add(t);
            }

            processQueue.removeAll(toRemove);
        }

        printDetails();
    }

    /**
     * Prints the final details of the resource manager
     */
    public void printDetails() {
        int cycleSum = 0;
        int waitSum = 0;

        System.out.println("\t\tFIFO");
        // Print details of each process and sum runs
        for(int i = 0; i < tasks.size(); i++) {
            System.out.print("Task " + (i+1) + "\t\t");
            Task t = tasks.get(i);
            t.printDetails();

            if(t.status != Status.ABORTED) {
                cycleSum += tasks.get(i).cycleNum;
                waitSum += t.blockCount;
            }
        }
        double pct = (double)waitSum/cycleSum * 100;
        System.out.println("total \t\t" + cycleSum + "\t" + waitSum + "\t " + pct + "%\n");
    }

    /**
     * Checks if the current state is deadlocked
     * @return true if a deadlock is present, false otherwise
     */
    public boolean deadlocked() {
        if(!hasTasks()) {
            return false;
        }

        for(Task task : tasks) {
            // If there exists a non-blocked task that is running, then there is no deadlock
            if(!task.isBlocked && task.status == Status.RUNNING) {
                return false;
            }
        }

        return true;
    }

    /**
     * Aborts the given task and returns resources to manager
     * @param t the task to be aborted
     */
    public void abortTask(Task t) {
        t.status = Status.ABORTED;

        for(Resource r : t.resourceHas.keySet()) {
            // Removes all resources from a given task
            r.removeResourceFromTask(t, -1);
        }

        // Ensure that resources are processed and added back to the manager
        processResources();

        // Check if tasks can be unblocked
        for(Task task : tasks) {
            if (task.isBlocked) {
                task.isBlocked = false;
                for (Resource r : task.resourceNeeds.keySet()) {
                    int units = task.resourceNeeds.get(r);
                    if (r.unitsLeft < units) {
                        task.isBlocked = true;
                    }
                }
            }
        }
    }

    /**
     * Call the processUnits() method on each resource to return released resources to the manager
     */
    public void processResources() {
        for(Resource r : resources) {
            r.processUnits();
        }
    }
}
