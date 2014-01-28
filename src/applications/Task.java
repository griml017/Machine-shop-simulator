package applications;

public class Task {
    private int machine;
    private int time;

    public Task(int theMachine, int theTime) {
        setMachine(theMachine);
        setTime(theTime);
    }

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public int getMachine() {
		return machine;
	}

	public void setMachine(int machine) {
		this.machine = machine;
	}
}

