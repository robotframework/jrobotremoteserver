/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.robotframework.remoteserver.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.apache.xmlrpc.webserver.XmlRpcServlet;
import org.robotframework.remoteserver.RemoteServer;
import org.robotframework.remoteserver.context.Context;
import org.robotframework.remoteserver.library.DefaultRemoteLibraryFactory;
import org.robotframework.remoteserver.library.RemoteLibrary;
import org.robotframework.remoteserver.library.RemoteLibraryFactory;
import org.robotframework.remoteserver.xmlrpc.ReflectiveHandlerMapping;
import org.robotframework.remoteserver.xmlrpc.TypeFactory;

/**
 * This servlet uses the same instance of a test library to process all requests on a given port
 */
public class RemoteServerServlet extends XmlRpcServlet implements Context {
    private static final long serialVersionUID = -7981676271855172976L;
    private static String page = null;
    private static final ThreadLocal<HttpServletRequest> request = new ThreadLocal<HttpServletRequest>();
    private RemoteServer remoteServer;
    private SortedMap<Integer, RemoteLibrary> libraryMap;

    public RemoteServerServlet(RemoteServer remoteServer, Map<Integer, Class<?>> libraryMap) {
	this.remoteServer = remoteServer;
	RemoteLibraryFactory libraryFactory = createLibraryFactory();
	this.libraryMap = new TreeMap<Integer, RemoteLibrary>();
	for (Integer port : libraryMap.keySet()) {
	    Class<?> clazz = libraryMap.get(port);
	    Object library;
	    try {
		library = clazz.newInstance();
	    } catch (Exception e) {
		throw new RuntimeException(String.format("Unable to create an instance of %s", clazz.getName()), e);
	    }
	    RemoteLibrary remoteLibrary = libraryFactory.createRemoteLibrary(library);
	    this.libraryMap.put(port, remoteLibrary);
	}
    }

    @Override
    protected XmlRpcHandlerMapping newXmlRpcHandlerMapping() throws XmlRpcException {
	ReflectiveHandlerMapping map = new ReflectiveHandlerMapping();
	map.setRequestProcessorFactoryFactory(new RemoteServerRequestProcessorFactoryFactory(this));
	map.addHandler("keywords", ServerMethods.class);
	map.removePrefixes();
	this.getXmlRpcServletServer().setTypeFactory(new TypeFactory(this.getXmlRpcServletServer()));
	return map;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	request.set(req);
	try {
	    super.service(req, resp);
	} finally {
	    request.remove();
	}
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	resp.setContentType("text/html");
	String body = getPage();
	resp.setContentLength(body.length());
	PrintWriter out = resp.getWriter();
	out.print(body);
    }

    /**
     * The request is shared so that more context, such as the client address, can be obtained
     * 
     * @return {@link HttpServletRequest} object that contains the request the client has made of the servlet
     */
    public static HttpServletRequest getRequest() {
	return request.get();
    }

    protected String getPage() {
	if (page != null)
	    return page;
	else {
	    StringBuilder sb = new StringBuilder();
	    sb.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">"
		    + "<HTML><HEAD><TITLE>jrobotremoteserver</TITLE></HEAD><BODY>"
		    + "<P>jrobotremoteserver serving:</P>"
		    + "<TABLE border='1' cellspacing='0' cellpadding='5'><TR><TH>Port</TH><TH>Library</TH></TR>");
	    for (Integer port : libraryMap.keySet()) {
		sb.append("<TR><TD>");
		sb.append(port.toString());
		sb.append("</TD><TD>");
		sb.append(StringEscapeUtils.escapeHtml(libraryMap.get(port).getName()));
		sb.append("</TD></TR>");
	    }
	    sb.append("</TABLE></BODY></HTML>");
	    page = sb.toString();
	    return page;
	}
    }

    public RemoteLibrary getLibrary() {
	return libraryMap.get(getRequest().getServerPort());
    }

    protected RemoteLibraryFactory createLibraryFactory() {
	return new DefaultRemoteLibraryFactory();
    }

    public RemoteServer getRemoteServer() {
	return remoteServer;
    }
}
