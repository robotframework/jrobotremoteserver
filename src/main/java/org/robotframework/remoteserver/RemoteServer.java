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
import java.util.SortedMap;
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

/**
 * Remote server for Robot Framework implemented in Java. Takes one or more user libraries and exposes their methods via
 * XML-RPC using an embedded web server.
 * 
 * @see <a href="http://code.google.com/p/robotframework/wiki/RemoteLibrary">Remote Library wiki page</a>
 * @see <a href="http://code.google.com/p/robotframework/wiki/UserGuide">User Guide</a> for Robot Framework
 * @see <a href="http://xmlrpc.scripting.com/spec.html">XML-RPC Specification</a>
 */
public class RemoteServer {
    private static Log log = LogFactory.getLog(RemoteServer.class);
    private Server server;
    private SortedMap<Integer, IRemoteLibrary> libraryMap = new TreeMap<Integer, IRemoteLibrary>();
    private boolean allowRemoteStop = true;
    private List<SelectChannelConnector> connectors = new ArrayList<SelectChannelConnector>();

    /**
     * @return whether this server allows remote stopping
     */
    public boolean getAllowRemoteStop() {
	return allowRemoteStop;
    }

    /**
     * @param allowed
     *            whether to allow stopping the server remotely
     */
    public void setAllowRemoteStop(boolean allowed) {
	allowRemoteStop = allowed;
    }

    public static void main(String[] args) throws Exception {
	// TODO: proper argument parsing
	configureLogging();
	List<String> libs = new ArrayList<String>();
	List<Integer> ports = new ArrayList<Integer>();
	for (int i = 0; i < args.length; i++) {
	    if (args[i].equals("-library")) {
		String[] parts = args[i + 1].split(":");
		libs.add(parts[0].trim());
		ports.add(new Integer(parts[1]));
		i++;
	    }
	}
	RemoteServer rs = new RemoteServer();
	for (int i = 0; i < libs.size(); i++)
	    rs.addLibrary(libs.get(i), ports.get(i));
	rs.start();
    }

    /**
     * Add the given test library to the remote server on the given port. The server must be stopped when calling this.
     * 
     * @param className
     *            the test library's class name
     * @param port
     *            port to serve the test library from
     */
    public void addLibrary(String className, int port) {
	Class<?> clazz;
	try {
	    clazz = Class.forName(className);
	} catch (Exception e) {
	    throw new RuntimeException(String.format("Unable to load class %s: %s", className, e.getMessage()), e);
	}
	addLibrary(clazz, port);
    }

    /**
     * Add the given test library to the remote server on the given port. The server must be stopped when calling this.
     * 
     * @param library
     *            class of the test library
     * @param port
     *            port to serve the test library from
     */
    public void addLibrary(Class<?> clazz, int port) {
	if (server != null && !server.isStopped())
	    throw new RuntimeException("Cannot add a library once the server is started");
	if (libraryMap.containsKey(port))
	    throw new RuntimeException(String.format("A library was already added for port %d", port));
	Object library;
	try {
	    library = clazz.newInstance();
	} catch (Exception e) {
	    throw new RuntimeException(String.format("Unable to create an instance of %s: %s", clazz.getName(), e
		    .getMessage()), e);
	}
	IRemoteLibrary remoteLibrary = RemoteLibraryFactory.newRemoteLibrary(library);
	libraryMap.put(port, remoteLibrary);
	SelectChannelConnector connector = new SelectChannelConnector();
	connector.setPort(port);
	connector.setThreadPool(new QueuedThreadPool(20));
	connector.setName("jrobotremotesever");
	connectors.add(connector);
	log.info(String.format("Added library %s", remoteLibrary.getName()));
    }

    protected SortedMap<Integer, IRemoteLibrary> getLibraryMap() {
	return libraryMap;
    }

    protected IRemoteLibrary getLibrary(Integer port) {
	return libraryMap.get(port);
    }

    protected void gracefulStop() {
	log.info("Robot Framework remote server stopping");
	server.setGracefulShutdown(2000);
	Thread stopper = new Thread() {
	    @Override
	    public void run() {
		try {
		    server.stop();
		} catch (Throwable e) {
		    log.error(String.format("Failed to stop the server: %s", e.getMessage()), e);
		}
	    }
	};
	stopper.start();
	libraryMap.clear();
    }

    /**
     * Immediately stops the remote server. This will remove all test libraries.
     * 
     * @throws Exception
     */
    public void stop() throws Exception {
	checkStarted();
	log.info("Robot Framework remote server stopping");
	try {
	    server.stop();
	    Context.removeRemoteServer(this);
	} finally {
	    libraryMap.clear();
	}
    }

    /**
     * Starts the remote server. Add test libraries first before calling this.
     * 
     * @throws Exception
     */
    public void start() throws Exception {
	if (server == null)
	    server = new Server();
	if (connectors.isEmpty())
	    throw new RuntimeException("Cannot start the server without adding a library first");
	if (!server.isStopped())
	    throw new RuntimeException("The server is starting or already started");
	Context.addRemoteServer(this, libraryMap.keySet());
	server.setConnectors(connectors.toArray(new Connector[] {}));
	ServletContextHandler servletContextHandler = new ServletContextHandler(server, "/", false, false);
	servletContextHandler.addServlet(RemoteServerServlet.class, "/");
	log.info("Robot Framework remote server starting");
	server.start();
	connectors.clear();
    }

    /**
     * Configures logging systems used by <tt>RemoteServer</tt> and its dependencies. Specifically,
     * <ul>
     * <li>Configure Log4J to log to the console</li>
     * <li>Set Log4J's log level to INFO</li>
     * <li>Redirect the Jetty's logging to Log4J</li>
     * <li>Set Jakarta Commons Logging to use log to Log4J</li>
     * </ul>
     * This is convenient if you do not want to configure the logging yourself. This will only affect future instances
     * of {@link org.eclipse.jetty.util.log.Logger} and {@link org.apache.commons.logging.Log}. This should be called as
     * early as possible.
     */
    public static void configureLogging() {
	Logger root = Logger.getRootLogger();
	root.removeAllAppenders();
	BasicConfigurator.configure();
	root.setLevel(Level.INFO);
	org.eclipse.jetty.util.log.Log.setLog(new Jetty2Log4j());
	LogFactory.releaseAll();
	LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log",
		"org.apache.commons.logging.impl.Log4JLogger");
	log = LogFactory.getLog(RemoteServer.class);
    }

    private void checkStarted() {
	if (server == null)
	    throw new RuntimeException("The server was never started");
    }
}
