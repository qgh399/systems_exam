package server;

public class OrderManagerHTTPServer {
	
	public static void main(String[] args) {
		OrderManagerHTTPMessageHandler handler = new OrderManagerHTTPMessageHandler();
		if (SupplyChainHTTPServerUtility.createServer(8081, handler)) {
			;
		}
	}

}
