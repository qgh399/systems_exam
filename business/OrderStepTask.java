package business;

import java.util.concurrent.Callable;

import utils.OrderProcessingException;
import business.OrderManager.StepStatus;
import client.CertainItemSupplierHTTPProxy;

public class OrderStepTask implements Callable<StepStatus>{

	private OrderStep step;
	private CertainItemSupplierHTTPProxy proxy;
	
	public OrderStepTask(OrderStep step, String address) throws Exception {
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
