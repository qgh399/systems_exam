package workload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;

import com.acertainsupplychain.business.CertainOrderManager;
import com.acertainsupplychain.business.ItemQuantity;
import com.acertainsupplychain.business.OrderStep;

public class OrderManagerWorker implements Callable<WorkerResult> {

	private CertainOrderManager orderManager;
	private WorkloadConfiguration config;
	private Random rand = new Random();
	private int port;
	
	public OrderManagerWorker(int port, WorkloadConfiguration config) {
		this.port = port;
		this.orderManager = CertainOrderManager.getInstance();
		this.config = config;
		
		HashMap<Integer, String> itemSupplierAddressMap = new HashMap<Integer, String>();
		itemSupplierAddressMap.put(port, config.getServerAddress());
		orderManager.initializeItemSupplierMappings(itemSupplierAddressMap);
	}

	@Override
	public WorkerResult call() throws Exception {
		int count = 1;
		long startTimeInNanoSecs = 0;
		long endTimeInNanoSecs = 0;
		int successfulInteractions = 0;
		long timeForRunsInNanoSecs = 0;
		
		float randomFloat;
		
		// Warm-up
		while (count++ <= config.getNumWarmupRuns()) {
			randomFloat = rand.nextFloat() * 100f;
			operationDistribution(); // Only one method in OrderManager sends requests to ItemSupplier
		}
		
		count = 1;
		startTimeInNanoSecs = System.nanoTime();
		while (count++ <= config.getNumRuns()) {
			randomFloat = rand.nextFloat() * 100f;
			if(operationDistribution())
				successfulInteractions++;
			
		}
		endTimeInNanoSecs = System.nanoTime();
		timeForRunsInNanoSecs += (endTimeInNanoSecs - startTimeInNanoSecs);
		return new WorkerResult(successfulInteractions, timeForRunsInNanoSecs, config.getNumRuns());
	}

	private boolean operationDistribution() {
		try 
		{
			List<OrderStep> workflow = new ArrayList<OrderStep>();
			List<ItemQuantity> step;
			for (int i = 0; i < config.getNumStepsPerWorkflow(); i++) {
				step = new ArrayList<ItemQuantity>();
				for (int j = 0; j < config.getNumItemsPerStep(); j++) {
					int randomItemId = rand.nextInt(100) + 1;
					int randomQuantity = rand.nextInt(10) + 1;
					step.add(new ItemQuantity(randomItemId, randomQuantity));
				}
				workflow.add(new OrderStep(port, step));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
