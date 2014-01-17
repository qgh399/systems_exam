package com.acertainsupplychain.client;

import java.util.List;
import java.util.Set;

import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.ByteArrayBuffer;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import com.acertainsupplychain.business.ItemQuantity;
import com.acertainsupplychain.business.ItemSupplier;
import com.acertainsupplychain.business.OrderStep;
import com.acertainsupplychain.utils.InvalidItemException;
import com.acertainsupplychain.utils.OrderProcessingException;
import com.acertainsupplychain.utils.SupplyChainConstants;
import com.acertainsupplychain.utils.SupplyChainMessageTag;
import com.acertainsupplychain.utils.SupplyChainUtiliy;

public class CertainItemSupplierHTTPProxy implements ItemSupplier{

	private HttpClient client;
	private String serverAddress;
	
	public CertainItemSupplierHTTPProxy(String serverAddress) throws Exception {
		this.serverAddress = serverAddress;
		
		this.client = new HttpClient();
		client.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
		client.setMaxConnectionsPerAddress(SupplyChainConstants.CLIENT_MAX_CONNECTION_ADDRESS);
		client.setThreadPool(new QueuedThreadPool(SupplyChainConstants.CLIENT_MAX_THREADSPOOL_THREADS));
		client.setTimeout(SupplyChainConstants.CLIENT_MAX_TIMEOUT_MILLISECS);
		client.start();
	}

	@Override
	public void executeStep(OrderStep step) throws OrderProcessingException {
		String stepXmlString = SupplyChainUtiliy.serializeObjectToXMLString(step);
		Buffer requestContent = new ByteArrayBuffer(stepXmlString);
		
		String urlString = serverAddress + "/" + SupplyChainMessageTag.EXECUTE;
		ContentExchange exchange = new ContentExchange();
		exchange.setURL(urlString);
		exchange.setMethod("POST");
		exchange.setRequestContent(requestContent);
		
		SupplyChainUtiliy.sendAndRecv(client, exchange);
	}

	@Override
	public List<ItemQuantity> getOrdersPerItem(Set<Integer> itemIds) throws InvalidItemException {
		String itemIdsXmlString = SupplyChainUtiliy.serializeObjectToXMLString(itemIds);
		Buffer requestContent = new ByteArrayBuffer(itemIdsXmlString);
		
		String urlString = serverAddress + "/" + SupplyChainMessageTag.ORDERSPERITEM;
		ContentExchange exchange = new ContentExchange();
		exchange.setURL(urlString);
		exchange.setMethod("POST");
		exchange.setRequestContent(requestContent);
		
		List<ItemQuantity> response = null;
		try {
			response = (List<ItemQuantity>) SupplyChainUtiliy.sendAndRecv(client, exchange);
		} catch (OrderProcessingException e) {
			throw (InvalidItemException) e;
		}
		
		return response;
	}

}
