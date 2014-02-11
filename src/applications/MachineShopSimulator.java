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
    
    static int timeNow; // current time
    private static int numMachines; // number of machines
    private static int numJobs; // number of jobs
    static EventList eList; // pointer to event list
    static Machine[] machine; // array of machines
    static int largeTime; // all machines finish before this

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
                Machine.changeState(p);
            }
            return true;
        }
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
        machine = new Machine[numMachines];
        for (int i = 0; i <= numMachines; i++)
            machine[i] = new Machine();

        System.out.println("Enter change-over times for machines");
        for (int j = 0; j <= numMachines; j++) {
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
		for (int i = 0; i <= numJobs; i++) {
            System.out.println("Enter number of tasks for job " + i + 1);
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
		for (int j = 0; j <= tasks; j++) {
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
            Machine.changeState(p);
    }

    //process all jobs to completion
    static void simulate() {
        while (numJobs > 0) {
            int nextToFinish = eList.nextEventMachine();
            timeNow = eList.nextEventTime(nextToFinish);
            Job theJob = Machine.changeState(nextToFinish);
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
