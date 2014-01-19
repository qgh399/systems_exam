package workload;

public class WorkerResult {

	private int successfulInteractions;
	private long timeForRunsInNanoSecs;
	private int numRuns;
	
	
	public WorkerResult(int successfulInteractions, long timeForRunsInNanoSecs,
			int numRuns) {
		this.setSuccessfulInteractions(successfulInteractions);
		this.setTimeForRunsInNanoSecs(timeForRunsInNanoSecs);
		this.setNumRuns(numRuns);
	}


	public int getSuccessfulInteractions() {
		return successfulInteractions;
	}


	public void setSuccessfulInteractions(int successfulInteractions) {
		this.successfulInteractions = successfulInteractions;
	}


	public long getTimeForRunsInNanoSecs() {
		return timeForRunsInNanoSecs;
	}


	public void setTimeForRunsInNanoSecs(long timeForRunsInNanoSecs) {
		this.timeForRunsInNanoSecs = timeForRunsInNanoSecs;
	}


	public int getNumRuns() {
		return numRuns;
	}


	public void setNumRuns(int numRuns) {
		this.numRuns = numRuns;
	}

}
