package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.BeforeClass;
import org.junit.Test;

import com.acertainsupplychain.business.CertainItemSupplier;
import com.acertainsupplychain.business.ItemQuantity;
import com.acertainsupplychain.business.OrderStep;
import com.acertainsupplychain.utils.InvalidItemException;
import com.acertainsupplychain.utils.OrderProcessingException;
import com.acertainsupplychain.utils.SupplyChainConstants;

public class ItemSupplierAtomicityTest {

	private static CertainItemSupplier itemSupplier;
	private static Set<Integer> supplierItemIds;
	private static ExecutorService exec;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		itemSupplier = CertainItemSupplier.getInstance();
		supplierItemIds = new HashSet<Integer>();
		
		supplierItemIds.add(101);
		supplierItemIds.add(102);
		
		itemSupplier.initializeItems(supplierItemIds);
		
		exec = Executors.newFixedThreadPool(2);
	}
	
	@Test
	public void testAtomicity() {
		ItemQuantity itemQ1 = new ItemQuantity(101, 1);
		List<ItemQuantity> items1 = new ArrayList<ItemQuantity>();
		items1.add(itemQ1);
		OrderStep step1 = new OrderStep(1, items1);
		
		ItemQuantity itemQ2 = new ItemQuantity(102, 1);
		List<ItemQuantity> items2 = new ArrayList<ItemQuantity>();
		items2.add(itemQ2);
		OrderStep step2 = new OrderStep(1, items2);
		
		int repeats = 1000;
		
		exec.submit(new ExecuteStepRunnable(step1, repeats));
		exec.submit(new ExecuteStepRunnable(step2, repeats));
		try {
			exec.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			fail();
		}
		
		List<ItemQuantity> results = null;
		try {
			results = itemSupplier.getOrdersPerItem(supplierItemIds);
		} catch (InvalidItemException e) {
			fail();
		}
		
		for(ItemQuantity itemQ : results) {
			assertEquals(1000, itemQ.getQuantity());
		}
		
		
	}
	
	private class ExecuteStepRunnable implements Runnable {

		private OrderStep step;
		private int repeats;
		
		public ExecuteStepRunnable(OrderStep step, int repeats) {
			this.step = step;
			this.repeats = repeats;
		}

		@Override
		public void run() {
			for (int i = 0; i < repeats; i++) {
				try {
					CertainItemSupplier.getInstance().executeStep(step);
				} catch (OrderProcessingException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	
}
