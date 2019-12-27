/* Copyright 2014 Kevin Ormbrek
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
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

import java.net.InetSocketAddress;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.robotframework.remoteserver.cli.CommandLineHelper;
import org.robotframework.remoteserver.library.RemoteLibrary;
import org.robotframework.remoteserver.logging.Jetty2Log4J;
import org.robotframework.remoteserver.servlet.IllegalPathException;
import org.robotframework.remoteserver.servlet.RemoteServerServlet;

/**
 * Remote server for Robot Framework implemented in Java. Takes one or more test
 * libraries and exposes their methods via XML-RPC using an embedded web server.
 * To use a different web server, use
 * {@link org.robotframework.remoteserver.servlet.RemoteServerServlet} instead.
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
    private static int serverPort;
    private static String serverHost;
    protected Server server;
    private RemoteServerServlet servlet = new RemoteServerServlet();

    public RemoteServer() {
        this(0);
    }

    public RemoteServer(int port) {
        this(null, port);
    }

    public RemoteServer(String host, int port) {
        if (host != null) {
            this.server = new Server(new InetSocketAddress(host, port));
        } else {
            this.server = new Server(port);
        }
        ServletContextHandler servletContextHandler = new ServletContextHandler(server, "/", false, false);
        servletContextHandler.addServlet(new ServletHolder(servlet), "/");
    }

    public int getLocalPort() {
        for (Connector connector: server.getConnectors()) {
            serverPort = ((ServerConnector)connector).getLocalPort();
        }
        return serverPort;
    }

    /**
     * Returns <code>true</code> if this server allows remote stopping.
     *
     * @return <code>true</code> if this server allows remote stopping
     */
    public boolean getAllowStop() {
        return servlet.getAllowStop();
    }

    /**
     * Allow or disallow stopping the server remotely.
     *
     * @param allowed
     *            <code>true</code> to allow stopping the server remotely
     */
    public void setAllowStop(boolean allowed) {
        servlet.setAllowStop(allowed);
    }

    /**
     * Main method for command line usage.
     *
     * @param args command line arguments for remote server
     * @throws Exception in case of failure
     */
    public static void main(String[] args) throws Exception {
        configureLogging();
        CommandLineHelper helper = new CommandLineHelper(args);
        if (helper.getHelpRequested()) {
            System.out.print(helper.getUsage());
            System.exit(0);
        }
        serverPort = helper.getPort();
        serverHost = helper.getHost();
        RemoteServer remoteServer = new RemoteServer(serverHost, serverPort);
        String error = helper.getError();
        if (error == null) {
            try {
                for (String path : helper.getLibraryMap().keySet())
                    remoteServer.putLibrary(path, helper.getLibraryMap().get(path));
            } catch (IllegalPathException e) {
                error = e.getMessage();
            }
        }
        if (error != null) {
            System.out.println("Error: " + error);
            System.out.println();
            System.out.println(helper.getUsage());
            System.exit(1);
        }
        remoteServer.setAllowStop(helper.getAllowStop());
        remoteServer.start();
    }

    /**
     * Map the given test library to the specified path. Paths must:
     * <ul>
     * <li>start with a /</li>
     * <li>contain only alphanumeric characters or any of these: / - . _ ~</li>
     * <li>not end in a /</li>
     * <li>not contain a repeating sequence of /s</li>
     * </ul>
     *
     * Example: <code>putLibrary("/myLib", new MyLibrary());</code>
     *
     * @param library
     *            instance of the test library
     * @param path
     *            path to map the test library to
     * @return the previous library mapped to the path, or null if there was no
     *         mapping for the path
     */
    public RemoteLibrary putLibrary(String path, Object library) {
        RemoteLibrary oldLibrary = servlet.putLibrary(path, library);
        String name = servlet.getLibraryMap().get(path).getName();
        log.info(String.format("Mapped path %s to library %s.", path, name));
        return oldLibrary;
    }

    /**
     * Removes the library mapped to the given path if the mapping exists.
     *
     * @param path
     *            path for the library whose mapping is to be removed
     * @return the previous library associated with the path, or null if there
     *         was no mapping for the path.
     */
    public RemoteLibrary removeLibrary(String path) {
        return servlet.removeLibrary(path);
    }

    /**
     * Gets a copy of the current library map. Keys in the map are the paths and
     * the values are {@link RemoteLibrary} wrappers of the libraries being
     * served.
     *
     * @return a copy of the current library map
     */
    public Map<String, RemoteLibrary> getLibraryMap() {
        return servlet.getLibraryMap();
    }

    /**
     * A non-blocking method for stopping the remote server that allows requests
     * to complete within the given timeout before shutting down the server. New
     * connections will not be accepted after calling this.
     *
     * @param timeoutMS
     *            the milliseconds to wait for existing request to complete
     *            before stopping the server
     * @throws Exception Exception thrown if server fails to stop without timeout
     */
    public void stop(int timeoutMS) throws Exception {
        log.info("Robot Framework remote server stopping");
        if (timeoutMS > 0) {
            this.server.setStopTimeout(timeoutMS);
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
            this.server.stop();
        }
    }

    /**
     * Stops the remote server immediately.
     *
     * @throws Exception thrown when immediate stop is not possible.
     */
    public void stop() throws Exception {
        stop(0);
    }

    /**
     * Starts the remote server. Add test libraries first before calling this.
     *
     * @throws Exception when server start fails for anu reason
     */
    public void start() throws Exception {
        log.info("Robot Framework remote server starting");
        server.start();
        log.info(String.format("Robot Framework remote server started on port %d.", serverPort));
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
        Configurator.initialize(new DefaultConfiguration());
        Configurator.setRootLevel(Level.INFO);
        org.eclipse.jetty.util.log.Log.setLog(new Jetty2Log4J());
        LogFactory.releaseAll();
        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log",
                "org.apache.commons.logging.impl.Log4JLogger");
        log = LogFactory.getLog(RemoteServer.class);
    }

}
