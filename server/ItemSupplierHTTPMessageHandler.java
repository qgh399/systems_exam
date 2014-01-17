package server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.acertainsupplychain.business.CertainItemSupplier;
import com.acertainsupplychain.business.OrderStep;
import com.acertainsupplychain.utils.OrderProcessingException;
import com.acertainsupplychain.utils.SupplyChainMessageTag;
import com.acertainsupplychain.utils.SupplyChainResponse;
import com.acertainsupplychain.utils.SupplyChainUtiliy;

public class ItemSupplierHTTPMessageHandler extends AbstractHandler{

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) 
			throws IOException, ServletException {
		
		String requestURI;
		
		response.setContentType("text/html;charset=utf-8"); 
		response.setStatus(HttpServletResponse.SC_OK);
		requestURI = request.getRequestURI();
		
		SupplyChainMessageTag messageTag = SupplyChainUtiliy.convertURItoMessageTag(requestURI);
		
		switch (messageTag) {
			case EXECUTE:
				String xml = SupplyChainUtiliy.extractPOSTDataFromRequest(request);
				OrderStep step = (OrderStep) SupplyChainUtiliy.deserializeXMLStringToObject(xml);
				SupplyChainResponse supplyChainResponse = new SupplyChainResponse();
				try 
				{
					CertainItemSupplier.getInstance().executeStep(step);
				}
				catch (OrderProcessingException e) {
					supplyChainResponse.setException(e);
				}
				
				response.getWriter().println(
						SupplyChainUtiliy.serializeObjectToXMLString(supplyChainResponse));
				
				break;
				
			default:
				System.out.println("Unhandled message tag");
				break;
		}
		
		baseRequest.setHandled(true);
		
	}

}
