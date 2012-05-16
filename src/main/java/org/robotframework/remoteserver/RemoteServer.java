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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

public class RemoteServer {
    private static final Log log = LogFactory.getLog(RemoteServer.class);
    private static Server server = new Server();
    private static Map<Integer, IRemoteLibrary> libraryMap = new TreeMap<Integer, IRemoteLibrary>();
    private static boolean shutdownAllowed = true;
    private static List<SelectChannelConnector> connectors = new ArrayList<SelectChannelConnector>();

    public static boolean getIsShutdownAllowed() {
	return shutdownAllowed;
    }

    public static void main(String[] args) throws Exception {
	configureLogger();
    }

    public static void addLibrary(String className, int port) {
	Object library;
	try {
	    library = Class.forName(className).newInstance();
	} catch (Exception e) {
	    throw new RuntimeException(String.format(
		    "Unable to create an instance of %s", className));
	}
	addLibrary(library, port);
    }

    public static void addLibrary(Object library, int port) {
	if (!server.isStopped())
	    throw new RuntimeException(
		    "Cannot add a library once the server is started");
	if (libraryMap.containsKey(port))
	    throw new RuntimeException(String.format(
		    "A library was already added for port %d", port));
	IRemoteLibrary remoteLibrary = RemoteLibraryFactory
		.newRemoteLibrary(library);
	libraryMap.put(port, remoteLibrary);
	SelectChannelConnector connector = new SelectChannelConnector();
	connector.setPort(port);
	connector.setThreadPool(new QueuedThreadPool(20));
	connector.setName("jrobotremotesever");
	connectors.add(connector);
    }

    protected static Map<Integer, IRemoteLibrary> getLibraryMap() {
	return libraryMap;
    }

    protected static IRemoteLibrary getLibrary() {
	return libraryMap.get(RemoteServerServlet.getPort());
    }

    public static void stop() throws Exception {
	server.stop();
    }

    public static void start() throws Exception {
	if (connectors.isEmpty())
	    throw new RuntimeException(
		    "Cannot start the server without adding a library first");
	server.setConnectors(connectors.toArray(new Connector[] {}));
	ServletContextHandler servletContextHandler = new ServletContextHandler(
		server, "/", true, false);
	servletContextHandler.addServlet(RemoteServerServlet.class, "/");
	log.info("Robot Framework remote server starting");
	server.start();
	connectors.clear();
    }

    private static boolean isMainClass() {
	StackTraceElement[] stack = Thread.currentThread().getStackTrace();
	StackTraceElement main = stack[stack.length - 1];
	return main.getClassName().equals(RemoteServer.class.getName());
    }

    private static void configureLogger() {
	if (!isMainClass())
	    return;
	Logger root = Logger.getRootLogger();
	if (!root.getAllAppenders().hasMoreElements()) {
	    BasicConfigurator.configure();
	    root.setLevel(Level.INFO);
	    // TODO: configure Jetty's logging
	}
    }
}
