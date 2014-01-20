package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import utils.InvalidItemException;
import utils.OrderProcessingException;
import business.CertainItemSupplier;
import business.ItemQuantity;
import business.OrderStep;
import client.CertainItemSupplierHTTPProxy;

public class LocalClientTest {
	
	private static CertainItemSupplierHTTPProxy localClient;
	private static CertainItemSupplier itemSupplier;
	private static String serverAddress = "http://localhost:8083";
	private static Set<Integer> supplierItemIds;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		/*
		 * We assume that before running these tests a item supplier server
		 * has been started on localhost:8083 and initialized with the same
		 * items as the one below (IDs 101, 102 and 103).
		 */
		try {
			localClient = new CertainItemSupplierHTTPProxy(serverAddress);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		supplierItemIds = new HashSet<Integer>();
		supplierItemIds.add(101);
		supplierItemIds.add(102);
		supplierItemIds.add(103);
		
		itemSupplier = CertainItemSupplier.getInstance();
		itemSupplier.initializeItems(supplierItemIds);
	}
	
	@Test
	public void orderingItemsIncreasesCumulativeOrders() {
		/*
		 * Place first order
		 */
		ItemQuantity itemQ1 = new ItemQuantity(101, 5);
		ItemQuantity itemQ2 = new ItemQuantity(102, 10);
		List<ItemQuantity> itemQuantities = new ArrayList<ItemQuantity>();
		itemQuantities.add(itemQ1);
		itemQuantities.add(itemQ2);
		OrderStep step = new OrderStep(1, itemQuantities);
		
		try {
			localClient.executeStep(step);
		} catch (OrderProcessingException e) {
			e.printStackTrace();
			fail();
		}
		
		List<ItemQuantity> ordersPerItem = null;
		try {
			ordersPerItem = localClient.getOrdersPerItem(supplierItemIds);
		} catch (InvalidItemException e) {
			e.printStackTrace();
			fail();
		}
		
		for (ItemQuantity itemQ : ordersPerItem) {
			if (itemQ.getItemId() == 101)
				assertEquals(5, itemQ.getQuantity());
			if (itemQ.getItemId() == 102)
				assertEquals(10, itemQ.getQuantity());
			if (itemQ.getItemId() == 103)
				assertEquals(0, itemQ.getQuantity());
		}
		
		/*
		 * Place another order
		 */
		itemQ1 = new ItemQuantity(101, 10);
		itemQ2 = new ItemQuantity(103, 3);
		itemQuantities = new ArrayList<ItemQuantity>();
		itemQuantities.add(itemQ1);
		itemQuantities.add(itemQ2);
		step = new OrderStep(1, itemQuantities);
		
		try {
			localClient.executeStep(step);
		} catch (OrderProcessingException e) {
			e.printStackTrace();
			fail();
		}
		
		ordersPerItem = null;
		try {
			ordersPerItem = localClient.getOrdersPerItem(supplierItemIds);
		} catch (InvalidItemException e) {
			e.printStackTrace();
			fail();
		}
		
		for (ItemQuantity itemQ : ordersPerItem) {
			if (itemQ.getItemId() == 101)
				assertEquals(15, itemQ.getQuantity());
			if (itemQ.getItemId() == 102)
				assertEquals(10, itemQ.getQuantity());
			if (itemQ.getItemId() == 103)
				assertEquals(3, itemQ.getQuantity());
		}
	}

}
