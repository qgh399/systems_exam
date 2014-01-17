package server;

import java.util.HashSet;
import java.util.Set;

import com.acertainsupplychain.business.CertainItemSupplier;

public class ItemSupplierHTTPServer {
	
	private static Set<Integer> supplierItemIds;
	private static CertainItemSupplier itemSupplier;
	
	public static void main(String[] args) {
		int port = 8082;
		ItemSupplierHTTPMessageHandler handler = new ItemSupplierHTTPMessageHandler();
		initializeItemSupplierData(port);
		if (SupplyChainHTTPServerUtility.createServer(port, handler)) {
			;
		}
	}

	private static void initializeItemSupplierData(int port) {
		if (port == 8082) {
			supplierItemIds = new HashSet<Integer>();
			supplierItemIds.add(101);
			supplierItemIds.add(102);
			supplierItemIds.add(103);
		}
		
		itemSupplier = CertainItemSupplier.getInstance();
		itemSupplier.initializeItems(supplierItemIds);
	}
	
	
}
