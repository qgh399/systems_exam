package server;

import java.util.HashMap;

import utils.OrderProcessingException;
import utils.SupplyChainLogger;
import business.CertainOrderManager;

public class OrderManagerHTTPServer {
	
	public static void main(String[] args) throws OrderProcessingException {
		int port = 8081;
		
		String logFilePath = "orderManager" + port + "Log.txt";
		SupplyChainLogger logger = new SupplyChainLogger(logFilePath);
		CertainOrderManager.getInstance().initializeLogger(logger);
		
		HashMap<Integer, String> itemSupplierAddressMap = new HashMap<Integer, String>();
		itemSupplierAddressMap.put(8083, "http://localhost:8083");
		itemSupplierAddressMap.put(8084, "http://localhost:8084");
		itemSupplierAddressMap.put(8085, "http://localhost:8085");
		CertainOrderManager.getInstance().initializeItemSupplierMappings(itemSupplierAddressMap);
		
		OrderManagerHTTPMessageHandler handler = new OrderManagerHTTPMessageHandler();
		if (SupplyChainHTTPServerUtility.createServer(8081, handler)) {
			;
		}
	}

}
