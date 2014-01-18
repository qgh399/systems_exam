package com.acertainsupplychain.utils;

import java.io.PrintWriter;
import java.util.List;

import com.acertainsupplychain.business.ItemQuantity;
import com.acertainsupplychain.business.OrderStep;

public class SupplyChainLogger {
	private String logFilePath;
	private PrintWriter writer;

	public synchronized void log(List<OrderStep> steps) {
		String stepLogLine = null;
		for (OrderStep step : steps)
		{
			stepLogLine = stepToLogLine(step);
			writer.println(stepLogLine);
		}
	}

	public String stepToLogLine(OrderStep step) {
		String result = "";
		result += step.getSupplierId() + " : ";
		for (ItemQuantity itemQ : step.getItems()) {
			result += itemQ.getItemId() + "(" + itemQ.getQuantity() + ") ";
		}
		return result.trim();
	}
	
}
