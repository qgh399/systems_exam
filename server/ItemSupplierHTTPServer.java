package server;

import java.util.HashSet;
import java.util.Set;

import com.acertainsupplychain.business.CertainItemSupplier;

public class ItemSupplierHTTPServer {
	
	private static Set<Integer> supplierItemIds;
	private static CertainItemSupplier itemSupplier;
	
	public static void main(String[] args) {
		int port = 8083;
		ItemSupplierHTTPMessageHandler handler = new ItemSupplierHTTPMessageHandler();
		initializeItemSupplierData(port);
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
		
		itemSupplier = CertainItemSupplier.getInstance();
		itemSupplier.initializeItems(supplierItemIds);
	}
	
	
}
