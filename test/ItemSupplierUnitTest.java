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

public class ItemSupplierUnitTest {
	
	private static CertainItemSupplier itemSupplier;
	private static Set<Integer> supplierItemIds;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		itemSupplier = CertainItemSupplier.getInstance();
		supplierItemIds = new HashSet<Integer>();
		
		supplierItemIds.add(101);
		supplierItemIds.add(102);
		supplierItemIds.add(103);
		
		itemSupplier.initializeItems(supplierItemIds);
	}

	@Test
	public void orderingBadItemsThrowsException() {
		/* 
		 * Check that items with ID not supported by the supplier get rejected 
		 */
		ItemQuantity wrongItem = new ItemQuantity(201, 5);
		List<ItemQuantity> wrongItemQuantities = new ArrayList<ItemQuantity>();
		wrongItemQuantities.add(wrongItem);
		
		OrderStep wrongStep = new OrderStep(1, wrongItemQuantities);
		
		boolean exceptionWasThrown = false;
		try {
			itemSupplier.executeStep(wrongStep);
		}
		catch (OrderProcessingException ex) {
			exceptionWasThrown = true;
		}
		assertTrue(exceptionWasThrown);
		
		/* 
		 * Check that items with negative quantity get rejected 
		 */
		wrongItem = new ItemQuantity(101, -5);
		wrongItemQuantities = new ArrayList<ItemQuantity>();
		wrongItemQuantities.add(wrongItem);
		
		wrongStep = new OrderStep(1, wrongItemQuantities);
		
		exceptionWasThrown = false;
		try {
			itemSupplier.executeStep(wrongStep);
		}
		catch (OrderProcessingException ex) {
			exceptionWasThrown = true;
		}
		assertTrue(exceptionWasThrown);
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
			itemSupplier.executeStep(step);
		} catch (OrderProcessingException e) {
			e.printStackTrace();
			fail();
		}
		
		List<ItemQuantity> ordersPerItem = null;
		try {
			ordersPerItem = itemSupplier.getOrdersPerItem(supplierItemIds);
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
			itemSupplier.executeStep(step);
		} catch (OrderProcessingException e) {
			e.printStackTrace();
			fail();
		}
		
		ordersPerItem = null;
		try {
			ordersPerItem = itemSupplier.getOrdersPerItem(supplierItemIds);
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
