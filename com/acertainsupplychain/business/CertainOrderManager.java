package com.acertainsupplychain.business;

import java.util.List;

import com.acertainsupplychain.utils.InvalidWorkflowException;
import com.acertainsupplychain.utils.OrderProcessingException;

public class CertainOrderManager implements OrderManager{

	private static CertainOrderManager orderManager;
	
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
	public int registerOrderWorkflow(List<OrderStep> steps)
			throws OrderProcessingException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<StepStatus> getOrderWorkflowStatus(int orderWorkflowId)
			throws InvalidWorkflowException {
		// TODO Auto-generated method stub
		return null;
	}

}
