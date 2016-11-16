import java.util.ArrayList;

/**
 * Created by rossfreeman on 11/14/16.
 */
public class Banker extends FIFO {


    public Banker() {
        tasks = new ArrayList<>();
        processQueue = new ArrayList<>();

        for(Task task : Task.tasks) {
            tasks.add(task.duplicate());
        }

        resources = Resource.duplicate();
    }

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
                        int claim = task.resourceClaim.get(resource);

                        if(task.resourceHas.containsKey(resource)) {
                            claim -= task.resourceHas.get(resource);
                        }

                        if (!isSafe(task, resource, act.unitSize) ){
                            task.isBlocked = true;
                            task.blockCount++;
                            task.activities.add(task.cycleNum + 1, act);
                        } else if(act.unitSize > claim) {
                            abortTask(task);
                        }
                        else {
                            resource.addResourceToTask(task, act.unitSize);
                            task.isBlocked = false;
                        }
                        task.cycleNum++;
                    } else if (act.type == ActivityType.RELEASE) {
                        Resource resource = resources.get(act.resourceNum - 1);
                        resource.removeResourceFromTask(task, act.unitSize);
                        task.cycleNum++;
                    } else if (act.type == ActivityType.TERMINATE) {
                        terminateTask(task);
                        task.cycleNum++;
                    } else if(act.type == ActivityType.INITIATE) {
                        Resource r = resources.get(act.resourceNum-1);
                        if(act.unitSize > r.totalUnits) {
                            abortTask(task);
                        } else {
                            task.resourceClaim.put(r, act.unitSize);
                            r.totalClaim += act.unitSize;
                            task.cycleNum++;
                        }
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

            ArrayList<Task> toRemove = new ArrayList<>();

            for(Task t : processQueue) {
                if(!t.isBlocked || t.status != Status.RUNNING)
                    toRemove.add(t);
            }

            processQueue.removeAll(toRemove);
        }

        printDetails();
    }

    public void terminateTask(Task t) {
        for(Resource r : t.resourceClaim.keySet()) {
            int size = t.resourceClaim.get(r);
            r.totalClaim -= size;
        }

        t.status = Status.TERMINATED;
    }

    public boolean isSafe(Task t, Resource r, int units) {
        for(Task task : tasks) {
            if (task.status == Status.RUNNING) {
                int unitsNeeded = task.resourceClaim.get(r);

                if (task.resourceHas.containsKey(r)) {
                    unitsNeeded -= task.resourceHas.get(r);
                }

                if (task == t) {
                    unitsNeeded -= units;
                }

                if (r.unitsLeft - units >= unitsNeeded) {
                    return true;
                }
            }
        }

        return false;
    }

    public void abortTask(Task t) {
        t.status = Status.ABORTED;

        for(Resource r : t.resourceHas.keySet()) {
            r.removeResourceFromTask(t, -1);
        }

        processResources();

    }

    public void printDetails() {
        int cycleSum = 0;
        int waitSum = 0;

        System.out.println("\t\tBanker's");
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
}
