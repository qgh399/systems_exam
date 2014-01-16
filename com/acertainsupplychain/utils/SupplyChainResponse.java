package com.acertainsupplychain.utils;

public class SupplyChainResponse {

	private OrderProcessingException exception = null;
	private Object response = null;
	
	public SupplyChainResponse(OrderProcessingException exception, Object response)
	{
		this.setException(exception);
		this.setResponse(response);
	}
	
	public OrderProcessingException getException() {
		return exception;
	}
	public void setException(OrderProcessingException exception) {
		this.exception = exception;
	}
	
	public Object getResponse() {
		return response;
	}
	public void setResponse(Object response) {
		this.response = response;
	}
	
	
}
