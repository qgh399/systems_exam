package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.acertainsupplychain.business.ItemQuantity;
import com.acertainsupplychain.business.OrderStep;
import com.acertainsupplychain.utils.SupplyChainLogger;

public class SupplyChainLoggerTest {

	private static SupplyChainLogger logger;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		logger = new SupplyChainLogger();
	}
	
	@Test
	public void stepToLogLineTest() {
		List<ItemQuantity> order1 = new ArrayList<ItemQuantity>();
		ItemQuantity itemQ1 = new ItemQuantity(101, 10);
		ItemQuantity itemQ2 = new ItemQuantity(102, 11);
		ItemQuantity itemQ3 = new ItemQuantity(103, 12);
		order1.add(itemQ1);
		order1.add(itemQ2);
		order1.add(itemQ3);
		
		OrderStep step = new OrderStep(8083, order1);
		
		String stepLogString = logger.stepToLogLine(step);
		
		assertEquals("8083 : 101(10) 102(11) 103(12)", stepLogString);
	}

}
