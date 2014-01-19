package com.acertainsupplychain.utils;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

import com.acertainsupplychain.business.ItemQuantity;
import com.acertainsupplychain.business.OrderStep;

public class SupplyChainLogger {
	private PrintWriter writer;
	
	public SupplyChainLogger(String logFilePath) throws OrderProcessingException {
		try {
			this.writer = new PrintWriter(logFilePath);
		} catch (FileNotFoundException e) {
			throw new OrderProcessingException("Failed initializing logger", e);
		}
	}
	
	public SupplyChainLogger() {
		;
	}

	public synchronized void log(List<OrderStep> steps) {
		String stepLogLine = null;
		for (OrderStep step : steps)
		{
			stepLogLine = stepToLogLine(step);
			writer.println(stepLogLine);
		}
		writer.flush();
	}
	
	public void log(OrderStep step) {
		String result = stepToLogLine(step);
		writer.println(result.trim());
		writer.flush();
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
