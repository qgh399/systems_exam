package com.acertainsupplychain.client;

import java.util.List;
import java.util.Set;

import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.ByteArrayBuffer;

import com.acertainsupplychain.business.ItemQuantity;
import com.acertainsupplychain.business.ItemSupplier;
import com.acertainsupplychain.business.OrderStep;
import com.acertainsupplychain.utils.InvalidItemException;
import com.acertainsupplychain.utils.OrderProcessingException;
import com.acertainsupplychain.utils.SupplyChainUtiliy;

public class CertainItemSupplierHTTPProxy implements ItemSupplier{

	private HttpClient client;
	private String serverAddress;
	
	public CertainItemSupplierHTTPProxy(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	@Override
	public void executeStep(OrderStep step) throws OrderProcessingException {
		String stepXmlString = SupplyChainUtiliy.serializeObjectToXMLString(step);
		Buffer requestContent = new ByteArrayBuffer(stepXmlString);
		
		String urlString = serverAddress + "/EXECUTE";
		ContentExchange exchange = new ContentExchange();
		exchange.setURL(urlString);
		exchange.setMethod("POST");
		exchange.setRequestContent(requestContent);
		
		SupplyChainUtiliy.sendAndRecv(client, exchange);
	}

	@Override
	public List<ItemQuantity> getOrdersPerItem(Set<Integer> itemIds) throws InvalidItemException {
		// TODO Auto-generated method stub
		return null;
	}

}
