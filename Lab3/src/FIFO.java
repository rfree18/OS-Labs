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
            for(Task task : tasks) {
                if(!processQueue.contains(task) && task.status != Status.ABORTED && task.status != Status.TERMINATED){
                    processQueue.add(task);
                }
            }

            for(Task task : processQueue) {
                if (task.status == Status.RUNNING) {
                    Activity act = task.activities.get(task.cycleNum);

                    if (act.type == ActivityType.REQUEST) {
                        Resource resource = resources.get(act.resourceNum - 1);
                        if (!resource.addResourceToTask(task, act.unitSize)){
                            task.isBlocked = true;
                            task.blockCount++;
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
//                        task.resourceClaim.put(resources.get(act.resourceNum-1), act.unitSize);
                        task.cycleNum++;
                    } else if(act.type == ActivityType.COMPUTE) {
                        if(task.computeTime == 0) {
                            task.computeTime = act.resourceNum;
                        }
                        task.computeTime--;

                        if(task.computeTime != 0) {
                            task.activities.add(task.cycleNum + 1, act);
                        }

                        task.cycleNum++;
                    }
                }
            }

            processResources();

            int taskCount = 0;
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

            for(Task t : processQueue) {
                if(!t.isBlocked || t.status != Status.RUNNING)
                    toRemove.add(t);
            }

            processQueue.removeAll(toRemove);
        }

        printDetails();
    }

    public void printDetails() {
        int cycleSum = 0;
        int waitSum = 0;

        System.out.println("\t\tFIFO");
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
        System.out.println("total \t\t" + cycleSum + "\t" + waitSum + "\t " + pct + "%");
    }

    public boolean deadlocked() {
        if(!hasTasks()) {
            return false;
        }

        for(Task task : tasks) {
            if(!task.isBlocked && task.status == Status.RUNNING) {
                return false;
            }
        }

        return true;
    }

    public void abortTask(Task t) {
        t.status = Status.ABORTED;

        for(Resource r : t.resourceHas.keySet()) {
            r.removeResourceFromTask(t, -1);
        }

        processResources();

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

    public void processResources() {
        for(Resource r : resources) {
            r.processUnits();
        }
    }
}
