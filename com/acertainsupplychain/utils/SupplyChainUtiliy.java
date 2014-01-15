package com.acertainsupplychain.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpExchange;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

public final class SupplyChainUtiliy {
	
	/**
	 * Serializes an object to an xml string
	 * 
	 * @param object
	 * @return
	 */
	public static String serializeObjectToXMLString(Object object) {
		String xmlString;
		XStream xmlStream = new XStream(new StaxDriver());
		xmlString = xmlStream.toXML(object);
		return xmlString;
	}

	/**
	 * De-serializes an xml string to object
	 * 
	 * @param xmlObject
	 * @return
	 */
	public static Object deserializeXMLStringToObject(String xmlObject) {
		Object dataObject = null;
		XStream xmlStream = new XStream(new StaxDriver());
		dataObject = xmlStream.fromXML(xmlObject);
		return dataObject;
	}

	public static Object sendAndRecv(HttpClient client, ContentExchange exchange) 
			throws OrderProcessingException {
		try {
			client.send(exchange);
		} catch (IOException e) {
			throw new OrderProcessingException(
					"Request sending failed.", e);
		}
		
		int exchangeState;
		try {
			exchangeState = exchange.waitForDone();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			throw new OrderProcessingException(
					"Request sending failed.", e);
		}
		
		if (exchangeState == HttpExchange.STATUS_COMPLETED)
		{
			String responseXMLstring;
			try {
				responseXMLstring = exchange.getResponseContent().trim();
			} catch (UnsupportedEncodingException e) {
				throw new OrderProcessingException(
						"Response XML deserialization failed.", e);
			}
			Object response = deserializeXMLStringToObject(responseXMLstring);
			return response;
		}
		 
		else if (exchangeState == HttpExchange.STATUS_EXCEPTED) {
			throw new OrderProcessingException(
					"Request exception.");
		} else if (exchangeState == HttpExchange.STATUS_EXPIRED) {
			throw new OrderProcessingException(
					"Request timeout.");
		} else {
			throw new OrderProcessingException(
					"Request unknown failure.");
		}
		
	}
}
