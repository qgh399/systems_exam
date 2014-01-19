package workload;

public class WorkloadConfiguration {
	
	private String serverAddress;
	private int numItems;
	private int numWarmupRuns;
	private int numRuns;
	private float percentWriteOperation;
	private int numItemsPerStep;

	public WorkloadConfiguration(String serverAddress, int numItems,
			int numWarmupRuns, int numRuns, float percentWriteOperation, int numItemsPerStep) {
		this.serverAddress = serverAddress;
		this.numItems = numItems;
		this.numWarmupRuns = numWarmupRuns;
		this.numRuns = numRuns;
		this.percentWriteOperation = percentWriteOperation;
		this.setNumItemsPerStep(numItemsPerStep);
	}

	public String getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public int getNumItems() {
		return numItems;
	}

	public void setNumItems(int numItems) {
		this.numItems = numItems;
	}

	public int getNumWarmupRuns() {
		return numWarmupRuns;
	}

	public void setNumWarmupRuns(int numWarmupRuns) {
		this.numWarmupRuns = numWarmupRuns;
	}

	public int getNumRuns() {
		return numRuns;
	}

	public void setNumRuns(int numRuns) {
		this.numRuns = numRuns;
	}

	public float getPercentWriteOperation() {
		return percentWriteOperation;
	}

	public void setPercentWriteOperation(float percentWriteOperation) {
		this.percentWriteOperation = percentWriteOperation;
	}

	public int getNumItemsPerStep() {
		return numItemsPerStep;
	}

	public void setNumItemsPerStep(int numItemsPerStep) {
		this.numItemsPerStep = numItemsPerStep;
	}

}
