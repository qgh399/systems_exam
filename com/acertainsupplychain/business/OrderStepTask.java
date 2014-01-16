package com.acertainsupplychain.business;

import java.util.concurrent.Callable;

import com.acertainsupplychain.business.OrderManager.StepStatus;

public class OrderStepTask implements Callable<StepStatus>{

	private OrderStep step;
	
	public OrderStepTask(OrderStep step) {
		this.step = step;
	}

	@Override
	public StepStatus call() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
