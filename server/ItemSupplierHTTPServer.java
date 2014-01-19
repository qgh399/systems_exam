package server;

import java.util.HashSet;
import java.util.Set;

import com.acertainsupplychain.business.CertainItemSupplier;
import com.acertainsupplychain.utils.OrderProcessingException;
import com.acertainsupplychain.utils.SupplyChainLogger;

public class ItemSupplierHTTPServer {
	
	private static Set<Integer> supplierItemIds;
	private static CertainItemSupplier itemSupplier;
	
	public static void main(String[] args) throws OrderProcessingException {
		int port = 8088;
		
		String logFilePath = "itemSupplier" + port + "Log.txt";
		SupplyChainLogger logger = new SupplyChainLogger(logFilePath);
		itemSupplier = CertainItemSupplier.getInstance();
		itemSupplier.initializeLogger(logger);
		initializeItemSupplierData(port);
		
		ItemSupplierHTTPMessageHandler handler = new ItemSupplierHTTPMessageHandler();
		if (SupplyChainHTTPServerUtility.createServer(port, handler)) {
			;
		}
	}

	private static void initializeItemSupplierData(int port) {
		if (port == 8083) {
			supplierItemIds = new HashSet<Integer>();
			supplierItemIds.add(101);
			supplierItemIds.add(102);
			supplierItemIds.add(103);
		}
		if (port == 8084) {
			supplierItemIds = new HashSet<Integer>();
			supplierItemIds.add(201);
			supplierItemIds.add(202);
			supplierItemIds.add(203);
		}
		if (port == 8088) { 
			int numItems = 100;
			supplierItemIds = new HashSet<Integer>();
			for (int i = 1; i <= numItems; i++) {
				supplierItemIds.add(i);
			}
		}
		
		itemSupplier.initializeItems(supplierItemIds);
	}
	
	
}
