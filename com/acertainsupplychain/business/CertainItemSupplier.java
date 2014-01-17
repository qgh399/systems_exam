package com.acertainsupplychain.business;

import java.util.List;
import java.util.Set;

import com.acertainsupplychain.utils.InvalidItemException;
import com.acertainsupplychain.utils.OrderProcessingException;

public class CertainItemSupplier implements ItemSupplier{

	private static CertainItemSupplier itemSupplier;
	
	public static CertainItemSupplier getInstance() {
		if (itemSupplier != null) {
			return itemSupplier;
		}
		else {
			itemSupplier = new CertainItemSupplier();
		}
		return itemSupplier;
	}
	
	@Override
	public synchronized void executeStep(OrderStep step) throws OrderProcessingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public synchronized List<ItemQuantity> getOrdersPerItem(Set<Integer> itemIds)
			throws InvalidItemException {
		// TODO Auto-generated method stub
		return null;
	}

	

}
