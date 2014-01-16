package com.acertainsupplychain.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import com.acertainsupplychain.utils.InvalidWorkflowException;
import com.acertainsupplychain.utils.OrderProcessingException;
import com.acertainsupplychain.utils.SupplyChainConstants;

public class CertainOrderManager implements OrderManager{

	private static CertainOrderManager orderManager;
	private ExecutorService exec;
	private ConcurrentHashMap<Integer, List<StepStatus>> workFlowStatusMap;
	private AtomicInteger workFlowID;
	
	public CertainOrderManager()
	{
		exec = Executors.newFixedThreadPool(SupplyChainConstants.NUMBER_OF_ORDER_MANAGER_THREADS);
		workFlowID = new AtomicInteger(0);
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
		// TODO: Log workflow durably
		
		// An atomic integer is used to represent the ID for each workflow
		// to make sure that different threads accessing the same OrderManager
		// don't create workflows with the same IDs.
		int id = workFlowID.getAndIncrement();
		int workFlowSize = steps.size();
		initializeWorkFlowStatusList(id, workFlowSize);
		
		List<Future<StepStatus>> results = new ArrayList<Future<StepStatus>>(workFlowSize);
		for (OrderStep step : steps)
		{
			OrderStepTask task = new OrderStepTask(step);
			results.add(exec.submit(task));
		}
		
		waitForItemSupplierUpdates(id, results);
		return id;
	}

	private void waitForItemSupplierUpdates(int id, List<Future<StepStatus>> results) throws OrderProcessingException {
		List<StepStatus> workFlowStatusList = workFlowStatusMap.get(id);
		boolean stepFailed;
		for (int i = 0; i < results.size(); i++)
		{
			stepFailed = false;
			try {
				// A blocking call waiting to see if the step was successful
				workFlowStatusList.set(i, results.get(i).get());
			} catch (InterruptedException e) {
				stepFailed = true;
				throw new OrderProcessingException("Error when waiting for item supplier updates", e);
			} catch (ExecutionException e) {
				stepFailed = true;
				throw new OrderProcessingException("Error when waiting for item supplier updates", e);
			}
			
			if (stepFailed)
				workFlowStatusList.set(i, StepStatus.FAILED);
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

}
