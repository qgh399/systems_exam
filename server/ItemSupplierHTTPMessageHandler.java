package server;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import utils.InvalidItemException;
import utils.OrderProcessingException;
import utils.SupplyChainMessageTag;
import utils.SupplyChainResponse;
import utils.SupplyChainUtility;
import business.CertainItemSupplier;
import business.OrderStep;

public class ItemSupplierHTTPMessageHandler extends AbstractHandler{

	@SuppressWarnings("unchecked")
	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) 
			throws IOException, ServletException {
		
		String requestURI;
		
		response.setContentType("text/html;charset=utf-8"); 
		response.setStatus(HttpServletResponse.SC_OK);
		requestURI = request.getRequestURI();
		
		SupplyChainMessageTag messageTag = SupplyChainUtility.convertURItoMessageTag(requestURI);
		
		switch (messageTag) {
			case EXECUTE:
				String xml = SupplyChainUtility.extractPOSTDataFromRequest(request);
				OrderStep step = (OrderStep) SupplyChainUtility.deserializeXMLStringToObject(xml);
				SupplyChainResponse supplyChainResponse = new SupplyChainResponse();
				try 
				{
					CertainItemSupplier.getInstance().executeStep(step);
				}
				catch (OrderProcessingException e) {
					supplyChainResponse.setException(e);
				}
				
				response.getWriter().println(
						SupplyChainUtility.serializeObjectToXMLString(supplyChainResponse));
				
				break;
				
			case ORDERSPERITEM:
				xml = SupplyChainUtility.extractPOSTDataFromRequest(request);
				Set<Integer> itemIds = (Set<Integer>) SupplyChainUtility.deserializeXMLStringToObject(xml);
				supplyChainResponse = new SupplyChainResponse();
				try {
					supplyChainResponse.setResponse(
							CertainItemSupplier.getInstance().getOrdersPerItem(itemIds));
				}
				catch (InvalidItemException e) {
					supplyChainResponse.setException(e);
				}
				
				response.getWriter().println(
						SupplyChainUtility.serializeObjectToXMLString(supplyChainResponse));
				
				break;
				
			default:
				System.out.println("Unhandled message tag");
				break;
		}
		
		baseRequest.setHandled(true);
		
	}

}
