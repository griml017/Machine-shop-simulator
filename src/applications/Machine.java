package applications;

import applications.Job;
import dataStructures.LinkedQueue;

public class Machine {
    LinkedQueue jobQ; // queue of waiting jobs for this machine
    int changeTime; // machine change-over time
    int totalWait; // total delay at this machine
    int numTasks; // number of tasks processed on this machine
    Job activeJob; // job currently active on this machine

    public Machine() {
        jobQ = new LinkedQueue();
    }
}