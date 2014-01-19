package com.acertainsupplychain.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.acertainsupplychain.utils.InvalidItemException;
import com.acertainsupplychain.utils.OrderProcessingException;
import com.acertainsupplychain.utils.SupplyChainLogger;

public class CertainItemSupplier implements ItemSupplier{

	private static CertainItemSupplier itemSupplier;
	private HashMap<Integer, Integer> itemQuantityMap;
	private SupplyChainLogger logger;
	
	public CertainItemSupplier() {
		itemQuantityMap = new HashMap<Integer, Integer>();
	}
	
	public synchronized static CertainItemSupplier getInstance() {
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
		/* 
		 * Validate input
		 */
		Set<Integer> allowedItemIds = itemQuantityMap.keySet();
		for (ItemQuantity item : step.getItems()) {
			if (!allowedItemIds.contains(item.getItemId())) {
				throw new InvalidItemException(
						"This item supplier does not supply item with ID: " + item.getItemId());
			}
			if (item.getQuantity() < 1) {
				throw new InvalidItemException(
						"Cannot order " + item.getQuantity() + " amount of items");
			}
		}
		
		// durable logging call
		if (logger != null)
			logger.log(step);
		
		/*
		 * Increment the quantity of ordered items
		 */
		for (ItemQuantity item : step.getItems()) { 
			int newQuantity = itemQuantityMap.get(item.getItemId()) + item.getQuantity();
			itemQuantityMap.put(item.getItemId(), newQuantity);
		}
	}

	@Override
	public synchronized List<ItemQuantity> getOrdersPerItem(Set<Integer> itemIds)
			throws InvalidItemException {
		
		List<ItemQuantity> ordersPerItem = new ArrayList<ItemQuantity>();
		Set<Integer> allowedItemIds = itemQuantityMap.keySet();
		
		for (int itemId : itemIds) {
			if (!allowedItemIds.contains(itemId)) {
				throw new InvalidItemException(
						"This item supplier does not supply item with ID: " + itemIds);
			}
			
			ordersPerItem.add(new ItemQuantity(itemId, itemQuantityMap.get(itemId)));
		}
		
		return ordersPerItem;
	}

	public synchronized void initializeItems(Set<Integer> supplierItemIds) {
		for (int itemId : supplierItemIds) {
			itemQuantityMap.put(itemId, 0);
		}
	}

	public void initializeLogger(SupplyChainLogger logger) {
		this.logger = logger;
	}

}
