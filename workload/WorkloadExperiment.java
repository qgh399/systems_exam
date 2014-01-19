package workload;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import server.ItemSupplierHTTPMessageHandler;
import server.SupplyChainHTTPServerUtility;

import com.acertainsupplychain.business.CertainItemSupplier;

public class WorkloadExperiment {
	
	private static Set<Integer> supplierItemIds;
	private static CertainItemSupplier itemSupplier = CertainItemSupplier.getInstance();
	
	public static void main(String[] args) throws Exception {
		int numItems = 100;
		final int port = 8083;
		int numLocalClients = 10;
		int numOrderManagers = 10;
		int numConcurrentThreads = numLocalClients + numOrderManagers;
		String serverAddress = "http://localhost:" + port;
		int numWarmupRuns = 20;
		int numRuns = 40;
		float percentWriteOperation = 80f; // 80% chance of a write operation
		int numItemsPerStep = 5;
		int numStepsPerWorkflow = 3;
		
		WorkloadConfiguration config = new WorkloadConfiguration(serverAddress, numItems, 
				numWarmupRuns, numRuns, percentWriteOperation, numItemsPerStep, numStepsPerWorkflow);
		
		System.out.println("Starting server ...");
		startItemSupplier(numItems, port);
		System.out.println("Server started.");
		
		List<WorkerResult> workerRunResults = new ArrayList<WorkerResult>();
		List<Future<WorkerResult>> runResults = new ArrayList<Future<WorkerResult>>();
		
		ExecutorService exec = Executors.newFixedThreadPool(numConcurrentThreads);
		
		System.out.println("Starting local client threads ...");
		for (int i = 0; i < numLocalClients; i++) {
			LocalClientWorker task = new LocalClientWorker(config);
			runResults.add(exec.submit(task));
		}
		System.out.println("Local client threads started.");
		
		System.out.println("Starting order manager threads ...");
		for (int i = 0; i < numOrderManagers; i++) {
			OrderManagerWorker task = new OrderManagerWorker(port, config);
			runResults.add(exec.submit(task));
		}
		System.out.println("Order manager threads started.");
		
		System.out.println("Waiting for results ...");
		for (Future<WorkerResult> futureRunResult : runResults) {
			WorkerResult runResult = futureRunResult.get(); // blocking call
			workerRunResults.add(runResult);
		}
		System.out.println("Results ready.");
		
		exec.shutdownNow();
		System.out.println("#### Reporting ####");
		reportMetric(workerRunResults);
		
	}

	private static void reportMetric(List<WorkerResult> workerRunResults) {
		for (WorkerResult result : workerRunResults) {
			System.out.println(result.getSuccessfulInteractions());
			System.out.println(result.getTimeForRunsInNanoSecs());
		}
	}

	private static void startItemSupplier(int numItems, final int port) {
		supplierItemIds = new HashSet<Integer>();
		for (int i = 1; i <= numItems; i++) {
			supplierItemIds.add(i);
		}
		
		itemSupplier.initializeItems(supplierItemIds);
		final ItemSupplierHTTPMessageHandler handler = new ItemSupplierHTTPMessageHandler();
		(new Thread() { 
			public void run() {
				SupplyChainHTTPServerUtility.createServer(port, handler); 
			}
		}).start();
	}
}
