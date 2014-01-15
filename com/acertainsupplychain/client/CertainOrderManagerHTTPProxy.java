package com.acertainsupplychain.client;

import java.util.List;

import com.acertainsupplychain.business.OrderManager;
import com.acertainsupplychain.business.OrderStep;
import com.acertainsupplychain.utils.InvalidWorkflowException;
import com.acertainsupplychain.utils.OrderProcessingException;

public class CertainOrderManagerHTTPProxy implements OrderManager {

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
