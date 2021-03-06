package workload;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;

import business.ItemQuantity;
import business.OrderStep;
import client.CertainItemSupplierHTTPProxy;

public class LocalClientWorker implements Callable<WorkerResult> {

	private CertainItemSupplierHTTPProxy localClient;
	private WorkloadConfiguration config;
	private Random rand = new Random();
	
	public LocalClientWorker(WorkloadConfiguration config) throws Exception {
		this.config = config;
		localClient = new CertainItemSupplierHTTPProxy(config.getServerAddress());
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
			operationDistribution(randomFloat);
		}
		
		count = 1;
		startTimeInNanoSecs = System.nanoTime();
		while (count++ <= config.getNumRuns()) {
			randomFloat = rand.nextFloat() * 100f;
			if(operationDistribution(randomFloat))
				successfulInteractions++;
			
		}
		endTimeInNanoSecs = System.nanoTime();
		timeForRunsInNanoSecs += (endTimeInNanoSecs - startTimeInNanoSecs);
		return new WorkerResult(successfulInteractions, timeForRunsInNanoSecs, config.getNumRuns());
	}

	private boolean operationDistribution(float randomFloat) {
		try 
		{
			if (randomFloat < config.getPercentWriteOperation()) 
			{
				List<ItemQuantity> itemQuantities = new ArrayList<ItemQuantity>();
				for (int i = 0; i < config.getNumItemsPerStep(); i++) {
					int randomItemId = rand.nextInt(100) + 1;
					int randomQuantity = rand.nextInt(10) + 1;
					itemQuantities.add(new ItemQuantity(randomItemId, randomQuantity));
				}
				
				OrderStep step = new OrderStep(1, itemQuantities);
				
				localClient.executeStep(step);
			}
			else 
			{
				Set<Integer> itemIds = new HashSet<Integer>();
				for (int i = 0; i < config.getNumItemsPerStep(); i++) {
					int randomItemId = rand.nextInt(100) + 1;
					itemIds.add(randomItemId);
				}
				
				localClient.getOrdersPerItem(itemIds);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;	
	}

}
