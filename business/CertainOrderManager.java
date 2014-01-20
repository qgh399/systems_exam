package business;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import utils.InvalidWorkflowException;
import utils.OrderProcessingException;
import utils.SupplyChainConstants;
import utils.SupplyChainLogger;

public class CertainOrderManager implements OrderManager{

	private static CertainOrderManager orderManager;
	private ExecutorService exec;
	private ConcurrentHashMap<Integer, List<StepStatus>> workFlowStatusMap;
	private AtomicInteger workFlowID;
	private HashMap<Integer, String> itemSupplierAddressMap;
	private SupplyChainLogger logger;
	
	public CertainOrderManager()
	{
		exec = Executors.newFixedThreadPool(SupplyChainConstants.NUMBER_OF_ORDER_MANAGER_THREADS);
		workFlowID = new AtomicInteger(1);
		workFlowStatusMap = new ConcurrentHashMap<Integer, List<StepStatus>>();
	}
	
	public void initializeItemSupplierMappings(HashMap<Integer, String> itemSupplierAddressMap) {
		this.itemSupplierAddressMap = itemSupplierAddressMap;
	}

	public static CertainOrderManager getInstance() { 
		if (orderManager != null) {
			return orderManager;
		}
		else {
			orderManager = new CertainOrderManager();
		}
		return orderManager;
	}
	
	@Override
	public int registerOrderWorkflow(List<OrderStep> steps) throws OrderProcessingException {
		/* 
		 * Validate input
		 */
		for (OrderStep step : steps)
		{		
			int supplierId = step.getSupplierId();
			if (!itemSupplierAddressMap.containsKey(supplierId))
				throw new OrderProcessingException("Step malformed, no item supplier with ID: " + supplierId);
		}
		
		// durable logging call
		if (logger != null)
			logger.log(steps);
		
		// An atomic integer is used to represent the ID for each workflow
		// to make sure that different threads accessing the same OrderManager
		// don't create workflows with the same IDs.
		int id = workFlowID.getAndIncrement();
		int workFlowSize = steps.size();
		initializeWorkFlowStatusList(id, workFlowSize);
		
		List<Future<StepStatus>> results = new ArrayList<Future<StepStatus>>(workFlowSize);
		for (OrderStep step : steps)
		{
			OrderStepTask task = null;
			try {
				task = new OrderStepTask(step, itemSupplierAddressMap.get(step.getSupplierId()));
			} catch (Exception e) {
				throw new OrderProcessingException("Problems starting up item supplier proxy");
			}
			results.add(exec.submit(task));
		}
		
		waitForItemSupplierUpdates(id, results);
		return id;
	}

	private void waitForItemSupplierUpdates(int id, List<Future<StepStatus>> results) throws OrderProcessingException {
		List<StepStatus> workFlowStatusList = workFlowStatusMap.get(id);
		for (int i = 0; i < results.size(); i++)
		{
			try {
				// A blocking call waiting to see if the step was successful
				workFlowStatusList.set(i, results.get(i).get());
			} catch (InterruptedException e) {
				throw new OrderProcessingException("Error when waiting for item supplier updates", e);
			} catch (ExecutionException e) {
				throw new OrderProcessingException("Error when waiting for item supplier updates", e);
			}
		}
	}

	private void initializeWorkFlowStatusList(int id, int size) {
		// Initialize the workflow status s.t. all steps are REGISTERED
		List<StepStatus> workFlowStatusList = new ArrayList<StepStatus>(
				Collections.nCopies(size, StepStatus.REGISTERED));
		workFlowStatusMap.put(id, workFlowStatusList);
	}

	@Override
	public List<StepStatus> getOrderWorkflowStatus(int orderWorkflowId) throws InvalidWorkflowException {
		if (!workFlowStatusMap.containsKey(orderWorkflowId))
			throw new InvalidWorkflowException("No workflow ID: " + orderWorkflowId + " found");
		return workFlowStatusMap.get(orderWorkflowId);
	}

	public void initializeLogger(SupplyChainLogger logger) {
		this.logger = logger;
	}

}
