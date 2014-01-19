package server;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.acertainsupplychain.business.CertainOrderManager;
import com.acertainsupplychain.business.OrderStep;
import com.acertainsupplychain.utils.InvalidWorkflowException;
import com.acertainsupplychain.utils.OrderProcessingException;
import com.acertainsupplychain.utils.SupplyChainMessageTag;
import com.acertainsupplychain.utils.SupplyChainResponse;
import com.acertainsupplychain.utils.SupplyChainUtility;

public class OrderManagerHTTPMessageHandler extends AbstractHandler {

	@SuppressWarnings("unchecked")
	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) 
			throws IOException, ServletException {
		
		String requestURI;
		
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		requestURI = request.getRequestURI();
		
		SupplyChainMessageTag messageTag = SupplyChainUtility.convertURItoMessageTag(requestURI);
		
		switch (messageTag) 
		{
			case REGISTER:
				String xml = SupplyChainUtility.extractPOSTDataFromRequest(request);
				List<OrderStep> steps = (List<OrderStep>) SupplyChainUtility.deserializeXMLStringToObject(xml);
				SupplyChainResponse supplyChainResponse = new SupplyChainResponse();
				try {
					supplyChainResponse.setResponse(
							CertainOrderManager.getInstance().registerOrderWorkflow(steps));
				}
				catch (OrderProcessingException e) {
					supplyChainResponse.setException(e);
				}
				
				response.getWriter().println(
						SupplyChainUtility.serializeObjectToXMLString(supplyChainResponse));
				
				break;
				
			case GETSTATUS:
				String workflowIdString = URLDecoder.decode(request.getParameter("workflowid"), "UTF-8");
				supplyChainResponse = new SupplyChainResponse();
				
				int workflowId = Integer.parseInt(workflowIdString);
				try {
					supplyChainResponse.setResponse(
							CertainOrderManager.getInstance().getOrderWorkflowStatus(workflowId));
				} catch (InvalidWorkflowException e) {
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
