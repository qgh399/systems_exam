package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.acertainsupplychain.client.CertainOrderManagerHTTPProxy;
import com.acertainsupplychain.utils.InvalidWorkflowException;
import com.acertainsupplychain.utils.OrderProcessingException;
import com.acertainsupplychain.business.CertainOrderManager;
import com.acertainsupplychain.business.OrderManager.StepStatus;
import com.acertainsupplychain.business.OrderStep;
import com.acertainsupplychain.business.ItemQuantity;

public class GlobalClientTest {

	private static CertainOrderManagerHTTPProxy globalClient;
	private static String serverAddress = "http://localhost:8081";
	
	@BeforeClass
	public static void setUpBeforeClass() {
		/*
		 * We assume before running this test that an order manager
		 * server has been started on localhost:8081 and two item
		 * supplier servers on localhost:8083 and localhost:8084
		 * with the corresponding items initialized on each one
		 * (see ItemSupplierHTTPServer.java).	
		 */
		try {
			globalClient = new CertainOrderManagerHTTPProxy();
			globalClient.setServerAddress(serverAddress);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void workflowRegistrationSucceedes() {
		List<OrderStep> workflow = new ArrayList<OrderStep>();
		
		List<ItemQuantity> order1 = new ArrayList<ItemQuantity>();
		ItemQuantity itemQ1 = new ItemQuantity(101, 10);
		ItemQuantity itemQ2 = new ItemQuantity(102, 11);
		ItemQuantity itemQ3 = new ItemQuantity(103, 12);
		order1.add(itemQ1);
		order1.add(itemQ2);
		order1.add(itemQ3);
		workflow.add(new OrderStep(8083, order1));
		
		List<ItemQuantity> order2 = new ArrayList<ItemQuantity>();
		ItemQuantity itemQ4 = new ItemQuantity(201, 10);
		ItemQuantity itemQ5 = new ItemQuantity(202, 11);
		ItemQuantity itemQ6 = new ItemQuantity(203, 12);
		order2.add(itemQ4);
		order2.add(itemQ5);
		order2.add(itemQ6);
		workflow.add(new OrderStep(8084, order2));
		
		int workflowId = -1;
		try {
			workflowId = globalClient.registerOrderWorkflow(workflow);
		} catch (OrderProcessingException e) {
			e.printStackTrace();
			fail();
		}
		
		List<StepStatus> stepStatus = null;
		try {
			stepStatus = globalClient.getOrderWorkflowStatus(workflowId);
		} catch (InvalidWorkflowException e) {
			e.printStackTrace();
			fail();
		}
		
		for (StepStatus st : stepStatus) {
			assertEquals(StepStatus.SUCCESSFUL, st);
		}
	}
	
	@Test
	public void workflowIncludingNonexistingItemSupplierFails() {
		List<OrderStep> workflow = new ArrayList<OrderStep>();
		
		/*
		 * Include one good order in workflow
		 */
		List<ItemQuantity> orderToGoodSupplier = new ArrayList<ItemQuantity>();
		ItemQuantity itemQ1 = new ItemQuantity(101, 10);
		ItemQuantity itemQ2 = new ItemQuantity(102, 11);
		ItemQuantity itemQ3 = new ItemQuantity(103, 12);
		orderToGoodSupplier.add(itemQ1);
		orderToGoodSupplier.add(itemQ2);
		orderToGoodSupplier.add(itemQ3);
		workflow.add(new OrderStep(8083, orderToGoodSupplier));
		
		/*
		 * Include an order to Satan that should cause an exception
		 */
		List<ItemQuantity> orderToSatan = new ArrayList<ItemQuantity>();
		ItemQuantity itemQ4 = new ItemQuantity(201, 10);
		ItemQuantity itemQ5 = new ItemQuantity(202, 11);
		ItemQuantity itemQ6 = new ItemQuantity(203, 12);
		orderToSatan.add(itemQ4);
		orderToSatan.add(itemQ5);
		orderToSatan.add(itemQ6);
		workflow.add(new OrderStep(666, orderToSatan));
		
		boolean exceptionWasThrown = false;
		try {
			globalClient.registerOrderWorkflow(workflow);
		} catch (OrderProcessingException e) {
			exceptionWasThrown = true;
		}
		assertTrue(exceptionWasThrown);
	}
}
