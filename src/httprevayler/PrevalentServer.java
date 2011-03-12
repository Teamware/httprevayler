package httprevayler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.SessionIdManager;
import org.eclipse.jetty.server.SessionManager;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.session.HashSessionIdManager;
import org.eclipse.jetty.server.session.HashSessionManager;
import org.eclipse.jetty.server.session.SessionHandler;

public class PrevalentServer {

	private static ApplicationServlet _applicationServlet;

	public static void startRunning(Object application) throws Exception {
		int serverPort = Integer.parseInt(System.getProperty("server.port", "8888"));
		
		_applicationServlet = new ApplicationServlet(application);
		AbstractHandler handler = new AbstractHandler() { @Override public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
			System.out.println(target);
			baseRequest.setHandled(true);
			_applicationServlet.service(request, response);
		}};
		
		Server server = new Server();
		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setPort(serverPort);
		server.addConnector(connector);

		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setDirectoriesListed(false);
		resourceHandler.setWelcomeFiles(new String[] { "index.html" });

		resourceHandler.setResourceBase("./war");

		// FIXME: JEB SocialAuth
		SessionIdManager idManager = new HashSessionIdManager();
	    SessionManager sessionManager = new HashSessionManager();
	    SessionHandler sessionHandler = new SessionHandler(sessionManager);
	    server.setHandler(handler);
	    sessionManager.setIdManager(idManager);
	    sessionManager.setSessionHandler(sessionHandler);
		
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { sessionHandler, resourceHandler, handler });
		server.setHandler(handlers);
		
		
		server.start();
		server.join();
	}

}
