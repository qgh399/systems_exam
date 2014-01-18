package server;

import com.acertainsupplychain.business.CertainOrderManager;
import com.acertainsupplychain.utils.OrderProcessingException;
import com.acertainsupplychain.utils.SupplyChainLogger;

public class OrderManagerHTTPServer {
	
	public static void main(String[] args) throws OrderProcessingException {
		int port = 8081;
		String logFilePath = "orderManager" + port + "Log.txt";
		SupplyChainLogger logger = new SupplyChainLogger(logFilePath);
		CertainOrderManager.getInstance().initializeLogger(logger);
		
		OrderManagerHTTPMessageHandler handler = new OrderManagerHTTPMessageHandler();
		if (SupplyChainHTTPServerUtility.createServer(8081, handler)) {
			;
		}
	}

}
