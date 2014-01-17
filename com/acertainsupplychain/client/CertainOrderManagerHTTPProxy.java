package com.acertainsupplychain.client;

import java.io.IOException;
import java.util.List;

import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.ByteArrayBuffer;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import com.acertainsupplychain.business.OrderManager;
import com.acertainsupplychain.business.OrderStep;
import com.acertainsupplychain.utils.InvalidWorkflowException;
import com.acertainsupplychain.utils.OrderProcessingException;
import com.acertainsupplychain.utils.SupplyChainConstants;
import com.acertainsupplychain.utils.SupplyChainMessageTag;
import com.acertainsupplychain.utils.SupplyChainUtiliy;

public class CertainOrderManagerHTTPProxy implements OrderManager {

	private HttpClient client;
	private String serverAddress;
	
	public String getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}
	
	public CertainOrderManagerHTTPProxy() throws Exception {
		this.client = new HttpClient();
		client.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
		client.setMaxConnectionsPerAddress(SupplyChainConstants.CLIENT_MAX_CONNECTION_ADDRESS);
		client.setThreadPool(new QueuedThreadPool(SupplyChainConstants.CLIENT_MAX_THREADSPOOL_THREADS));
		client.setTimeout(SupplyChainConstants.CLIENT_MAX_TIMEOUT_MILLISECS);
		client.start();
	}

	@Override
	public int registerOrderWorkflow(List<OrderStep> steps) throws OrderProcessingException {
		String orderStepsXmlString = SupplyChainUtiliy.serializeObjectToXMLString(steps);
		Buffer requestContent = new ByteArrayBuffer(orderStepsXmlString);
		
		String urlString = serverAddress + "/" + SupplyChainMessageTag.REGISTER;
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
