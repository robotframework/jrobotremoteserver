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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.robotframework.remoteserver.cli.CommandLineHelper;
import org.robotframework.remoteserver.logging.Jetty2Log4J;
import org.robotframework.remoteserver.servlet.RemoteServerServlet;

/**
 * Remote server for Robot Framework implemented in Java. Takes one or more test
 * libraries and exposes their methods via XML-RPC using an embedded web server.
 * 
 * @see <a
 *      href="https://github.com/ombre42/jrobotremoteserver/wiki">jrobotremoteserver
 *      wiki</a>
 * @see <a
 *      href="http://code.google.com/p/robotframework/wiki/RemoteLibrary">Remote
 *      Library wiki page</a>
 * @see <a href="http://code.google.com/p/robotframework/wiki/UserGuide">User
 *      Guide for Robot Framework</a>
 * @see <a href="http://xmlrpc.scripting.com/spec.html">XML-RPC
 *      Specification</a>
 */
public class RemoteServer {
    private static Log log = LogFactory.getLog(RemoteServer.class);
    protected Server server = new Server();
    private RemoteServerServlet servlet = new RemoteServerServlet();
    private SelectChannelConnector connector = new SelectChannelConnector();
    private boolean allowStop = true;

    public RemoteServer() {
        connector.setName("jrobotremoteserver");
        server.setConnectors(new Connector[] { connector });
        servlet.setRemoteServer(this);
        ServletContextHandler servletContextHandler = new ServletContextHandler(server, "/", false, false);
        servletContextHandler.addServlet(new ServletHolder(servlet), "/");
    }

    /**
     * @return The actual port the server's connector is listening on or -1 if
     *         it has not been opened, or -2 if it has been closed.
     */
    public Integer getLocalPort() {
        return connector.getLocalPort();
    }

    /**
     * @param port
     *            The port to listen of for connections or 0 if any available
     *            port may be used. Defaults to 0.
     */
    public void setPort(int port) {
        connector.setPort(port);
    }

    /**
     * @return whether this server allows remote stopping
     */
    public boolean getAllowStop() {
        return allowStop;
    }

    /**
     * @param allowed
     *            whether to allow stopping the server remotely
     */
    public void setAllowStop(boolean allowed) {
        allowStop = allowed;
    }

    /**
     * @return the hostname set with {@link #setHost(String)}
     */
    public String getHost() {
        return connector.getHost();
    }

    /**
     * Set the hostname of the interface to bind to. Usually not needed and
     * determined automatically. For exotic network configuration, network with
     * VPN, specifying the host might be necessary.
     * 
     * @param hostName
     *            the hostname or address representing the interface to which
     *            all connectors will bind, or null for all interfaces.
     */
    public void setHost(String hostName) {
        connector.setHost(hostName);
    }

    public static void main(String[] args) throws Exception {
        configureLogging();
        CommandLineHelper helper = new CommandLineHelper(args);
        if (helper.getHelpRequested()) {
            System.out.print(helper.getUsage());
            System.exit(0);
        } else if (helper.getError() != null) {
            System.out.println("Error: " + helper.getError());
            System.out.println();
            System.out.println(helper.getUsage());
            System.exit(1);
        }
        RemoteServer remoteServer = new RemoteServer();
        remoteServer.setPort(helper.getPort());
        for (String path : helper.getLibraryMap().keySet())
            remoteServer.putLibrary(path, helper.getLibraryMap().get(path));
        remoteServer.setAllowStop(helper.getAllowStop());
        remoteServer.setHost(helper.getHost());
        remoteServer.start();
    }

    /**
     * Map the given test library to the specified path.
     * 
     * @param clazz
     *            class of the test library
     * @param path
     *            path to map the test library to
     */
    public void putLibrary(String path, Class<?> clazz) {
        servlet.putLibrary(path, clazz);
        log.info(String.format("Mapped path %s to library %s.", path, clazz.getName()));
    }

    /**
     * A non-blocking method for stopping the remote server that allows requests
     * to complete within the given timeout before shutting down the server.
     * This method exists to allow stopping the server remotely. New connections
     * will not be accepted after calling this.
     * 
     * @param timeoutMS
     *            the milliseconds to wait for existing request to complete
     *            before stopping the server
     */
    public void stop(int timeoutMS) throws Exception {
        log.info("Robot Framework remote server stopping");
        if (timeoutMS > 0) {
            server.setGracefulShutdown(timeoutMS);
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
        } else {
            server.stop();
        }
    }

    /**
     * Stops the remote server immediately.
     * 
     * @throws Exception
     */
    public void stop() throws Exception {
        stop(0);
    }

    /**
     * Starts the remote server. Add test libraries first before calling this.
     * 
     * @throws Exception
     */
    public void start() throws Exception {
        log.info("Robot Framework remote server starting");
        server.start();
        log.info(String.format("Robot Framework remote server started on port %d.", getLocalPort()));
    }

    /**
     * Configures logging systems used by <tt>RemoteServer</tt> and its
     * dependencies. Specifically,
     * <ul>
     * <li>Configure Log4J to log to the console</li>
     * <li>Set Log4J's log level to INFO</li>
     * <li>Redirect the Jetty's logging to Log4J</li>
     * <li>Set Jakarta Commons Logging to log to Log4J</li>
     * </ul>
     * This is convenient if you do not want to configure the logging yourself.
     * This will only affect future instances of
     * {@link org.eclipse.jetty.util.log.Logger} and
     * {@link org.apache.commons.logging.Log}, so this should be called as early
     * as possible.
     */
    public static void configureLogging() {
        Logger root = Logger.getRootLogger();
        root.removeAllAppenders();
        BasicConfigurator.configure();
        root.setLevel(Level.INFO);
        org.eclipse.jetty.util.log.Log.setLog(new Jetty2Log4J());
        LogFactory.releaseAll();
        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log",
                "org.apache.commons.logging.impl.Log4JLogger");
        log = LogFactory.getLog(RemoteServer.class);
    }

}
