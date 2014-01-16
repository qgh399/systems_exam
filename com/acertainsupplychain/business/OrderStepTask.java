package com.acertainsupplychain.business;

import java.util.concurrent.Callable;

import com.acertainsupplychain.business.OrderManager.StepStatus;
import com.acertainsupplychain.client.CertainItemSupplierHTTPProxy;
import com.acertainsupplychain.utils.OrderProcessingException;

public class OrderStepTask implements Callable<StepStatus>{

	private OrderStep step;
	private CertainItemSupplierHTTPProxy proxy;
	
	public OrderStepTask(OrderStep step, String address) {
		this.step = step;
		this.proxy = new CertainItemSupplierHTTPProxy(address);
	}

	@Override
	public StepStatus call() {
		try {
			proxy.executeStep(step);
		} catch (OrderProcessingException e) {
			return StepStatus.FAILED;
		}
		return StepStatus.SUCCESSFUL;
	}

}
