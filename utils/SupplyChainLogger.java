package utils;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import business.ItemQuantity;
import business.OrderStep;

public class SupplyChainLogger {
	private PrintWriter writer;
	private DateFormat dateFormat;
	
	public SupplyChainLogger(String logFilePath) throws OrderProcessingException {
		dateFormat = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss] ");
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
			writer.println(dateFormat.format(new Date()) + stepLogLine);
		}
		writer.flush();
	}
	
	public void log(OrderStep step) {
		String result = stepToLogLine(step);
		writer.println(dateFormat.format(new Date()) +  result.trim());
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
