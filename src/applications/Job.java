package applications;

import applications.Task;
import dataStructures.LinkedQueue;

public class Job {
    private LinkedQueue taskQ; // this job's tasks
    private int length; // sum of scheduled task times
    private int arrivalTime; // arrival time at current queue
    private int id; // job identifier

    public Job(int theId) {
        setId(theId);
        setTaskQ(new LinkedQueue());
        // length and arrivalTime have default value 0
    }

    public void addTask(int theMachine, int theTime) {
        getTaskQ().put(new Task(theMachine, theTime));
    }

    
    // remove next task of job and return its time also update length
     
    public int removeNextTask() {
        int theTime = ((Task) getTaskQ().remove()).getTime();
        setLength(getLength() + theTime);
        return theTime;
    }

	public LinkedQueue getTaskQ() {
		return taskQ;
	}

	public void setTaskQ(LinkedQueue taskQ) {
		this.taskQ = taskQ;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(int arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
}