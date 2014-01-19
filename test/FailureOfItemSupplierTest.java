package test;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import com.acertainsupplychain.client.CertainOrderManagerHTTPProxy;

public class FailureOfItemSupplierTest {

	private static CertainOrderManagerHTTPProxy globalClient;
	private static String serverAddress = "http://localhost:8081";
	
	@BeforeClass
	public static void setUpBeforeClass() {
		/*
		 * We assume before running these tests that the GlobalClientTest.java
		 * has been run (and all assumptions to run that test hold). Then after
		 * running that test we shut down the item supplier at localhost:8083
		 * to simulate a failure of the component. Only by then we run these test.
		 */
		try {
			globalClient = new CertainOrderManagerHTTPProxy();
			globalClient.setServerAddress(serverAddress);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	

}
