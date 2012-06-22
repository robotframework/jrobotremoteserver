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
package org.robotframework.remoteserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.SortedMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.apache.xmlrpc.webserver.XmlRpcServlet;
import org.robotframework.remoteserver.library.RemoteLibrary;
import org.robotframework.remoteserver.xmlrpc.ReflectiveHandlerMapping;
import org.robotframework.remoteserver.xmlrpc.TypeFactory;

public class RemoteServerServlet extends XmlRpcServlet {
    private static final long serialVersionUID = -7981676271855172976L;
    private static String page = null;

    @Override
    protected XmlRpcHandlerMapping newXmlRpcHandlerMapping() throws XmlRpcException {
	ReflectiveHandlerMapping map = new ReflectiveHandlerMapping();
	map.addHandler("keywords", ServerMethods.class);
	map.removePrefixes();
	this.getXmlRpcServletServer().setTypeFactory(new TypeFactory(this.getXmlRpcServletServer()));
	return map;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	Context.setPort(req.getServerPort());
	super.service(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	resp.setContentType("text/html");
	String body = getPage();
	resp.setContentLength(body.length());
	PrintWriter out = resp.getWriter();
	out.print(body);
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
	    SortedMap<Integer, RemoteLibrary> map = Context.getRemoteServer().getLibraryMap();
	    for (Integer port : map.keySet()) {
		sb.append("<TR><TD>");
		sb.append(port.toString());
		sb.append("</TD><TD>");
		sb.append(StringEscapeUtils.escapeHtml(map.get(port).getName()));
		sb.append("</TD></TR>");
	    }
	    sb.append("</TABLE></BODY></HTML>");
	    page = sb.toString();
	    return page;
	}
    }
}
