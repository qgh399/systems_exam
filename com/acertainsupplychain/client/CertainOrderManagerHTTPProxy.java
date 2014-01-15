package com.acertainsupplychain.client;

import java.io.IOException;
import java.util.List;

import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.ByteArrayBuffer;

import com.acertainsupplychain.business.OrderManager;
import com.acertainsupplychain.business.OrderStep;
import com.acertainsupplychain.utils.InvalidWorkflowException;
import com.acertainsupplychain.utils.OrderProcessingException;
import com.acertainsupplychain.utils.SupplyChainUtiliy;

public class CertainOrderManagerHTTPProxy implements OrderManager {

	private HttpClient client;
	private String serverAddress;
	
	@Override
	public int registerOrderWorkflow(List<OrderStep> steps) throws OrderProcessingException {
		String orderStepsXmlString = SupplyChainUtiliy.serializeObjectToXMLString(steps);
		Buffer requestContent = new ByteArrayBuffer(orderStepsXmlString);
		
		String urlString = serverAddress + "/REGISTER";
		ContentExchange exchange = new ContentExchange();
		exchange.setURL(urlString);
		exchange.setMethod("POST");
		exchange.setRequestContent(requestContent);
		
		int workflowId = (Integer) SupplyChainUtiliy.sendAndRecv(client, exchange);
		
		return workflowId;
	}

	@Override
	public List<StepStatus> getOrderWorkflowStatus(int orderWorkflowId)
			throws InvalidWorkflowException {
		// TODO Auto-generated method stub
		return null;
	}

}
