package workload;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
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
		int port = 8083;
		int numLocalClients = 10;
		int numOrderManagers = 10;
		int numConcurrentThreads = numLocalClients + numOrderManagers;
		String serverAddress = "http://localhost:" + port;
		int numWarmupRuns = 20;
		int numRuns = 40;
		float percentWriteOperation = 80f;
		int numItemsPerStep = 5;
		
		WorkloadConfiguration config = new WorkloadConfiguration(serverAddress, numItems, 
				numWarmupRuns, numRuns, percentWriteOperation, numItemsPerStep);
		
		startItemSupplier(numItems, port);
		
		List<WorkerResult> workerRunResults = new ArrayList<WorkerResult>();
		List<Future<WorkerResult>> runResults = new ArrayList<Future<WorkerResult>>();
		
		ExecutorService exec = Executors.newFixedThreadPool(numConcurrentThreads);
		
		for (int i = 0; i < numLocalClients; i++) {
			LocalClientWorker task = new LocalClientWorker(serverAddress, config);
			runResults.add(exec.submit(task));
		}
		
		for (int i = 0; i < numOrderManagers; i++) {
			OrderManagerWorker task = new OrderManagerWorker(supplierItemIds);
			runResults.add(exec.submit(task));
		}
		
		for (Future<WorkerResult> futureRunResult : runResults) {
			WorkerResult runResult = futureRunResult.get(); // blocking call
			workerRunResults.add(runResult);
		}
		
		exec.shutdownNow();
		//reportMetric(workerRunResults);
		
	}

	private static void startItemSupplier(int numItems, int port) {
		supplierItemIds = new HashSet<Integer>();
		for (int i = 1; i <= numItems; i++) {
			supplierItemIds.add(i);
		}
		
		itemSupplier.initializeItems(supplierItemIds);
		ItemSupplierHTTPMessageHandler handler = new ItemSupplierHTTPMessageHandler();
		SupplyChainHTTPServerUtility.createServer(port, handler);
		
	}
}
