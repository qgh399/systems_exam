package test;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import com.acertainsupplychain.business.ItemQuantity;
import com.acertainsupplychain.client.CertainItemSupplierHTTPProxy;
import com.acertainsupplychain.utils.InvalidItemException;

public class FailureOfOrderManagerTest {

	private static CertainItemSupplierHTTPProxy localClient1;
	private static String serverAddress1 = "http://localhost:8083";
	private static Set<Integer> supplierItemIds1;
	private static CertainItemSupplierHTTPProxy localClient2;
	private static String serverAddress2 = "http://localhost:8084";
	private static Set<Integer> supplierItemIds2;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		/*
		 * We assume before running these tests that the GlobalClientTest.java
		 * has been run (and all assumptions to run that test hold). Then after
		 * running that test we shut down the order manager (localhost:8081)
		 * to simulate a failure of the component. Only by then we run these tests.
		 */
		try {
			localClient1 = new CertainItemSupplierHTTPProxy(serverAddress1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		supplierItemIds1 = new HashSet<Integer>();
		supplierItemIds1.add(101);
		supplierItemIds1.add(102);
		supplierItemIds1.add(103);
		
		
		try {
			localClient2 = new CertainItemSupplierHTTPProxy(serverAddress2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		supplierItemIds2 = new HashSet<Integer>();
		supplierItemIds2.add(201);
		supplierItemIds2.add(202);
		supplierItemIds2.add(203);
	}
	
	@Test
	public void itemSuppliersWorkIfOrderManagerFails() {
		
		List<ItemQuantity> ordersPerItem1 = null;
		try {
			ordersPerItem1 = localClient1.getOrdersPerItem(supplierItemIds1);
		} catch (InvalidItemException e) {
			e.printStackTrace();
			fail();
		}
		
		for (ItemQuantity itemQ : ordersPerItem1) {
			if (itemQ.getItemId() == 101)
				assertEquals(20, itemQ.getQuantity());
			if (itemQ.getItemId() == 102)
				assertEquals(22, itemQ.getQuantity());
			if (itemQ.getItemId() == 103)
				assertEquals(24, itemQ.getQuantity());
		}
		
		List<ItemQuantity> ordersPerItem2 = null;
		try {
			ordersPerItem2 = localClient2.getOrdersPerItem(supplierItemIds2);
		} catch (InvalidItemException e) {
			e.printStackTrace();
			fail();
		}
		
		for (ItemQuantity itemQ : ordersPerItem2) {
			if (itemQ.getItemId() == 201)
				assertEquals(10, itemQ.getQuantity());
			if (itemQ.getItemId() == 202)
				assertEquals(11, itemQ.getQuantity());
			if (itemQ.getItemId() == 203)
				assertEquals(12, itemQ.getQuantity());
		}
	}

}
