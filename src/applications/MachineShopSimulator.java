/** machine shop simulation */

package applications;

import utilities.MyInputStream;
import dataStructures.LinkedQueue;
import exceptions.MyInputException;

public class MachineShopSimulator {
    
    
    public static final String atLeastOne = "number of machines and jobs must be >= 1";
    public static final String atLeastZero = "change-over time must be >= 0";
    public static final String oneTask = "each job must have >= 1 task";
    public static final String badMachineTask = "bad machine number or task time";
    
    private static int timeNow; // current time
    private static int numMachines; // number of machines
    private static int numJobs; // number of jobs
    private static EventList eList; // pointer to event list
    private static Machine[] machine; // array of machines
    private static int largeTime; // all machines finish before this

    // methods
    /**
     * move theJob to machine for its next task
     * 
     * @return false iff no next task
     */
    static boolean nextMachine(Job theJob) {
        if (theJob.getTaskQ().isEmpty()) {// no next task
            System.out.println("Job " + theJob.getId() + " has completed at "
                    + timeNow + " Total wait was " + (timeNow - theJob.getLength()));
            return false;
        } else {// theJob has a next task
                // get machine for next task
            int p = ((Task) theJob.getTaskQ().getFrontElement()).getMachine();
            // put on machine p's wait queue
            machine[p].jobQ.put(theJob);
            theJob.setArrivalTime(timeNow);
            // if p idle, schedule immediately
            if (eList.nextEventTime(p) == largeTime) {// machine is idle
                changeState(p);
            }
            return true;
        }
    }

    /**
     * change the state of theMachine
     * 
     * @return last job run on this machine
     */
    static Job changeState(int theMachine) {// Task on theMachine has finished, schedule next one.
        Job lastJob;
        if (machine[theMachine].activeJob == null) {// in idle or change-over state
            lastJob = changeOver(theMachine);
        } else {// task has just finished on machine[theMachine]
                // schedule change-over time
            lastJob = machine[theMachine].activeJob;
            machine[theMachine].activeJob = null;
            eList.setFinishTime(theMachine, timeNow
                    + machine[theMachine].changeTime);
        }

        return lastJob;
    }

	private static Job changeOver(int theMachine) {
		Job lastJob;
		lastJob = null;
		if (machine[theMachine].jobQ.isEmpty()) 
		    eList.setFinishTime(theMachine, largeTime);
		else {// take job off the queue and work on it
		    machine[theMachine].activeJob = (Job) machine[theMachine].jobQ
		            .remove();
		    machine[theMachine].totalWait += timeNow
		            - machine[theMachine].activeJob.getArrivalTime();
		    machine[theMachine].numTasks++;
		    int t = machine[theMachine].activeJob.removeNextTask();
		    eList.setFinishTime(theMachine, timeNow + t);
		}
		return lastJob;
	}

    /** input machine shop data */
    static void inputData() {
        // define the input stream to be the standard input stream
        MyInputStream keyboard = new MyInputStream();

        System.out.println("Enter number of machines and jobs");
        numMachines = keyboard.readInteger();
        numJobs = keyboard.readInteger();
        if (numMachines < 1 || numJobs < 1)
            throw new MyInputException(atLeastOne);

        // create event and machine queues
        eList = new EventList(numMachines, largeTime);
        machine = new Machine[numMachines + 1];
        for (int i = 1; i <= numMachines; i++)
            machine[i] = new Machine();

        System.out.println("Enter change-over times for machines");
        for (int j = 1; j <= numMachines; j++) {
            int ct = keyboard.readInteger();
            if (ct < 0)
                throw new MyInputException(atLeastZero);
            machine[j].changeTime = ct;
        }

        // input the jobs
        
        inputJob(keyboard);
    }

	private static void inputJob(MyInputStream keyboard) {
		Job job;
		for (int i = 1; i <= numJobs; i++) {
            System.out.println("Enter number of tasks for job " + i);
            int tasks = keyboard.readInteger(); // number of tasks
            int firstMachine = 0; // machine for first task
            if (tasks < 1)
                throw new MyInputException(oneTask);

            
            job = new Job(i);
            System.out.println("Enter the tasks (machine, time)"
                    + " in process order");
            firstMachine = processOrder(keyboard, job, tasks, firstMachine);
            machine[firstMachine].jobQ.put(job);
        }
	}

	private static int processOrder(MyInputStream keyboard, Job job, int tasks,
			int firstMachine) {
		for (int j = 1; j <= tasks; j++) {
		    int theMachine = keyboard.readInteger();
		    int theTaskTime = keyboard.readInteger();
		    if (theMachine < 1 || theMachine > numMachines
		            || theTaskTime < 1)
		        throw new MyInputException(badMachineTask);
		    if (j == 1)
		        firstMachine = theMachine; 
		    job.addTask(theMachine, theTaskTime); 
		}
		return firstMachine;
	}

    // load first jobs onto each machine 
    static void startShop() {
        for (int p = 1; p <= numMachines; p++)
            changeState(p);
    }

    //process all jobs to completion
    static void simulate() {
        while (numJobs > 0) {
            int nextToFinish = eList.nextEventMachine();
            timeNow = eList.nextEventTime(nextToFinish);
            Job theJob = changeState(nextToFinish);
            // move theJob to its next machine, decrement numJobs if theJob has finished
            if (theJob != null && !nextMachine(theJob))
                numJobs--;
        }
    }

    /** output wait times at machines */
    static void outputStats() {
        System.out.println("Finish time = " + timeNow);
        for (int p = 1; p <= numMachines; p++) {
            System.out.println("Machine " + p + " completed "
                    + machine[p].numTasks + " tasks");
            System.out.println("The total wait time was "
                    + machine[p].totalWait);
            System.out.println();
        }
    }

    /** entry point for machine shop simulator */
    public static void main(String[] args) {
        largeTime = Integer.MAX_VALUE;
        /*
         * It's vital that we (re)set this to 0 because if the simulator is called
         * multiple times (as happens in the acceptance tests), because timeNow
         * is static it ends up carrying over from the last time it was run. I'm
         * not convinced this is the best place for this to happen, though.
         */
        timeNow = 0;
        inputData(); // get machine and job data
        startShop(); // initial machine loading
        simulate(); // run all jobs through shop
        outputStats(); // output machine wait times
    }
}
